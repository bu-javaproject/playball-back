package com.playball.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //요청 1번 당 1번만 실행되는 필터

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //요청 헤더에서 토큰 꺼내기
        String token = resolveToken(request);

        //토큰 존재 + 유효하면 -> 인증 정보 저장
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 토큰에서 회원 정보 추출
            Long memberId = jwtTokenProvider.getMemberIdFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            //Spring Security가 이해하는 형태로 인증 객체 생성
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(memberId,
                                                                                                null,
                                                                                                         List.of(new SimpleGrantedAuthority("ROLE_" + role)));

            //요청의 IP, 세션 등 부가 정보 첨부
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //Spring Security 금고에 "이 요청은 1번 회원 거야" 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 토큰이 없거나 유효하지 않으면 -> 아무것도 안 함
        // -> SecurityContextHolder가 비어있음
        // -> SecurityConfig에서 authenticated()로 막힘 (401 에러)
        // -> permitAll()이면 그냥 통과

        //controller로 요청 넘기기 , 토큰 검증에 실패해도 해당 코드는 실행
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        //헤더에서 토큰만 추출
        String bearerToken = request.getHeader("Authorization");

        //"Beaerer"로 시작하면 -> 앞 7글자 잘라서 순수 토큰만 리턴
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Authorization 헤더가 없거나 형식이 다르면 null 리턴
        return null;
    }
}
