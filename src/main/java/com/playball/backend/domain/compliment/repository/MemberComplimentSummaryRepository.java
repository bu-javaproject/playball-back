package com.playball.backend.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.playball.backend.domain.compliment.entity.MemberComplimentSummary;
import com.playball.backend.domain.compliment.entity.MemberComplimentSummaryId;

import java.util.List;

public interface MemberComplimentSummaryRepository extends JpaRepository<MemberComplimentSummary, MemberComplimentSummaryId> {

    List<MemberComplimentSummary> findByMemberIdOrderByCountDesc(Long memberId);

    @Query("SELECT COALESCE(SUM(s.count), 0) FROM MemberComplimentSummary s WHERE s.memberId = :memberId")
    int sumCountByMemberId(@Param("memberId") Long memberId);

    // MySQL ON DUPLICATE KEY UPDATE — member_id + tag 복합 UNIQUE 전제
    @Modifying
    @Query(value = "INSERT INTO member_compliment_summary (member_id, tag, count) " +
                   "VALUES (:memberId, :tag, 1) ON DUPLICATE KEY UPDATE count = count + 1",
           nativeQuery = true)
    void incrementCount(@Param("memberId") Long memberId, @Param("tag") String tag);
}
