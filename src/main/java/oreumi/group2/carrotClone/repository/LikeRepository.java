package oreumi.group2.carrotClone.repository;

import oreumi.group2.carrotClone.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
     boolean existsByPostIdAndUserId(Long postId, Long userId);
     Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
}