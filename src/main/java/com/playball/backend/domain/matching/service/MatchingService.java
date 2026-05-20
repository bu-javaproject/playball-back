package com.playball.backend.domain.matching.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.matching.dto.MatchedResponse;
import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.matching.entity.MatchParticipant;
import com.playball.backend.domain.matching.entity.ParticipantStatus;
import com.playball.backend.domain.matches.repository.MatchParticipantRepository;
import com.playball.backend.domain.matching.repository.MatchingRepository;
import com.playball.backend.domain.member.entity.Member;
import com.playball.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

/**
 * 매칭 참여 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingRepository matchRepository;
    private final MemberRepository memberRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    /**
     * 사용자를 매치에 참가시킨다.
     * 삭제된 매치, 정원 초과, 중복 참가 요청은 예외로 처리한다.
     * 참가 후 정원이 꽉 차면 매치 상태를 CLOSED로 변경한다.
     */
    @Transactional
    public MatchedResponse joinMatch(Long matchId, Long userId) {
        // 매치 존재 여부 확인
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        // 삭제된 매치에는 참가 불가
        if (match.getStatus() == MatchStatus.DELETED) {
            throw new CustomException(ErrorCode.MATCH_DELETED);
        }

        // 정원이 가득 찬 매치에는 참가 불가
        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            throw new CustomException(ErrorCode.MATCH_FULL);
        }

        // PENDING 또는 APPROVED 상태로 이미 참가 신청한 경우 중복 참가 방지
        boolean alreadyJoined = matchParticipantRepository.existsByMatch_IdAndMember_MemberIdAndStatusIn(
                matchId, userId, List.of(ParticipantStatus.PENDING, ParticipantStatus.APPROVED));
        if (alreadyJoined) {
            throw new CustomException(ErrorCode.ALREADY_JOINED);
        }

        // 사용자 존재 여부 확인
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 참가 후 현재 인원 계산 및 정원 도달 시 CLOSED 상태로 전환
        int updatedPlayers = match.getCurrentPlayers() + 1;
        MatchStatus newStatus = updatedPlayers == match.getMaxPlayers()
                ? MatchStatus.CLOSED
                : MatchStatus.OPEN;

        match.incrementPlayers(newStatus);

        // 참가자 레코드 저장 (즉시 APPROVED 처리)
        matchParticipantRepository.save(MatchParticipant.builder()
                .match(match)
                .member(member)
                .status(ParticipantStatus.APPROVED)
                .build());

        return MatchedResponse.builder()
                .matchId(match.getId())
                .currentPlayers(updatedPlayers)
                .maxPlayers(match.getMaxPlayers())
                .status(newStatus.name())
                .build();
    }
}
