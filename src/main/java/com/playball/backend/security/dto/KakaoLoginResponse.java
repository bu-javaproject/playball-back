package com.playball.backend.security.dto;

import com.playball.backend.member.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Member;

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

    // 이 객체를 이렇게 만들 레시피
    public static KakaoLoginResponse of (String accessToken, String refreshToken, String tokenType, boolean isNewUser, MemberDTO member) {

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
