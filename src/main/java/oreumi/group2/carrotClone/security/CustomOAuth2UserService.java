package oreumi.group2.carrotClone.security;

import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import oreumi.group2.carrotClone.model.enums.UserRole;
import oreumi.group2.carrotClone.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리 중 오류 발생: " + ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {

        // 어떤 소셜로 로그인했는지
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Google 사용자 정보 추출
        String id = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");

        // DB 식별자로 사용하기에 무조건가져와야함
        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("이메일 정보를 가져올 수 없습니다.");
        }

        // 이미 가입된 사용자인지 판단
        Optional<User> userOptional = userRepository.findByUsername(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 기존 사용자가 다른 OAuth 제공자로 가입한 경우 체크
            if (!user.getProvider().equals(AuthProvider.valueOf(registrationId.toUpperCase()))) {
                throw new OAuth2AuthenticationException(
                        "이미 " + user.getProvider() + " 계정으로 가입된 이메일입니다."
                );
            }
        } else {
            // 새 사용자 생성
            user = new User();
            user.setUsername(email);
            user.setPassword("qweqweqwe@#!@#!1231");
            user.setRole(UserRole.USER);
            user.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            user.setProviderId(id);
            user.setNickname(name);
            user = userRepository.save(user);
        }

        // 사용자 정보를 attributes에 추가
        Map<String, Object> modifiedAttributes = new HashMap<>(attributes);
        modifiedAttributes.put("user", user);

        // OAuth2User 객체 반환 (권한과 함께)
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                modifiedAttributes,
                "sub" // nameAttributeKey
        );
    }
}

