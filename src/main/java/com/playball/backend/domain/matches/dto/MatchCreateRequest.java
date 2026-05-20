package com.playball.backend.domain.matches.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Schema(description = "경기 생성 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchCreateRequest {

    @Schema(description = "경기 제목", example = "강남 풋살 같이 하실 분!")
    @NotBlank
    private String sportType;

    @Schema(description = "종목 (SOCCER | BASKETBALL | RUNNING | BADMINTON)", example = "SOCCER")
    @NotBlank
    private String title;

    @Schema(description = "경기 일시 (현재 이후)", example = "2024-06-01T18:00:00")
    @NotNull
    @FutureOrPresent
    private LocalDateTime matchDate;

    @Schema(description = "장소명", example = "강남 스포츠센터")
    private String locationName;

    @Schema(description = "위도", example = "37.5665")
    @NotNull
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    @NotNull
    private Double longitude;

    @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "최대 참가 인원 (1명 이상)", example = "10")
    @NotNull
    @Min(1)
    private Integer maxPlayers;

    @Schema(description = "성별 제한 (null: 무관, M: 남성만, F: 여성만)", example = "null")
    private String gender;

    @Schema(description = "연령대 제한 (null: 무관, 20: 20대, 30: 30대 등)", example = "20")
    private Integer ageRange;

    @Schema(description = "실력 수준 제한 (BEGINNER | INTERMEDIATE | ADVANCED)", example = "INTERMEDIATE")
    private SkillLevel skillLevel;

    @Schema(description = "참가비 (0 이상, null이면 0원으로 처리)", example = "5000")
    @Min(0)
    private Integer entryFee;

    @Schema(description = "공지 메시지", example = "초보 환영! 풋살화 필참")
    private String description;

    public enum SkillLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }
}
