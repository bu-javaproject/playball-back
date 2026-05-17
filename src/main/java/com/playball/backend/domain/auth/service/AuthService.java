package com.playball.backend.domain.auth.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.auth.jwt.JwtTokenProvider;
import com.playball.backend.domain.auth.oauth.KakaoOAuthService;
import com.playball.backend.domain.auth.dto.KakaoLoginResponse;
import com.playball.backend.domain.auth.entity.RefreshToken;
import com.playball.backend.domain.auth.repository.RefreshTokenRepository;
import com.playball.backend.domain.member.entity.Member;
import com.playball.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOAuthService kakaoOAuthService;

    @Value("${kakao.redirect-uri}") // yml에서 직접 읽어옴
    private String redirectUri;

    @Transactional
    public KakaoLoginResponse kakaoLogin(String authorizationCode) {
        String kakaoAccessToken = kakaoOAuthService.getAccessToken(authorizationCode, redirectUri);
        KakaoOAuthService.KakaoUserInfo kakaoUser = kakaoOAuthService.getUserInfo(kakaoAccessToken);

        Member member = memberRepository.findByKakaoId(kakaoUser.kakaoId())
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .kakaoId(kakaoUser.kakaoId())
                            .email(kakaoUser.email())
                            .name(kakaoUser.nickname())
                            .profileImage(kakaoUser.profileImage())
                            .role("USER")
                            .signupCompleted(false)
                            .build();
                    Member saved = memberRepository.save(newMember);
                    log.info("신규 회원 임시 등록: kakaoId={}, memberId={}", kakaoUser.kakaoId(), saved.getMemberId());
                    return saved;
                });

        boolean isNewUser = !Boolean.TRUE.equals(member.getSignupCompleted());

        String accessToken = jwtTokenProvider.createAccessToken(
                String.valueOf(member.getMemberId()), member.getRole());
        String refreshToken = createAndSaveRefreshToken(member.getMemberId());

        return KakaoLoginResponse.of(accessToken, refreshToken, "Bearer", isNewUser, member);
    }

    @Transactional
    public KakaoLoginResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        Member member = memberRepository.findById(storedToken.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newRefreshToken = createAndSaveRefreshToken(member.getMemberId());
        String newAccessToken = jwtTokenProvider.createAccessToken(
                String.valueOf(member.getMemberId()), member.getRole());

        boolean isNewUser = !Boolean.TRUE.equals(member.getSignupCompleted());
        return KakaoLoginResponse.of(newAccessToken, newRefreshToken, "Bearer", isNewUser, member);
    }

    @Transactional
    public void logout(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    private String createAndSaveRefreshToken(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);

        String token = jwtTokenProvider.createRefreshToken();
        long expirationMs = jwtTokenProvider.getRefreshExpiration();

        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(memberId)
                .token(token)
                .expiryDate(LocalDateTime.now().plusSeconds(expirationMs / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
