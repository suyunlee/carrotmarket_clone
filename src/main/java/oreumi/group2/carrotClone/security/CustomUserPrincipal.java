package oreumi.group2.carrotClone.security;

import oreumi.group2.carrotClone.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomUserPrincipal implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String,Object> attributes;

    // form 로그인용
    public CustomUserPrincipal(User user) {
        this.user = user;
        this.attributes = Collections.emptyMap();
    }

    // oAuth2 로그인용
    public CustomUserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // --- Form 정보 ---
    public User getUser() {
        return user;
    }

    // --- OAuth2User 메서드 ---
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return attributes.getOrDefault("sub", user.getProviderId()).toString();
    }

    // --- UserDetails 메서드 ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}