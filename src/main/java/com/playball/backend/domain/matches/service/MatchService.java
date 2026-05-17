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
import com.playball.backend.domain.matches.dto.NearbyMatchView;
import com.playball.backend.domain.matches.dto.RandomMatchRequest;
import com.playball.backend.domain.matches.dto.RandomMatchResponse;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matches.entity.SportType;
import com.playball.backend.domain.matches.repository.MatchParticipantRepository;
import com.playball.backend.domain.matching.entity.MatchParticipant;
import com.playball.backend.domain.matching.entity.ParticipantStatus;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import com.playball.backend.domain.member.entity.Member;
import com.playball.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchingRepository matchingRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MatchCreateResponse createMatch(MatchCreateRequest request, Long hostId) {
        Member host = memberRepository.findById(hostId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SportType sportType;
        try {
            sportType = SportType.valueOf(request.getSportType());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        Match match = Match.builder()
                .hostId(hostId)
                .title(request.getTitle())
                .sportType(sportType)
                .matchDate(request.getMatchDate())
                .locationName(request.getLocationName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .maxPlayers(request.getMaxPlayers())
                .currentPlayers(1)
                .gender(request.getGender())
                .ageRange(request.getAgeRange())
                .skillLevel(request.getSkillLevel())
                .entryFee(request.getEntryFee() != null ? request.getEntryFee() : 0)
                .description(request.getDescription())
                .status(MatchStatus.OPEN)
                .build();

        Match saved = matchingRepository.save(match);

        matchParticipantRepository.save(MatchParticipant.builder()
                .match(saved)
                .member(host)
                .status(ParticipantStatus.APPROVED)
                .build());

        return MatchCreateResponse.builder()
                .matchId(saved.getId())
                .title(saved.getTitle())
                .sportType(saved.getSportType().name())
                .matchDate(saved.getMatchDate())
                .locationName(saved.getLocationName())
                .latitude(saved.getLatitude())
                .longitude(saved.getLongitude())
                .address(saved.getAddress())
                .maxPlayers(saved.getMaxPlayers())
                .currentPlayers(saved.getCurrentPlayers())
                .gender(saved.getGender())
                .ageRange(saved.getAgeRange())
                .skillLevel(saved.getSkillLevel() != null ? saved.getSkillLevel().name() : null)
                .entryFee(saved.getEntryFee())
                .description(saved.getDescription())
                .status(MatchStatus.OPEN)
                .build();
    }

    @Transactional
    public MatchUpdateResponse updateMatch(Long matchId, MatchUpdateRequest request, Long memberId) {
        Match match = getActiveMatch(matchId);
        if (match.getHostId() == null || !match.getHostId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (request.getMaxPlayers() != null && request.getMaxPlayers() < match.getCurrentPlayers()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

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

    public RandomMatchResponse findRandomMatch(RandomMatchRequest request, Long memberId) {
        String gender = request.getGender() != null ? request.getGender().name() : null;
        return matchingRepository.findRandomMatch(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                request.getSportType(),
                request.getDate(),
                request.getMaxFee(),
                request.getSkillLevel(),
                gender,
                request.getAgeRange(),
                memberId
        ).map(view -> RandomMatchResponse.builder()
                .matchId(view.getMatchId())
                .title(view.getTitle())
                .sportType(view.getSportType())
                .matchDate(view.getMatchDate())
                .locationName(view.getLocationName())
                .entryFee(view.getEntryFee())
                .currentPlayers(view.getCurrentPlayers())
                .maxPlayers(view.getMaxPlayers())
                .distance(view.getDistance())
                .build()
        ).orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));
    }

    public MatchDetailResponse getMatch(Long matchId) {
        Match match = getActiveMatch(matchId);

        List<MatchDetailResponse.MemberInfo> joinedMembers = matchParticipantRepository
                .findByMatchAndStatus(match, ParticipantStatus.APPROVED)
                .stream()
                .map(p -> MatchDetailResponse.MemberInfo.from(p.getMember()))
                .toList();

        return MatchDetailResponse.builder()
                .match(MatchDetailResponse.MatchInfo.from(match))
                .joinedMembers(joinedMembers)
                .build();
    }

    public List<MatchResponse> getMyMatches(Long memberId) {
        return matchParticipantRepository
                .findMyMatches(memberId, ParticipantStatus.APPROVED)
                .stream()
                .map(mp -> toResponse(mp.getMatch()))
                .toList();
    }

    public List<MatchResponse> getMatches(Double latitude, Double longitude, Double radius,
                                           String sportType, int page, int size) {
        if (latitude != null && longitude != null) {
            return matchingRepository
                    .findNearbyMatches(latitude, longitude, radius, sportType)
                    .stream()
                    .map(this::toNearbyResponse)
                    .toList();
        }
        return matchingRepository
                .findByStatusNot(MatchStatus.DELETED, PageRequest.of(page, size))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteMatch(Long matchId, Long memberId) {
        Match match = getActiveMatch(matchId);
        if (match.getHostId() == null || !match.getHostId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
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

    private MatchResponse toNearbyResponse(NearbyMatchView view) {
        return MatchResponse.builder()
                .matchId(view.getMatchId())
                .title(view.getTitle())
                .sportType(view.getSportType())
                .matchDate(view.getMatchDate())
                .locationName(view.getLocationName())
                .latitude(view.getLatitude())
                .longitude(view.getLongitude())
                .maxPlayers(view.getMaxPlayers())
                .currentPlayers(view.getCurrentPlayers())
                .status(MatchStatus.valueOf(view.getStatus()))
                .build();
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
                .gender(match.getGender())
                .ageRange(match.getAgeRange())
                .status(match.getStatus())
                .updatedAt(match.getUpdatedAt())
                .build();
    }
}
