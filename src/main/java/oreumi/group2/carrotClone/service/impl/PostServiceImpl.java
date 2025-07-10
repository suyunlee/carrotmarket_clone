package oreumi.group2.carrotClone.service.impl;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.DTO.PostDTO;
import oreumi.group2.carrotClone.model.*;
import oreumi.group2.carrotClone.repository.LikeRepository;
import oreumi.group2.carrotClone.repository.PostRepository;
import oreumi.group2.carrotClone.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired PostRepository postRepository;
    /* DI */
    @Autowired LikeRepository likeRepository;


    /* ID 기반 게시물 찾기 */
    @Override
    @Transactional(readOnly = true)
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
    public Post createPost(User user, PostDTO postDTO, List<String> images) {
        Post p = new Post();
        p.setUser(user);
        p.setTitle(postDTO.getTitle());
        p.setDescription(postDTO.getDescription());
        p.setPrice(postDTO.getPrice());
        p.setLocation(postDTO.getLocation());
        p.setCategory(postDTO.getCategory());
        List<Image> imageList = new ArrayList<>();
        for(String s : images){
            Image i = new Image();
            i.setImageUrl(s);
            i.setPost(p);

            imageList.add(i);
        }
        p.setImages(imageList);
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
                    existingPost.setSold(p.isSold());
                    existingPost.setLocation(p.getLocation());
                    existingPost.setDescription(p.getDescription());
                    existingPost.setCategory(p.getCategory());
                    return postRepository.save(existingPost);
                }).orElseThrow(() -> new EntityExistsException("존재하지않는 게시물입니다."));
    }

    /* 좋아요 */
    @Override
    @Transactional(readOnly = true)
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