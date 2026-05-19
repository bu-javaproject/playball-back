package com.playball.backend.domain.matching.Controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.playball.backend.domain.matching.dto.MatchJoinRequest;
import com.playball.backend.domain.matching.dto.MatchRealtimeResponse;
import com.playball.backend.domain.matching.dto.MatchedResponse;
import com.playball.backend.domain.matching.service.MatchRealtimeService;
import com.playball.backend.domain.matching.service.MatchingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MatchWebSocketController {

    private final MatchingService matchingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MatchRealtimeService matchRealtimeService;

    // 경기 참가
    @MessageMapping("/match/{matchId}/join")
    public void joinMatch(
            @DestinationVariable("matchId") Long matchId,
            @Payload MatchJoinRequest request) {

        MatchedResponse response = matchingService.joinMatch(matchId, request.getUserId());

        messagingTemplate.convertAndSend("/topic/match/" + matchId, response);
    }

    // 경기 퇴장
    @MessageMapping("/match/{matchId}/leave")
    public void leaveMatch(
            @DestinationVariable("matchId") Long matchId,
            @Payload MatchJoinRequest request) {

        MatchRealtimeResponse response = matchRealtimeService.leaveMatch(
                matchId,
                request.getUserId());

        messagingTemplate.convertAndSend("/topic/match/" + matchId, response);
    }

    // 실시간 인원 업데이트

    // 매칭 상태 알림

}