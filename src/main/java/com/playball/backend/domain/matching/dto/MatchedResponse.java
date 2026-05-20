package com.playball.backend.domain.matching.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "경기 참가 결과")
@Getter
@Builder
public class MatchedResponse {

    @Schema(description = "경기 ID", example = "1")
    private Long matchId;

    @Schema(description = "참가 후 현재 참가 인원", example = "6")
    private Integer currentPlayers;

    @Schema(description = "최대 참가 인원", example = "10")
    private Integer maxPlayers;

    @Schema(description = "참가 후 경기 상태 (OPEN | CLOSED)", example = "OPEN")
    private String status;
}
