package com.playball.backend.domain.matches.service;

import java.util.List;

import org.springframework.stereotype.Service;

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
import com.playball.backend.domain.matches.mapper.MatchMapper;
import com.playball.backend.domain.matches.repository.MatchParticipantRepository;
import com.playball.backend.domain.matching.entity.MatchParticipant;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import com.playball.backend.member.entity.Member;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchMapper matchMapper;
    private final MatchingRepository matchingRepository;
    private final MatchParticipantRepository matchParticipantRepository;


    // 경기 생성
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

        matchMapper.insertMatch(match);

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

    // 경기 정보 수정
    public MatchUpdateResponse updateMatch(Long matchId, MatchUpdateRequest request) {

        // 수정 실행
        matchMapper.updateMatch(
                matchId,
                request.getTitle(),
                request.getMatchDate(),
                request.getMaxPlayers(),
                request.getEntryFee(),
                request.getDescription());

        // 수정된 매치 조회
        Match match = matchMapper.findById(matchId);

        // Response 변환
        return MatchUpdateResponse.builder()
                .matchId(match.getId())
                .title(match.getTitle())
                .matchDate(match.getMatchDate())
                .maxPlayers(match.getMaxPlayers())
                .currentPlayers(match.getCurrentPlayers())
                .description(match.getDescription())
                .entryFee(match.getEntryFee())
                .status(MatchStatus.OPEN)
                .updatedAt(match.getUpdatedAt())
                .build();
    }

    // 랜덤 매칭 요청
    public RandomMatchResponse findRandomMatch(RandomMatchRequest request) {

        return matchMapper.findRandomMatch(request);
    }

    // 경기 상세 조회 응답 
    public MatchDetailResponse getMatch(Long matchId) {

        // 경기 조회
        Match match = matchingRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("경기를 찾을 수 없습니다."));

        // 참가자 조회
        List<Member> joinedMembers = matchParticipantRepository
                .findByMatch(match)
                .stream()
                .map(MatchParticipant::getMember)
                .toList();

        // DTO 반환
        return MatchDetailResponse.builder()
                .match(match)
                .joinedMembers(joinedMembers)
                .build();
    }

    

    public List<MatchResponse> getMatches(int page, int size) {

        int offset = page * size;

        List<Match> matches = matchMapper.findAll(offset, size);

        return matches.stream()
                .map(this::toResponse)
                .toList();
    }

    // 매치 조회 DTO 변환 메소드
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
                // .skillLevel(match.getSkillLevel() != null ? match.getSkillLevel().name() :
                // null)
                // .entryFee(match.getEntryFee())
                .status(match.getStatus())
                .updatedAt(match.getUpdatedAt())
                .build();
    }

    public void deleteMatch(Long matchId) {

        // 존재 여부 확인
        Match match = matchMapper.findById(matchId);

        if (match == null) {
            throw new CustomException(ErrorCode.MATCH_NOT_FOUND);
        }

        // 이미 삭제된 경우 방지
        if (match.getStatus() == MatchStatus.DELETED) {
            throw new CustomException(ErrorCode.MATCH_DELETED);
        }

        matchMapper.deleteMatch(matchId);
    }
}