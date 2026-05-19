package com.playball.backend.compliment.repository;

import com.playball.backend.compliment.entity.MemberComplimentSummary;
import com.playball.backend.compliment.entity.MemberComplimentSummaryId;
import com.playball.backend.compliment.enums.ComplimentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberComplimentSummaryRepository extends JpaRepository<MemberComplimentSummary, MemberComplimentSummaryId> {

    List<MemberComplimentSummary> findByMemberIdOrderByCountDesc(Long memberId);

    Optional<MemberComplimentSummary> findByMemberIdAndTag(Long memberId, ComplimentTag tag);

    @Query("SELECT COALESCE(SUM(s.count), 0) FROM MemberComplimentSummary s WHERE s.memberId = :memberId")
    int sumCountByMemberId(@Param("memberId") Long memberId);
}