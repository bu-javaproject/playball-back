package com.playball.backend.domain.matches.repository;

import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matching.entity.MatchParticipant;
import com.playball.backend.domain.matching.entity.ParticipantStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {

    List<MatchParticipant> findByMatchAndStatus(Match match, ParticipantStatus status);

    Optional<MatchParticipant> findByMatch_IdAndMember_MemberId(Long matchId, Long memberId);

    boolean existsByMatch_IdAndMember_MemberIdAndStatusIn(Long matchId, Long memberId, List<ParticipantStatus> statuses);

    @Query("SELECT mp FROM MatchParticipant mp JOIN FETCH mp.match m WHERE mp.member.memberId = :memberId AND mp.status = :status AND m.status != com.playball.backend.domain.matches.entity.MatchStatus.DELETED ORDER BY m.matchDate DESC")
    List<MatchParticipant> findMyMatches(@Param("memberId") Long memberId, @Param("status") ParticipantStatus status);
}
