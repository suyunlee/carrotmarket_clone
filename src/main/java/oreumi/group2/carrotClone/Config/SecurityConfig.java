package oreumi.group2.carrotClone.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
            .csrf(csrf -> csrf.disable()) /* 개발중임으로 */
            /* userDetailService 추가 필요 */
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))  // ← 이 부분 추가!
            .authorizeHttpRequests(user -> user
                    .anyRequest().permitAll())
            .formLogin(user -> user
                    .loginPage("/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll())
            .logout(user -> user
                    /* 핸들러 추가 필요(successHandler) */
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            )
            .httpBasic(Customizer.withDefaults());
            return http.build();
    }
}