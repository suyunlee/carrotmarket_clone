package oreumi.group2.carrotClone.model;

import jakarta.persistence.*;
import lombok.*;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import oreumi.group2.carrotClone.model.enums.UserRole;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(unique = true, nullable = false, length = 255)
    private String nickname;

    @Column(name = "phonenumber", nullable = false, length = 20)
    private String phoneNumber;

    @Column(length = 255)
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false,updatable = false)
    private LocalDateTime createdAt; /* 생성일자 */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(name = "neighborhood_verified")
    private boolean neighborhoodVerified;

    @Column(name = "neighborhood_name", length = 100)
    private String neighborhoodName;

    @Column(name = "neighborhood_verified_at")
    private LocalDateTime neighborhoodVerifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 20)
    private AuthProvider provider; /* 소셜 로그인 */

    @Column(name = "provider_id", length = 255)
    private String providerId;

    /* ==== ERD PK 연동 관계 === */

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>(); /* post */

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>(); /* like */

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();
}