package com.playball.backend.domain.matching.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.matching.dto.MatchedResponse;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matching.repository.MatchingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingRepository matchRepository;

    @Transactional
    public MatchedResponse joinMatch(Long matchId, Long userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            throw new CustomException(ErrorCode.MATCH_FULL);
        }

        int updatedPlayers = match.getCurrentPlayers() + 1;
        MatchStatus newStatus = updatedPlayers == match.getMaxPlayers()
                ? MatchStatus.CLOSED
                : MatchStatus.OPEN;

        match.incrementPlayers(newStatus);

        return MatchedResponse.builder()
                .matchId(match.getId())
                .currentPlayers(updatedPlayers)
                .maxPlayers(match.getMaxPlayers())
                .status(newStatus.name())
                .build();
    }
}
