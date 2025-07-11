package oreumi.group2.carrotClone.security;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String provider   = userRequest.getClientRegistration().getRegistrationId(); // "google"
        Map<String,Object> attrs = oauth2User.getAttributes();
        String providerId = attrs.get("sub").toString();
        String email      = attrs.get("email").toString();
        String name       = attrs.get("name").toString();

        User user = userRepository.findByUsername(email)
                .filter(u -> u.getProvider() == AuthProvider.valueOf(provider.toUpperCase()))
                .map(u -> {
                    u.setNickname(name);
                    return userRepository.save(u);
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(email);
                    u.setPassword("OAuth2User");  // 임시 패스워드
                    u.setRole(UserRole.USER);
                    u.setProvider(AuthProvider.valueOf(provider.toUpperCase()));
                    u.setProviderId(providerId);
                    u.setNickname(name);
                    return userRepository.save(u);
                });

        return new PrincipalDetails(user, attrs);
    }
}