package com.playball.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "위치 정보 업데이트 요청")
@Getter
@NoArgsConstructor
public class LocationUpdateRequest {

    @Schema(description = "위도", example = "37.5665")
    @NotNull
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    @NotNull
    private Double longitude;

    @Schema(description = "주소 (선택)", example = "서울특별시 강남구 테헤란로")
    private String address;
}
