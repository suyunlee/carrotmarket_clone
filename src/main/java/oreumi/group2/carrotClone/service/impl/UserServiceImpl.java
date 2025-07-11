package oreumi.group2.carrotClone.service.impl;

import jakarta.persistence.EntityExistsException;
import oreumi.group2.carrotClone.DTO.UserDTO;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import oreumi.group2.carrotClone.model.enums.UserRole;
import oreumi.group2.carrotClone.repository.UserRepository;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* user 정보 저장 */
    @Override
    public User register(UserDTO userDTO) {

        if(userRepository.existsByUsername(userDTO.getUsername())){
            throw new EntityExistsException("이미 사용중인 아이디입니다.");
        }
        if(userRepository.existsByNickname(userDTO.getNickname())){
            throw new EntityExistsException("이미 사용중인 닉네임입니다.");
        }
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()){
            if (userDTO.getPassword().length() <= 2){
                throw new IllegalArgumentException(("비밀번호는 3자리 이상이어야 합니다."));
            }
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setNickname(userDTO.getNickname());
        user.setLocation(userDTO.getLocation());
        user.setRole(UserRole.USER);

        /* 소셜 로그인할시 */
        if(userDTO.getProviderId() != null){
            user.setProvider(userDTO.getProvider());
            user.setProviderId(userDTO.getProviderId());
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
        exiting.setLocation(user.getLocation());
        exiting.setNeighborhoodVerifiedAt(user.getNeighborhoodVerifiedAt());
        exiting.setNeighborhoodName(user.getNeighborhoodName());
        exiting.setNeighborhoodVerifiedAt(user.getNeighborhoodVerifiedAt());
        exiting.setRole(user.getRole());

        return userRepository.save(user);
    }

    /* id 기준 위치 조회 */
    @Override
    public String getLocation(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityExistsException("해당 유저는 존재하지 않습니다"))
                .getLocation();
    }

    /* id 기준 유저 삭제 */
    @Override
    public void deleteUser(Long id) {
        if(userRepository.findById(id) == null){
            throw new EntityExistsException("해당 유저는 존재하지않습니다.");
        }
        userRepository.deleteById(id);
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