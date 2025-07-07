package oreumi.group2.carrotClone.service.impl;


import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.repository.PostRepository;
import oreumi.group2.carrotClone.service.PostService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceimpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    // 제목 기반 검색
    @Override
    public List<Post> searchPostsByTitle(String keyword) {
        return postRepository.findbyTitleContainingIgnoreCase(keyword);
    }

    // 위치 기반 검색
    @Override
    public List<Post> searchPostsByLocation(String location) {
        return postRepository.findbyLocationContainingIgnorecase(location);
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
    public Post createPost(Post post) {
        return postRepository.save(post);
    }
}
