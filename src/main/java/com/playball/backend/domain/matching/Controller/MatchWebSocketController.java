package com.playball.backend.domain.matching.Controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.playball.backend.domain.matching.dto.MatchJoinRequest;
import com.playball.backend.domain.matching.dto.MatchRealtimeResponse;
import com.playball.backend.domain.matching.dto.MatchResponse;
import com.playball.backend.domain.matching.service.MatchRealtimeService;
import com.playball.backend.domain.matching.service.MatchService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchWebSocketController {

    private final MatchService matchService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MatchRealtimeService matchRealtimeService;

    // 경기 참가
    @MessageMapping("/match/{matchId}/join")
    public void joinMatch(
            @DestinationVariable Long matchId,
            @Payload MatchJoinRequest request) {

        MatchResponse response = matchService.joinMatch(matchId, request.getUserId());

        // 실시간 브로드캐스트
        messagingTemplate.convertAndSend(
                "/topic/match/" + matchId,
                response);
    }

    // 경기 퇴장
    @MessageMapping("/match/{matchId}/leave")
    public void leaveMatch(
            @DestinationVariable Long matchId,
            MatchJoinRequest request) {

        MatchRealtimeResponse response = matchRealtimeService.leaveMatch(
                matchId,
                request.getUserId());

        messagingTemplate.convertAndSend(
                "/topic/match/" + matchId,
                response);
    }

    // 실시간 인원 업데이트

    // 매칭 상태 알림

}