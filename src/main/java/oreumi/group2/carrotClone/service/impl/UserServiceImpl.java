package oreumi.group2.carrotClone.service.impl;

import jakarta.persistence.EntityExistsException;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired private UserRepository userRepository;

    /* user 정보 저장 */
    @Override
    public User register(User user) {

        if(userRepository.existsByUsername(user.getUsername())){
            throw new EntityExistsException("이미 사용중인 아이디입니다.");
        }
        if(userRepository.existsByNickname(user.getNickname())){
            throw new EntityExistsException("이미 사용중인 닉네임입니다.");
        }
        if(userRepository.existsByPhoneNumber(user.getPhoneNumber())){
            throw new EntityExistsException("이미 등록된 전화번호입니다.");
        }
        return userRepository.save(user);
    }

    /* Username 기준으로 조회 */
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /* nickname 기준으로 조회 */
    @Override
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    /* id 기준 user 조회 */
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /* 유저 정보 업데이트 */
    @Override
    public User updateUser(User user) {
        User exiting =  userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityExistsException("해당 유저는 존재하지않습니다."));

        exiting.setUsername(user.getUsername());
        exiting.setNickname(user.getNickname());
        exiting.setPhoneNumber(user.getPhoneNumber());

        return userRepository.save(user);
    }

    /* id 기준 유저 삭제 */
    @Override
    public void deleteUser(Long id) {
        if(userRepository.findById(id) == null){
            throw new EntityExistsException("해당 유저는 존재하지않습니다.");
        }
        userRepository.deleteById(id);
    }

    /* 휴대폰 전화번호 중복체크 */
    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    /* 전체 조회 */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /* 소셜 로그인 */
    @Override
    public Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider,providerId);
    }
}