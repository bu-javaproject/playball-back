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

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingRepository matchRepository;
    private final MemberRepository memberRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    @Transactional
    public MatchedResponse joinMatch(Long matchId, Long userId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        if (match.getCurrentPlayers() >= match.getMaxPlayers()) {
            throw new CustomException(ErrorCode.MATCH_FULL);
        }

        boolean alreadyJoined = matchParticipantRepository.existsByMatch_IdAndMember_MemberIdAndStatusIn(
                matchId, userId, List.of(ParticipantStatus.PENDING, ParticipantStatus.APPROVED));
        if (alreadyJoined) {
            throw new CustomException(ErrorCode.ALREADY_JOINED);
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        int updatedPlayers = match.getCurrentPlayers() + 1;
        MatchStatus newStatus = updatedPlayers == match.getMaxPlayers()
                ? MatchStatus.CLOSED
                : MatchStatus.OPEN;

        match.incrementPlayers(newStatus);

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
