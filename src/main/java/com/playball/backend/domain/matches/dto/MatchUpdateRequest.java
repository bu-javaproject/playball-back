package com.playball.backend.domain.matches.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Schema(description = "경기 수정 요청 (변경하지 않을 필드는 null 또는 생략)")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchUpdateRequest {

    @Schema(description = "변경할 경기 제목 (null이면 변경 안 함)", example = "강남 풋살 모집 (초보 환영)")
    private String title;

    @Schema(description = "변경할 경기 일시 (현재 이후, null이면 변경 안 함)", example = "2024-06-15T18:00:00")
    @FutureOrPresent
    private LocalDateTime matchDate;

    @Schema(description = "변경할 최대 참가 인원 (1 이상, null이면 변경 안 함)", example = "12")
    @Min(1)
    private Integer maxPlayers;

    @Schema(description = "변경할 참가비 (null이면 변경 안 함)", example = "3000")
    private Integer entryFee;

    @Schema(description = "변경할 공지 메시지 (null이면 변경 안 함)", example = "풋살화 필참, 음료 제공")
    private String description;
}
