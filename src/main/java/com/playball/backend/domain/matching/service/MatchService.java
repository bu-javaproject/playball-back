package com.playball.backend.domain.matching.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.playball.backend.domain.matching.dto.MatchResponse;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matching.repository.MatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    @Transactional
    public MatchResponse joinMatch(Long matchId, Long userId) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("경기를 찾을 수 없습니다."));

        // 모집 마감 확인
        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            throw new RuntimeException("정원이 가득 찼습니다.");
        }

        // 현재 인원 증가
        Integer updatedPlayers = match.getCurrentPlayers() + 1;

        // 상태 변경
        MatchStatus status = updatedPlayers.equals(match.getMaxPlayers())
                ? MatchStatus.CLOSED
                : MatchStatus.OPEN;

        Match updatedMatch = Match.builder()
                .id(match.getId())
                .title(match.getTitle())
                .sportType(match.getSportType())
                .matchDate(match.getMatchDate())
                .locationName(match.getLocationName())
                .latitude(match.getLatitude())
                .longitude(match.getLongitude())
                .address(match.getAddress())
                .maxPlayers(match.getMaxPlayers())
                .currentPlayers(updatedPlayers)
                .skillLevel(match.getSkillLevel())
                .entryFee(match.getEntryFee())
                .description(match.getDescription())
                .status(status)
                .updatedAt(match.getUpdatedAt())
                .build();

        matchRepository.save(updatedMatch);

        return MatchResponse.builder()
                .matchId(match.getId())
                .currentPlayers(updatedPlayers)
                .maxPlayers(match.getMaxPlayers())
                .status(status.name())
                .build();
    }
}