package com.playball.backend.domain.matching.Controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

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

    @MessageMapping("/match/{matchId}/join")
    public void joinMatch(
            @DestinationVariable("matchId") Long matchId,
            SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
        MatchedResponse response = matchingService.joinMatch(matchId, memberId);
        messagingTemplate.convertAndSend("/topic/match/" + matchId, response);
    }

    @MessageMapping("/match/{matchId}/leave")
    public void leaveMatch(
            @DestinationVariable("matchId") Long matchId,
            SimpMessageHeaderAccessor headerAccessor) {
        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
        MatchRealtimeResponse response = matchRealtimeService.leaveMatch(matchId, memberId);
        messagingTemplate.convertAndSend("/topic/match/" + matchId, response);
    }
}
