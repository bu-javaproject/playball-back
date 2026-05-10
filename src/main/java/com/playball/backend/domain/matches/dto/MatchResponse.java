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
public class MatchResponse {

    private Long matchId;
    private String title;
    private String sportType;
    private LocalDateTime matchDate;

    private String locationName;
    private Double latitude;
    private Double longitude;

    private Integer maxPlayers;
    private Integer currentPlayers;

    // private String skillLevel;
    // private Integer entryFee;

    private MatchStatus status;

    private LocalDateTime updatedAt;
}