package com.playball.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    //Access Token 만들기
    public String createAccessToken(String memberId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .subject(memberId) //토큰 주인
                .claim("role", role) //권한 (커스텀 데이터)
                .issuedAt(now) //지금 발급
                .expiration(expiryDate) //expiryDate 시간 지나면 무효
                .signWith(secretKey) //비밀키 (위조 방지)
                .compact(); //문자열로 반환
    }

    //refresh Token 만들기
    public String createRefreshToken() {
        return UUID.randomUUID().toString(); //이 문자열이 DB에 있으면 유효 토큰
    }

    //Token에서 정보 꺼내기
    public Long getMemberIdFromToken(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    //Token 유효성 검증
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
