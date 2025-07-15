package oreumi.group2.carrotClone.service.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import oreumi.group2.carrotClone.dto.PostDTO;
import oreumi.group2.carrotClone.model.*;
import oreumi.group2.carrotClone.repository.LikeRepository;
import oreumi.group2.carrotClone.repository.PostRepository;
import oreumi.group2.carrotClone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired PostRepository postRepository;
    /* DI */
    @Autowired LikeRepository likeRepository;
    private final String FILESTORE_PATH = "C:/uploads/";

    /* ID 기반 게시물 찾기 */
    @Override
    @Transactional
    public Optional<Post> findById(Long id) { return postRepository.findById(id); }

    /* 전체 조회 */
    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // 제목 기반 검색
    @Override
    public List<Post> searchPostsByTitle(String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // 위치 기반 검색
    @Override
    public List<Post> searchPostsByLocation(String location) {
        return postRepository.findByLocationContainingIgnoreCase(location);
    }

    // 최신순 정렬
    @Override
    public List<Post> getPostsSortedByNewest() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // 가격 정렬 (비싼순서 / 싼 순서)
    @Override
    public List<Post> getPostsSortedByPrice(boolean ascending) {
        return ascending ? postRepository.findAllByOrderByPriceAsc() : postRepository.findAllByOrderByPriceDesc();
    }

    // 게시물 단건 조회
    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

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
//        List<Image> imageList = new ArrayList<>();
//        for(MultipartFile file : files){
//            String fileurl = storeAndGetFileUrl(file);
//            Image image = new Image();
//            image.setImageUrl(fileurl);
//            image.setPost(p);
//
//            imageList.add(image);
//        }
//        p.setImages(imageList);
        return postRepository.save(p);
    }

    /* 게시물 삭제 */
    @Override
    public void deletePost(Long id) {
        Optional<Post> p = postRepository.findById(id);
        if(p == null){
            throw new EntityExistsException("존재하지않는 게시물입니다.");
        }
        postRepository.deleteById(p.get().getId());
    }

    /* 게시물 업데이트 */
    @Override
    public Post updatePost(Long id, PostDTO p) {
        return postRepository.findById(id).map(
                existingPost -> {
                    existingPost.setTitle(p.getTitle());
                    existingPost.setPrice(p.getPrice());
                    existingPost.setLocation(p.getLocation());
                    existingPost.setDescription(p.getDescription());
                    existingPost.setCategory(p.getCategory());
                    return postRepository.save(existingPost);
                }).orElseThrow(() -> new EntityExistsException("존재하지않는 게시물입니다."));
    }

    /* 이미지 확장자 리턴 메소드 */
    private String getFileType(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        if (contentType != null) {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            switch (mediaType.toString()) {
                case MediaType.IMAGE_JPEG_VALUE -> { return ".jpeg"; }
                case MediaType.IMAGE_PNG_VALUE -> { return ".png"; }
            }
        }
        return "";
    }

    /* 이미지 저장 */
    @SneakyThrows
    public String storeAndGetFileUrl(MultipartFile multipartFile) {
        if(getFileType(multipartFile).contentEquals("")) {
            throw new IllegalArgumentException("허용되지 않은 이미지 파일 타입입니다.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = UUID.randomUUID() + "_" + originalFilename;
        File file = new File(FILESTORE_PATH + storeFilename);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("폴더 만들기 실패");
            }
        }

        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return "/uploads/" + storeFilename;
    }

    /* 좋아요 */
    @Override
    @Transactional
    public boolean isLikedByUser(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId , user.getId());

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            Like like = new Like(post, user);
            likeRepository.save(like);
        }
        return likeRepository.existsByPostIdAndUserId(postId,user.getId());
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
    public Page<Post> searchPosts(String keyword, Long categoryId, Pageable pageable) {
        return postRepository.findByKeywordAndCategory(keyword, categoryId, pageable);
    }
}