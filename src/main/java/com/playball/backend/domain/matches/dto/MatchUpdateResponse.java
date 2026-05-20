package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;

import com.playball.backend.domain.matches.entity.MatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "경기 수정 응답")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchUpdateResponse {

    @Schema(description = "경기 ID", example = "1")
    private Long matchId;

    @Schema(description = "경기 제목", example = "강남 풋살 모집 (초보 환영)")
    private String title;

    @Schema(description = "경기 일시", example = "2024-06-15T18:00:00")
    private LocalDateTime matchDate;

    @Schema(description = "최대 참가 인원", example = "12")
    private Integer maxPlayers;

    @Schema(description = "현재 참가 인원", example = "5")
    private Integer currentPlayers;

    @Schema(description = "공지 메시지", example = "풋살화 필참, 음료 제공")
    private String description;

    @Schema(description = "참가비", example = "3000")
    private Integer entryFee;

    @Schema(description = "경기 상태 (OPEN | CLOSED | COMPLETED | DELETED)", example = "OPEN")
    private MatchStatus status;

    @Schema(description = "수정일시", example = "2024-05-10T12:00:00")
    private LocalDateTime updatedAt;
}
