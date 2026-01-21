package com.library.bookarte.global.config;

import com.library.bookarte.auth.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Postman 테스트용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_PATTERNS).permitAll() //swagger 관련 요청 허용
                        .requestMatchers(
                                "/api/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] SWAGGER_PATTERNS = {
            "/v3/api-docs/**",         // API 정의 JSON 파일
            "/swagger-ui/**",          // Swagger UI 리소스
            "/swagger-ui.html",        // 접속 페이지
            "/swagger-resources/**",   // 관련 리소스
            "/webjars/**"              // 라이브러리 파일
    };
}
