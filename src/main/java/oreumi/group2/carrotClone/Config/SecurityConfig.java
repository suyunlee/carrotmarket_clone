package oreumi.group2.carrotClone.Config;

import lombok.RequiredArgsConstructor;
import oreumi.group2.carrotClone.security.CustomOAuth2UserService;
import oreumi.group2.carrotClone.security.CustomUserDetailService;
import oreumi.group2.carrotClone.security.NeighborhoodAccessFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailService customUserDetailService;
    private final NeighborhoodAccessFilter neighborhoodAccessFilter;

    // 비밀번호 암호화 처리
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 사용자 정보 검증
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(customUserDetailService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    // 시큐리티 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/chat/**", "/ws-chat/**", "/posts" ))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/scripts/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/", "/posts", "/posts/**", "/users/signup", "/users",
                                "/login", "/maps/permission", "/maps/verify", "/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/posts/*").permitAll()
                        .anyRequest().authenticated()

                )
                .addFilterAfter(neighborhoodAccessFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(login -> login
                        .loginPage("/users/login")
                        .loginProcessingUrl("/login") /* post */
                        .defaultSuccessUrl("/", true)
                        .failureHandler((request, response, exception) -> {
                            request.setAttribute("로그인 실패하였습니다.", exception.getMessage());
                            request.getRequestDispatcher("/error-page").forward(request, response);
                        })      // 실패 시 출력 메세지 및 error 로 보내기
                        .usernameParameter("username")           // 아이디 필드명 지정
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/users/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/",true)
                        .failureUrl("/login?error=oauth2_error")
                );
        return http.build();
    }
}