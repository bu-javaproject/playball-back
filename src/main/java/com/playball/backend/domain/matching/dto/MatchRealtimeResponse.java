package com.playball.backend.domain.matching.dto;


import lombok.Builder;
import lombok.Getter;

// 실시간 응답 DTO
@Getter
@Builder
public class MatchRealtimeResponse {

    private Long matchId;

    // 현재 인원
    private Integer currentPlayers;

    // 최대 인원
    private Integer maxPlayers;

    // 상태
    private String status;

    // 이벤트 타입
    private String type;

    // 메시지
    private String message;
}