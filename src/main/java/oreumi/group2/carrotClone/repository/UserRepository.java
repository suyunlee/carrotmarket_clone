package oreumi.group2.carrotClone.repository;

import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    /* 로그인 조회 용도 */
    Optional<User> findByUsername(String username);

    /* 닉네임 조회 용도 */
    Optional<User> findByNickname(String nickname);

    /* 회원가입 중복 체크 */
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByPhoneNumber(String phoneNumber);

    /* 소셜 로그인 */
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}