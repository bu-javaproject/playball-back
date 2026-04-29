package com.playball.backend.config;

import com.playball.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionCreationEvent;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //1.csrf 보호 끄기
                .csrf(csrf -> csrf.disable())

                //2.서버에 세션 만들지 않기
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //3.URL별 접근 규칙
                .authorizeHttpRequests(auth -> auth
                        //Swagger UI
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                        .permitAll()
                        //인증 API (카카오 로그인, 토큰 재발급)
                        .requestMatchers("/api/auth/kakao", "/api/auth/refresh")
                        .permitAll()
                        //닉네임 중복 확인 (비로그인도 가능)
                        .requestMatchers("/api/members/check-nickname")
                        .permitAll()
                        //경기 조회, 로컬 매칭 (비로그인도 보기 가능)
                        .requestMatchers(HttpMethod.GET, "/api/matches/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/matching/local")
                        .permitAll()
                        //WebSocket 연결
                        .requestMatchers("/ws/**")
                        .permitAll()
                        //나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                //4.JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
}





