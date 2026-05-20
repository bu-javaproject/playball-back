package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomMatchResponse {

    private Long matchId;
    private String title;
    private String sportType;
    private LocalDateTime matchDate;
    private String locationName;
    private Integer entryFee;
    private Integer currentPlayers;
    private Integer maxPlayers;
    private Double distance;
}