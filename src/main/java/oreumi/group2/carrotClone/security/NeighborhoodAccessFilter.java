package oreumi.group2.carrotClone.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 게시글 작성, 채팅방 진입시
 * 로그인, 동네인증 상태검사 후 각각 회원가입, 동네인증 페이지로 리다이렉트
 */

@Component
public class NeighborhoodAccessFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
        throws ServletException, IOException {

        String path = req.getServletPath();
        String method = req.getMethod();

        boolean readOnlyPost =
                "GET".equals(method) && ("/posts".equals(path) || path.matches("^/posts/\\d+$"));

        boolean blockPostActions = path.startsWith("/posts") && !readOnlyPost;
        boolean blockChat        = path.startsWith("/chat/");

        if (blockPostActions || blockChat) {
            Authentication auth = SecurityContextHolder
                    .getContext().getAuthentication();

            // 1) 로그인 안 됨 → 회원가입으로 리다이렉트
            if (auth == null || !auth.isAuthenticated() ||
                    "anonymousUser".equals(auth.getPrincipal())) {
                res.sendRedirect("/users/login");
                return;
            }

            // 2) 로그인O, 동네인증 X 동네인증 페이지로 리다이렉트
            Object principal = auth.getPrincipal();
            boolean verified = false;
            if (principal instanceof CustomUserPrincipal) {
                verified = ((CustomUserPrincipal) principal)
                        .getUser().isNeighborhoodVerified();
            }
            if (!verified) {
                res.sendRedirect("/maps/verify");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
