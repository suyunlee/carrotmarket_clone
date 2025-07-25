package oreumi.group2.carrotClone.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import lombok.*;
import oreumi.group2.carrotClone.dto.PostDTO;
import oreumi.group2.carrotClone.model.*;
import oreumi.group2.carrotClone.repository.PostRepository;
import oreumi.group2.carrotClone.service.PostService;
import oreumi.group2.carrotClone.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    /* DI */
    @Autowired PostRepository postRepository;
    @Autowired S3Service s3Service;

    /* ID 기반 게시물 찾기 */
    @Override
    @Transactional
    public Optional<Post> findById(Long id) { return postRepository.findById(id); }

    // 게시물 등록
    @Override
    public Post createPost(User user, PostDTO postDTO, List<MultipartFile> files) {
        Post p = new Post();
        p.setUser(user);
        p.setTitle(postDTO.getTitle());
        p.setDescription(postDTO.getDescription());
        p.setPrice(postDTO.getPrice());
        p.setLocation(postDTO.getLocation());
        p.setCategory(postDTO.getCategory());
        if (files != null &&
                files.stream().anyMatch(file -> !file.isEmpty())) {
            List<Image> imageList = new ArrayList<>();
            for (MultipartFile file : files) {
                try {
                    String fileUrl = s3Service.uploadFile(file);
                    String s3Key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                    Image image = new Image(p, fileUrl, s3Key, file.getOriginalFilename());
                    imageList.add(image);
                    } catch (IOException e) {
                        throw new RuntimeException("파일 업로드 중 오류 발생", e);
                    }
            }
            p.setImages(imageList);
        }
        return postRepository.save(p);
    }

    /* 게시물 삭제 */
    @Override
    public void deletePost(Long id) {
        Optional<Post> p = postRepository.findById(id);
        if(p == null){
            throw new EntityExistsException("존재하지않는 게시물입니다.");
        }
        deleteImages(id);
        postRepository.deleteById(p.get().getId());
    }

    /* 게시물 업데이트 */
    @Override
    @Transactional
    public Post updatePost(Long id, PostDTO p, List<MultipartFile> files) {
        return postRepository.findById(id).map(
                existingPost -> {
                    existingPost.setTitle(p.getTitle());
                    existingPost.setPrice(p.getPrice());
                    existingPost.setLocation(p.getLocation());
                    existingPost.setDescription(p.getDescription());
                    existingPost.setCategory(p.getCategory());

                    if (files != null &&
                            files.stream().anyMatch(file -> !file.isEmpty())) {
                        deleteImages(id);
                        existingPost.getImages().clear();
                        List<Image> imageList = new ArrayList<>();
                        for (MultipartFile file : files) {
                            try {
                                String fileUrl = s3Service.uploadFile(file);
                                String s3Key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                                Image image = new Image(existingPost, fileUrl, s3Key, file.getOriginalFilename());
                                imageList.add(image);
                            } catch (IOException e) {
                                throw new RuntimeException("파일 업로드 중 오류 발생", e);
                            }
                        }
                        existingPost.getImages().addAll(imageList);
                    }

                    return postRepository.save(existingPost);
                }).orElseThrow(() -> new EntityExistsException("존재하지않는 게시물입니다."));
    }

    /* 이미지 삭제 */
    public void deleteImages(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        List<Image> images = post.getImages();
        for (Image image : images) {
            s3Service.deleteFile(image.getS3Key());
        }
    }

    /* 조회수 */
    @Transactional
    public void increaseViewCount(Long postId) {
        postRepository.increaseViewCount(postId);
    }

    /* 페이지네이션(전체 게시물) */
    @Override
    @Transactional(readOnly = true)
    public Page<Post> findAll(Pageable pageable) { return postRepository.findAll(pageable); }

    /* 페이지네이션(검색) */
    @Override
    @Transactional(readOnly = true)
    public Page<Post> searchPosts(String keyword, Long categoryId,
                                  Integer priceMin, Integer priceMax, Boolean isSold, String dong, Pageable pageable) {
        boolean onlyAvaliable = Boolean.TRUE.equals(isSold);
        return postRepository.findByKeywordAndCategory(keyword, categoryId,
                priceMin, priceMax, onlyAvaliable, dong, pageable);
    }

    /* slice를 통한 무한스크롤 */
    @Override
    @Transactional(readOnly = true)
    public Slice<Post> findAllSlice(Pageable pageable) { return postRepository.findAll(pageable); }


    private final List<String> dongList = new ArrayList<>();

    @PostConstruct
    public void loadDongList() {
        try (InputStream is = getClass().getResourceAsStream("/data/dong-list.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            if (is == null) {
                System.err.println("CSV 파일을 찾을 수 없습니다.");
                return;
            }

            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    String fullDong = tokens[1] + " " + tokens[2] + " " + tokens[3] + " " + tokens[4];
                    dongList.add(fullDong.trim());
                }
            }

            System.out.println("동 리스트 로딩 완료: " + dongList.size() + "개");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getRegionData(String regionName, String dongName) {
        return dongList.stream()
                .filter(dong -> dong.contains(regionName))
                .map(dong -> {

                    String[] parts = dong.split(" ");
                    List<String> priorityOrder = List.of("동", "가", "읍", "면");

                    for (int i = parts.length - 1; i >= 0; i--) {
                        for (String suffix : priorityOrder) {
                            if (parts[i].endsWith(suffix) && !parts[i].contains(regionName)
                                    && !parts[i].contains(dongName)) {
                                return parts[i];
                            }
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }
}