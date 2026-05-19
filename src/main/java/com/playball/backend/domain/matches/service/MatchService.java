package com.playball.backend.domain.matches.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.matches.dto.*;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matches.entity.SportType;
import com.playball.backend.domain.matches.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

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

        matchRepository.save(match);

        return MatchCreateResponse.builder()
                .matchId(match.getId())
                .title(match.getTitle())
                .sportType(match.getSportType().name())
                .matchDate(match.getMatchDate())
                .locationName(match.getLocationName())
                .latitude(match.getLatitude())
                .longitude(match.getLongitude())
                .maxPlayers(match.getMaxPlayers())
                .currentPlayers(match.getCurrentPlayers())
                .skillLevel(match.getSkillLevel() != null ? match.getSkillLevel().name() : null)
                .entryFee(match.getEntryFee())
                .status(MatchStatus.OPEN)
                .build();
    }

    @Transactional
    public MatchUpdateResponse updateMatch(Long matchId, MatchUpdateRequest request) {
        Match match = findActiveMatchOrThrow(matchId);

        if (request.getTitle() != null) match.setTitle(request.getTitle());
        if (request.getMatchDate() != null) match.setMatchDate(request.getMatchDate());
        if (request.getMaxPlayers() != null) match.setMaxPlayers(request.getMaxPlayers());
        if (request.getEntryFee() != null) match.setEntryFee(request.getEntryFee());
        if (request.getDescription() != null) match.setDescription(request.getDescription());

        matchRepository.save(match);

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

    @Transactional(readOnly = true)
    public MatchResponse getMatch(Long matchId) {
        return toResponse(findActiveMatchOrThrow(matchId));
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatches(int page, int size) {
        return matchRepository.findByStatusNotOrderByMatchDateDesc(
                        MatchStatus.DELETED, PageRequest.of(page, size))
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RandomMatchResponse findRandomMatch(RandomMatchRequest request) {
        String skillLevel = request.getSkillLevel() != null ? String.valueOf(request.getSkillLevel()) : null;

        return matchRepository.findRandomMatch(
                        request.getLatitude(), request.getLongitude(),
                        request.getSportType(), request.getDate(),
                        request.getMaxFee(), skillLevel, request.getRadius())
                .map(row -> RandomMatchResponse.builder()
                        .matchId(((Number) row[0]).longValue())
                        .title((String) row[4])
                        .sportType((String) row[5])
                        .matchDate(((java.sql.Timestamp) row[6]).toLocalDateTime())
                        .locationName((String) row[8])
                        .entryFee(row[13] != null ? ((Number) row[13]).intValue() : null)
                        .distance(((Number) row[row.length - 1]).doubleValue())
                        .build())
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));
    }

    @Transactional
    public void deleteMatch(Long matchId) {
        Match match = findActiveMatchOrThrow(matchId);
        match.setStatus(MatchStatus.DELETED);
        matchRepository.save(match);
    }

    private Match findActiveMatchOrThrow(Long matchId) {
        return matchRepository.findByIdAndStatusNot(matchId, MatchStatus.DELETED)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));
    }

    private MatchResponse toResponse(Match match) {
        return MatchResponse.builder()
                .matchId(match.getId())
                .title(match.getTitle())
                .sportType(match.getSportType().name())
                .matchDate(match.getMatchDate())
                .locationName(match.getLocationName())
                .latitude(match.getLatitude())
                .longitude(match.getLongitude())
                .maxPlayers(match.getMaxPlayers())
                .currentPlayers(match.getCurrentPlayers())
                .skillLevel(match.getSkillLevel() != null ? match.getSkillLevel().name() : null)
                .entryFee(match.getEntryFee())
                .status(match.getStatus())
                .updatedAt(match.getUpdatedAt())
                .build();
    }
}