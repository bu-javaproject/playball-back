package com.playball.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "프로필 수정 요청 (변경하지 않을 필드는 null 또는 생략)")
@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    @Schema(description = "변경할 닉네임 (2~10자, null이면 변경 안 함)", example = "새닉네임")
    @Size(min = 2, max = 10)
    private String nickname;

    @Schema(description = "변경할 활동지역 주소 (null이면 변경 안 함)", example = "서울특별시 마포구")
    private String address;

    @Schema(description = "변경할 위도 (null이면 변경 안 함)", example = "37.5665")
    private Double latitude;

    @Schema(description = "변경할 경도 (null이면 변경 안 함)", example = "126.9780")
    private Double longitude;

    @Schema(description = "변경할 선호 종목 목록 (null이면 변경 안 함)", example = "[\"SOCCER\", \"RUNNING\"]")
    private List<String> favoriteSports;
}
