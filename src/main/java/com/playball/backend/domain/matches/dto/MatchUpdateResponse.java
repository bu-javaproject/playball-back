package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;

import com.playball.backend.domain.matches.entity.MatchStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchUpdateResponse {

    private Long matchId;

    private String title;
    private LocalDateTime matchDate;

    private Integer maxPlayers;
    private Integer currentPlayers;

    private String description;
    private Integer entryFee;

    private MatchStatus status;

    private LocalDateTime updatedAt;
}