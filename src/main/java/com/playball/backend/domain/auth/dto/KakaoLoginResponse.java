package com.playball.backend.domain.auth.dto;

import com.playball.backend.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;

    //true -> 추가 정보 입력 화면으로 이동
    //false -> 메인 홈 화면으로 이동
    private boolean isNewUser;
    private MemberInfo member;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Long memberId;
        private String nickname;
        private String profileImage;
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
