package com.playball.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "회원가입 추가정보 입력 요청")
@Getter
@Setter
@NoArgsConstructor
public class SignUpCompleteRequest {

    @Schema(description = "닉네임 (2~10자)", example = "플레이볼러")
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다")
    private String nickname;

    @Schema(description = "성별 (M: 남성, F: 여성)", example = "M")
    @NotBlank(message = "성별은 필수입니다")
    private String gender;

    @Schema(description = "나이", example = "25")
    @NotNull(message = "나이는 필수입니다")
    private Integer age;

    @Schema(description = "활동지역 주소", example = "서울특별시 강남구")
    private String address;

    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "선호 종목 목록 (SOCCER | BASKETBALL | RUNNING | BADMINTON)", example = "[\"SOCCER\", \"BASKETBALL\"]")
    @NotNull(message = "종목을 최소 1개 선택해주세요")
    private List<String> favoriteSports;

    @Schema(description = "실력 수준 (BEGINNER | INTERMEDIATE | ADVANCED)", example = "INTERMEDIATE")
    private String skillLevel;

    @Schema(description = "선호 포지션", example = "공격수")
    private String preferredPosition;
}
