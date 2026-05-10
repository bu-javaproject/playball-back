package com.playball.backend.domain.matching.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.playball.backend.domain.matching.dto.MatchRealtimeResponse;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matching.repository.MatchingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchRealtimeService {

    private final MatchingRepository matchRepository;

    /**
     * 경기 참가
     */
    @Transactional
    public MatchRealtimeResponse joinMatch(
            Long matchId,
            Long userId
    ) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("경기를 찾을 수 없습니다."));

        // 경기 상태 확인
        if (match.getStatus() == MatchStatus.CLOSED) {
            throw new RuntimeException("이미 마감된 경기입니다.");
        }

        // 정원 확인
        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            throw new RuntimeException("정원이 가득 찼습니다.");
        }

        // 현재 인원 증가
        Integer updatedPlayers = match.getCurrentPlayers() + 1;

        // 정원 가득 차면 CLOSED
        MatchStatus updatedStatus =
                updatedPlayers.equals(match.getMaxPlayers())
                        ? MatchStatus.CLOSED
                        : MatchStatus.OPEN;

        // 엔티티 업데이트
        updateMatch(match, updatedPlayers, updatedStatus);

        return MatchRealtimeResponse.builder()
                .matchId(match.getId())
                .currentPlayers(updatedPlayers)
                .maxPlayers(match.getMaxPlayers())
                .status(updatedStatus.name())
                .type("JOIN")
                .message("사용자가 경기에 참가했습니다.")
                .build();
    }

    /**
     * 경기 퇴장
     */
    @Transactional
    public MatchRealtimeResponse leaveMatch(
            Long matchId,
            Long userId
    ) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("경기를 찾을 수 없습니다."));

        // 최소 0 보장
        Integer updatedPlayers =
                Math.max(0, match.getCurrentPlayers() - 1);

        MatchStatus updatedStatus =
                updatedPlayers < match.getMaxPlayers()
                        ? MatchStatus.OPEN
                        : MatchStatus.CLOSED;

        updateMatch(match, updatedPlayers, updatedStatus);

        return MatchRealtimeResponse.builder()
                .matchId(match.getId())
                .currentPlayers(updatedPlayers)
                .maxPlayers(match.getMaxPlayers())
                .status(updatedStatus.name())
                .type("LEAVE")
                .message("사용자가 경기에서 퇴장했습니다.")
                .build();
    }

    /**
     * Match 상태 업데이트
     */
    private void updateMatch(
            Match match,
            Integer currentPlayers,
            MatchStatus status
    ) {

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
                .currentPlayers(currentPlayers)
                .skillLevel(match.getSkillLevel())
                .entryFee(match.getEntryFee())
                .description(match.getDescription())
                .status(status)
                .updatedAt(match.getUpdatedAt())
                .build();

        matchRepository.save(updatedMatch);
    }
}