package com.playball.backend.security.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.member.dto.MemberDTO;
import com.playball.backend.member.mapper.MemberMapper;
import com.playball.backend.security.JwtTokenProvider;
import com.playball.backend.security.dto.KakaoLoginResponse;
import com.playball.backend.security.dto.RefreshTokenDTO;
import com.playball.backend.security.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberMapper memberMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOAuthService kakaoOAuthService;

    /**
     * 카카오 로그인
     * - 기존 회원 → 토큰 발급 (isNewUser: false)
     * - 신규 회원 → 카카오 기본 정보로 임시 가입 후 토큰 발급 (isNewUser: true)
     */
    @Transactional
    public KakaoLoginResponse kakaoLogin(String authorizationCode, String redirectUri) {
        // 1. 카카오 인가코드 → 카카오 Access Token
        String kakaoAccessToken = kakaoOAuthService.getAccessToken(authorizationCode, redirectUri);

        // 2. 카카오 Access Token → 사용자 정보
        KakaoOAuthService.KakaoUserInfo kakaoUser = kakaoOAuthService.getUserInfo(kakaoAccessToken);

        // 3. 기존 회원 확인
        Optional<MemberDTO> existingMember = memberMapper.findByKakaoId(kakaoUser.kakaoId());

        MemberDTO member;
        boolean isNewUser;

        if (existingMember.isPresent()) {
            // 기존 회원
            member = existingMember.get();
            isNewUser = !Boolean.TRUE.equals(member.getSignupCompleted());
        } else {
            // 신규 회원 → 임시 등록
            member = MemberDTO.builder()
                    .kakaoId(kakaoUser.kakaoId())
                    .email(kakaoUser.email())
                    .name(kakaoUser.nickname())
                    .profileImage(kakaoUser.profileImage())
                    .role("USER")
                    .signupCompleted(false)
                    .build();
            memberMapper.insertMember(member);
            isNewUser = true;
            log.info("신규 회원 임시 등록: kakaoId={}, memberId={}", kakaoUser.kakaoId(), member.getMemberId());
        }

        // 4. JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(
                String.valueOf(member.getMemberId()), member.getRole());
        String refreshToken = createAndSaveRefreshToken(member.getMemberId());

        return KakaoLoginResponse.of(accessToken, refreshToken, "Bearer", isNewUser, member);
    }

    /**
     * Refresh Token으로 Access Token 재발급
     */
    @Transactional
    public KakaoLoginResponse refreshAccessToken(String refreshToken) {
        RefreshTokenDTO storedToken = refreshTokenMapper.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenMapper.deleteByToken(refreshToken);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        MemberDTO member = memberMapper.findById(storedToken.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        refreshTokenMapper.deleteByMemberId(member.getMemberId());
        String newRefreshToken = createAndSaveRefreshToken(member.getMemberId());
        String newAccessToken = jwtTokenProvider.createAccessToken(
                String.valueOf(member.getMemberId()), member.getRole());

        boolean isNewUser = !Boolean.TRUE.equals(member.getSignupCompleted());
        return KakaoLoginResponse.of(newAccessToken, newRefreshToken, "Bearer", isNewUser, member);
    }

    /**
     * 로그아웃 → Refresh Token 삭제
     */
    @Transactional
    public void logout(Long memberId) {
        refreshTokenMapper.deleteByMemberId(memberId);
    }

    /**
     * Refresh Token 생성 및 DB 저장
     */
    private String createAndSaveRefreshToken(Long memberId) {
        refreshTokenMapper.deleteByMemberId(memberId);

        String token = jwtTokenProvider.createRefreshToken();
        long expirationMs = jwtTokenProvider.getRefreshExpiration();

        RefreshTokenDTO refreshToken = RefreshTokenDTO.builder()
                .memberId(memberId)
                .token(token)
                .expiryDate(LocalDateTime.now().plusSeconds(expirationMs / 1000))
                .build();

        refreshTokenMapper.insertToken(refreshToken);
        return token;
    }
}
