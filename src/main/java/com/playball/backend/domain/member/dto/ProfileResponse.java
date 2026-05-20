package com.playball.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "회원 프로필 응답")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "닉네임", example = "플레이볼러")
    private String nickname;

    @Schema(description = "성별 (M | F)", example = "M")
    private String gender;

    @Schema(description = "나이", example = "25")
    private Integer age;

    @Schema(description = "프로필 이미지 URL", example = "https://k.kakaocdn.net/...")
    private String profileImage;

    @Schema(description = "활동지역 주소", example = "서울특별시 강남구")
    private String address;

    @Schema(description = "실력 수준 (BEGINNER | INTERMEDIATE | ADVANCED)", example = "INTERMEDIATE")
    private String skillLevel;

    @Schema(description = "선호 포지션", example = "공격수")
    private String preferredPosition;

    @Schema(description = "선호 종목 목록", example = "[\"SOCCER\", \"BASKETBALL\"]")
    private List<String> favoriteSports;
}
