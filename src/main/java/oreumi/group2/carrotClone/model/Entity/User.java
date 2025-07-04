package oreumi.group2.carrotClone.model.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.security.AuthProvider;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = true)
    private String location;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String status;

    @Column(nullable = true)
    private boolean neighborhoodVerified;

    @Column(nullable = true)
    private String neighborhoodName;

    @Column(nullable = true)
    private LocalDateTime neighborhoodVerifiedAt;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

}
