package oreumi.group2.carrotClone.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        userService.findByUsername(email).orElseGet(() -> {
            User user = User.builder()
                    .username(email)
                    .password("") // 소셜 로그인은 패스워드 없음
                    .nickname(oAuth2User.getAttribute("name"))
                    .role("ROLE_USER")
                    .status("ACTIVE")
                    .provider(AuthProvider.GOOGLE)
                    .providerId(oAuth2User.getName())
                    .build();
            return userService.register(user);
        });

        response.sendRedirect("/");
    }
}

