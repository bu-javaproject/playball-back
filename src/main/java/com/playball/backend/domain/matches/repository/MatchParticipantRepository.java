package com.playball.backend.domain.matches.repository;

import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matching.entity.MatchParticipant;
import com.playball.backend.domain.matching.entity.ParticipantStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {

    List<MatchParticipant> findByMatchAndStatus(Match match, ParticipantStatus status);

    Optional<MatchParticipant> findByMatch_IdAndMember_MemberId(Long matchId, Long memberId);

    boolean existsByMatch_IdAndMember_MemberIdAndStatusIn(Long matchId, Long memberId, List<ParticipantStatus> statuses);
}
