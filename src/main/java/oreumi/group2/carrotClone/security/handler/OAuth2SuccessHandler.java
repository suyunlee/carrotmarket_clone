package oreumi.group2.carrotClone.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.model.User;
import oreumi.group2.carrotClone.model.enums.AuthProvider;
import oreumi.group2.carrotClone.model.enums.UserRole;
import oreumi.group2.carrotClone.security.CustomOAuth2User;
import oreumi.group2.carrotClone.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Builder
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getEmail();

        userService.findByUsername(email).orElseGet(() -> {
            User user = User.builder()
                    .username(email)
                    .password("") // OAuth는 비밀번호 없음
                    .nickname(oAuth2User.getName())
                    .status("ACTIVE")
                    .provider(AuthProvider.GOOGLE)
                    .providerId(oAuth2User.getProviderId())
                    .build();
            return userService.register(user);
        });

        response.sendRedirect("/");
    }
}

