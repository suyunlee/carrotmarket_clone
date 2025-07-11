package oreumi.group2.carrotClone.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title; /* 제목 */

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; /* 설명 */

    @Column(nullable = false,precision = 15,scale = 2)
    private BigDecimal price; /* 가격 */

    @Column(nullable = false)
    private boolean Sold; /* 판매여부 */

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; /* 등록 날짜 */

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; /* 업데이트 날짜 */

    @Column(length = 255)
    private String location; /* 위치 */

    @Column(name = "viewcount")
    private Long viewCount = 0L; /* 조회수 */

    /* 좋아요 : 일대다 관계 (1: N)*/
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>(); /* 좋아요 */

    /* 이미지 : 일대다 관계 (1: N)*/    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>(); /* 좋아요 */

    /* 채팅방 : 일대다 관계 (1: N)*/
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>(); /* 좋아요 */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; /* User 정보 */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; /* category 정보 */



}