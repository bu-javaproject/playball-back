package com.playball.backend.domain.compliment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.playball.backend.domain.compliment.entity.Compliment;

import java.util.List;

public interface ComplimentRepository extends JpaRepository<Compliment, Long> {

    int countByMatchIdAndRaterIdAndRateeId(Long matchId, Long raterId, Long rateeId);

    @Query("SELECT c FROM Compliment c WHERE c.rateeId = :memberId " +
           "AND (:cursor IS NULL OR c.complimentId < :cursor) " +
           "ORDER BY c.complimentId DESC")
    List<Compliment> findReceivedByMemberWithCursor(
            @Param("memberId") Long memberId,
            @Param("cursor") Long cursor,
            Pageable pageable);

    List<Compliment> findByRaterIdAndMatchIdOrderByComplimentIdDesc(Long raterId, Long matchId);

    List<Compliment> findByRateeIdAndMatchIdOrderByComplimentIdDesc(Long rateeId, Long matchId);
}
