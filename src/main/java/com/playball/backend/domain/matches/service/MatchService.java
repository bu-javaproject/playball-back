package com.playball.backend.domain.matches.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.matches.dto.MatchCreateRequest;
import com.playball.backend.domain.matches.dto.MatchCreateResponse;
import com.playball.backend.domain.matches.dto.MatchDetailResponse;
import com.playball.backend.domain.matches.dto.MatchResponse;
import com.playball.backend.domain.matches.dto.MatchUpdateRequest;
import com.playball.backend.domain.matches.dto.MatchUpdateResponse;
import com.playball.backend.domain.matches.dto.RandomMatchRequest;
import com.playball.backend.domain.matches.dto.RandomMatchResponse;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matches.entity.SportType;
import com.playball.backend.domain.matches.repository.MatchParticipantRepository;
import com.playball.backend.domain.matching.repository.MatchingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchingRepository matchingRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    @Transactional
    public MatchCreateResponse createMatch(MatchCreateRequest request) {
        Match match = Match.builder()
                .title(request.getTitle())
                .sportType(SportType.valueOf(request.getSportType()))
                .matchDate(request.getMatchDate())
                .locationName(request.getLocationName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .maxPlayers(request.getMaxPlayers())
                .currentPlayers(1)
                .skillLevel(request.getSkillLevel())
                .entryFee(request.getEntryFee())
                .description(request.getDescription())
                .status(MatchStatus.OPEN)
                .build();

        Match saved = matchingRepository.save(match);

        return MatchCreateResponse.builder()
                .matchId(saved.getId())
                .title(saved.getTitle())
                .sportType(saved.getSportType().name())
                .matchDate(saved.getMatchDate())
                .locationName(saved.getLocationName())
                .latitude(saved.getLatitude())
                .longitude(saved.getLongitude())
                .maxPlayers(saved.getMaxPlayers())
                .currentPlayers(saved.getCurrentPlayers())
                .skillLevel(saved.getSkillLevel() != null ? saved.getSkillLevel().name() : null)
                .entryFee(saved.getEntryFee())
                .status(MatchStatus.OPEN)
                .build();
    }

    @Transactional
    public MatchUpdateResponse updateMatch(Long matchId, MatchUpdateRequest request) {
        Match match = getActiveMatch(matchId);

        match.update(
                request.getTitle(),
                request.getMatchDate(),
                request.getMaxPlayers(),
                request.getEntryFee(),
                request.getDescription()
        );

        return MatchUpdateResponse.builder()
                .matchId(match.getId())
                .title(match.getTitle())
                .matchDate(match.getMatchDate())
                .maxPlayers(match.getMaxPlayers())
                .currentPlayers(match.getCurrentPlayers())
                .description(match.getDescription())
                .entryFee(match.getEntryFee())
                .status(match.getStatus())
                .updatedAt(match.getUpdatedAt())
                .build();
    }

    public RandomMatchResponse findRandomMatch(RandomMatchRequest request) {
        return matchingRepository.findRandomMatch(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                request.getSportType(),
                request.getDate(),
                request.getMaxFee(),
                request.getSkillLevel()
        ).map(view -> RandomMatchResponse.builder()
                .matchId(view.getMatchId())
                .title(view.getTitle())
                .sportType(view.getSportType())
                .matchDate(view.getMatchDate())
                .locationName(view.getLocationName())
                .entryFee(view.getEntryFee())
                .distance(view.getDistance())
                .build()
        ).orElse(null);
    }

    public MatchDetailResponse getMatch(Long matchId) {
        Match match = matchingRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        List<MatchDetailResponse.MemberInfo> joinedMembers = matchParticipantRepository
                .findByMatch(match)
                .stream()
                .map(p -> MatchDetailResponse.MemberInfo.from(p.getMember()))
                .toList();

        return MatchDetailResponse.builder()
                .match(MatchDetailResponse.MatchInfo.from(match))
                .joinedMembers(joinedMembers)
                .build();
    }

    public List<MatchResponse> getMatches(int page, int size) {
        return matchingRepository
                .findByStatusNot(MatchStatus.DELETED, PageRequest.of(page, size))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteMatch(Long matchId) {
        Match match = getActiveMatch(matchId);
        match.markDeleted();
    }

    private Match getActiveMatch(Long matchId) {
        Match match = matchingRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));
        if (match.getStatus() == MatchStatus.DELETED) {
            throw new CustomException(ErrorCode.MATCH_DELETED);
        }
        return match;
    }

    private MatchResponse toResponse(Match match) {
        return MatchResponse.builder()
                .matchId(match.getId())
                .title(match.getTitle())
                .sportType(match.getSportType() != null ? match.getSportType().name() : null)
                .matchDate(match.getMatchDate())
                .locationName(match.getLocationName())
                .latitude(match.getLatitude())
                .longitude(match.getLongitude())
                .maxPlayers(match.getMaxPlayers())
                .currentPlayers(match.getCurrentPlayers())
                .status(match.getStatus())
                .updatedAt(match.getUpdatedAt())
                .build();
    }
}
