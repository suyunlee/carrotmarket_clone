package oreumi.group2.carrotClone.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 구글 OAuth 로그인 시 사용자 정보를 감싸는 클래스
 */
@Getter
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }

    public String getProviderId() {
        return oAuth2User.getName(); // 고유 식별자 (sub)
    }
}
