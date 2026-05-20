package com.playball.backend.domain.auth.dto;

import com.playball.backend.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "카카오 로그인 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginResponse {

    @Schema(description = "JWT Access Token (30분 유효)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT Refresh Token (7일 유효)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "신규 회원 여부 (true: 추가정보 입력 필요, false: 메인 홈으로 이동)", example = "false")
    private boolean isNewUser;

    @Schema(description = "로그인한 회원 기본 정보")
    private MemberInfo member;

    @Schema(description = "회원 기본 정보")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {

        @Schema(description = "회원 ID", example = "1")
        private Long memberId;

        @Schema(description = "닉네임", example = "플레이볼러")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "https://k.kakaocdn.net/...")
        private String profileImage;

        @Schema(description = "권한 (ROLE_USER | ROLE_ADMIN)", example = "ROLE_USER")
        private String role;
    }

    public static KakaoLoginResponse of(String accessToken, String refreshToken, String tokenType, boolean isNewUser, Member member) {
        return KakaoLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .isNewUser(isNewUser)
                .member(MemberInfo.builder()
                        .memberId(member.getMemberId())
                        .nickname(member.getNickname())
                        .profileImage(member.getProfileImage())
                        .role(member.getRole())
                        .build())
                .build();
    }
}
