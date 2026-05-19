package com.playball.backend.domain.matching.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.matching.dto.MatchRealtimeResponse;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchRealtimeService {

    private final MatchingRepository matchRepository;

    @Transactional
    public MatchRealtimeResponse leaveMatch(Long matchId, Long userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        int updatedPlayers = Math.max(0, match.getCurrentPlayers() - 1);
        MatchStatus updatedStatus = updatedPlayers < match.getMaxPlayers()
                ? MatchStatus.OPEN
                : MatchStatus.CLOSED;

        match.updatePlayerCount(updatedPlayers, updatedStatus);

        return MatchRealtimeResponse.builder()
                .matchId(match.getId())
                .currentPlayers(updatedPlayers)
                .maxPlayers(match.getMaxPlayers())
                .status(updatedStatus.name())
                .type("LEAVE")
                .message("사용자가 경기에서 퇴장했습니다.")
                .build();
    }
}
