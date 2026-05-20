package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "랜덤 매칭 결과")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomMatchResponse {

    @Schema(description = "추천된 경기 ID", example = "42")
    private Long matchId;

    @Schema(description = "경기 제목", example = "강남 풋살 같이 하실 분!")
    private String title;

    @Schema(description = "종목", example = "SOCCER")
    private String sportType;

    @Schema(description = "경기 일시", example = "2024-06-01T18:00:00")
    private LocalDateTime matchDate;

    @Schema(description = "장소명", example = "강남 스포츠센터")
    private String locationName;

    @Schema(description = "참가비", example = "5000")
    private Integer entryFee;

    @Schema(description = "현재 참가 인원", example = "5")
    private Integer currentPlayers;

    @Schema(description = "최대 참가 인원", example = "10")
    private Integer maxPlayers;

    @Schema(description = "현재 위치로부터의 거리 (km)", example = "2.3")
    private Double distance;
}
