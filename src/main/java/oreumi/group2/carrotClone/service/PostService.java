package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.DTO.PostDTO;
import oreumi.group2.carrotClone.model.Category;
import oreumi.group2.carrotClone.model.Post;
import oreumi.group2.carrotClone.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> findById(Long id);
    List<Post> getAllPosts();  /* 전체 조회 */
    List<Post> searchPostsByTitle (String keyword); /* 제목 조회 */
    List<Post> searchPostsByLocation (String location); /* 위치기반 조회 */
    List<Post> getPostsSortedByNewest (); /* 최신순  */
    List<Post> getPostsSortedByPrice (boolean ascending); /* 가격순 */
    Post getPostById (Long id); /* Id 기반 조회 */
    Post createPost (User user, String title, String description, BigDecimal price,
                     String location,List<String> images, Category category); /* 저장 */
    void deletePost(Long id); // 삭제
    Post updatePost(Post post); /* 단일 업데이트 */
    boolean isLikedByUser(Long postId, User user);
}