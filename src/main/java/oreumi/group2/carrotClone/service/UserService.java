package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.model.enums.AuthProvider;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /* 회원 가입*/
    User register(User user);
    
    /* username 조회*/
    Optional<User> findByUsername(String username);
    
    /* nickname 조회 */
    Optional<User> findByNickname(String nickname);

    /* id 기준으로 user 찾기 */
    Optional<User> getUserById(Long id);

    /* user 정보 업데이트 */
    User updateUser(User user);

    /* user 정보 삭제 */
    void deleteUser(Long id);
    
    /* 휴대폰 전화 중복 확인 */
    boolean existsByPhoneNumber(String phoneNumber);

    /* id 기준 위치 조회 */
    String getLocation(Long id);

    /* 모든 유저 가져오기 */
    List<User> getAllUsers();

    /* 소셜 로그인 */
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}