package oreumi.group2.carrotClone.repository;

import jakarta.transaction.Transactional;
import oreumi.group2.carrotClone.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    //제목 기반 검색
    List<Post> findByTitleContainingIgnoreCase (String keyword);
    // 장소 기반 검색
    List<Post> findByLocationContainingIgnoreCase (String location);

    List<Post> findAllByOrderByCreatedAtDesc(); //최신순 정렬
    List<Post> findAllByOrderByPriceAsc(); // 가격 낮은 순 정렬
    List<Post> findAllByOrderByPriceDesc(); // 가격 높은 순 정렬

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void increaseViewCount(@Param("id") Long id);
}