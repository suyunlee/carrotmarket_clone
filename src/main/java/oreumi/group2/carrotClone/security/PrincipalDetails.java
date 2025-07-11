package oreumi.group2.carrotClone.security;

import oreumi.group2.carrotClone.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    // 폼 로그인 전용 생성자
    public PrincipalDetails(User user) {
        this.user = user;
        this.attributes = Collections.emptyMap();
    }

    // OAuth2 로그인 전용 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // ─────────────────────────────────────────
    // UserDetails 구현 (폼 로그인)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 예: UserRole이 USER 또는 ADMIN
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    // ─────────────────────────────────────────
    // OAuth2User 구현 (소셜 로그인)
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        // 구글의 고유 식별자(sub)를 리턴
        return attributes.getOrDefault("sub", user.getProviderId()).toString();
    }

    // ─────────────────────────────────────────
    // 도메인 User 객체 getter
    public User getUser() {
        return user;
    }
}