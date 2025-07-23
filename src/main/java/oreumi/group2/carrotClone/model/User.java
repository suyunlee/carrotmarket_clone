package oreumi.group2.carrotClone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import oreumi.group2.carrotClone.model.enums.UserRole;
import oreumi.group2.carrotClone.validation.ValidPassword;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
              message = "유효한 이메일 주소를 입력하세요.")
    private String username;

    @ValidPassword
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$",
                message = "유효한 비밀번호를 입력하세요.")
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(length = 255)
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(name = "neighborhood_verified", nullable = false)
    private boolean neighborhoodVerified = false;

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

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();
}