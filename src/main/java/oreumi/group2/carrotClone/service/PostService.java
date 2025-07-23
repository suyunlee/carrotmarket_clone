package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.dto.PostDTO;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> findById(Long id);
    Post createPost (User user, PostDTO postDTO, List<MultipartFile> images); /* 저장 */
    void deletePost(Long id); // 삭제
    Post updatePost(Long id, PostDTO p,  List<MultipartFile> files); /* 단일 업데이트 */
    void increaseViewCount(Long postId); /* 조회수 */
    Page<Post> findAll(Pageable pageable);
    Page<Post> searchPosts(String keyword, Long categoryId,
                           Integer priceMin, Integer priceMax, Boolean isSold, String dong, Pageable pageable);
    Slice<Post> findAllSlice(Pageable pageable);
    List<String> getRegionData(String regionName, String dongName);
}