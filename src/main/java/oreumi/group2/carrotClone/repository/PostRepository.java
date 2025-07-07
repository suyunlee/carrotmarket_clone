package oreumi.group2.carrotClone.repository;

import oreumi.group2.carrotClone.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    //제목 기반 검색
    List<Post> findbyTitleContainingIgnoreCase (String keyword);
    // 장소 기반 검색
    List<Post> findbyLocationContainingIgnorecase (String location);

    List<Post> findAllByOrderByCreatedAtDesc(); //최신순 정렬
    List<Post> findAllByOrderByPriceAsc(); // 가격 낮은 순 정렬
    List<Post> findAllByOrderByPriceDesc(); // 가격 높은 순 정렬
}
