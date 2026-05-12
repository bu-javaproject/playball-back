package com.playball.backend.domain.matching.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchedResponse {

    private Long matchId;
    private Integer currentPlayers;
    private Integer maxPlayers;
    private String status;
}