package oreumi.group2.carrotClone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; /* User 정보 */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; /* category 정보 */

    @Column(nullable = false, length = 255)
    private String title; /* 제목 */

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; /* 설명 */

    @Column(nullable = false)
    private Double price; /* 가격 */

    private boolean isSold; /* 판매여부 */

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt; /* 등록 날짜 */

    @UpdateTimestamp
    private Timestamp updatedAt; /* 업데이트 날짜 */

    @Column(length = 255)
    private String location; /* 위치 */

    private Long viewCount; /* 조회수 */

    /* 좋아요 : 일대다 관계 (1: N)*/
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes; /* 좋아요 */

    /* 이미지 : 일대다 관계 (1: N)*/    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images; /* 좋아요 */

    /* 채팅방 : 일대다 관계 (1: N)*/
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms; /* 좋아요 */
}