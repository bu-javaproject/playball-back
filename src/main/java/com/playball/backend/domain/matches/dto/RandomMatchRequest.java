package com.playball.backend.domain.matches.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 랜덤 매칭 요청 DTO.
 *
 * 위도·경도는 필수이며, radius(기본 5.0 km) 이내의 OPEN 상태 경기 중
 * 나머지 선택 조건(종목·날짜·성별·연령·실력·참가비)을 적용해 1건을 무작위 반환한다.
 * null인 선택 조건은 SQL에서 필터를 건너뛴다(IS NULL OR ... 패턴).
 */
@Schema(description = "랜덤 매칭 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomMatchRequest {

    @Schema(description = "현재 위치 위도 (필수)", example = "37.5665")
    @NotNull
    private Double latitude;

    @Schema(description = "현재 위치 경도 (필수)", example = "126.9780")
    @NotNull
    private Double longitude;

    @Schema(description = "현재 주소 (선택)", example = "서울특별시 강남구")
    private String address;

    @Schema(description = "희망 경기 날짜 (선택, 미입력 시 날짜 무관)", example = "2024-06-01")
    private LocalDate date;

    @Schema(description = "희망 종목 (필수, SOCCER | BASKETBALL | RUNNING | BADMINTON)", example = "SOCCER")
    @NotBlank
    private String sportType;

    @Schema(description = "선호 포지션 (선택)", example = "공격수")
    private String preferredPosition;

    @Schema(description = "성별 제한 필터 (선택, M | F)", example = "M")
    private Gender gender;

    @Schema(description = "연령대 필터 (선택, 20: 20대 등)", example = "20")
    private Integer ageRange;

    @Schema(description = "실력 수준 필터 (선택, BEGINNER | INTERMEDIATE | ADVANCED)", example = "INTERMEDIATE")
    private String skillLevel;

    @Schema(description = "최대 참가비 필터 (선택, 이 금액 이하의 경기만 조회)", example = "5000")
    private Integer maxFee;

    // 미입력 시 기본 5 km 반경 검색 — DB 쿼리에서 Haversine 공식으로 거리 계산 후 이 값 이하만 조회
    @Schema(description = "검색 반경 (km, 기본 5.0)", example = "5.0")
    @Builder.Default
    private Double radius = 5.0;

    public enum Gender {
        M, F
    }
}
