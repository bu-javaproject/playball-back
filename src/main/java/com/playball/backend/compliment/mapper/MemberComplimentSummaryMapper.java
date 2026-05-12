package com.playball.backend.compliment.mapper;

import com.playball.backend.compliment.dto.MemberComplimentSummaryDTO;
import com.playball.backend.compliment.enums.ComplimentTag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface MemberComplimentSummaryMapper {

    /** 회원의 특정 태그 카운트 +1 (upsert) */
    void incrementCount(@Param("memberId") Long memberId,
                        @Param("tag") ComplimentTag tag);

    /** 회원의 모든 태그별 카운트 조회 */
    List<MemberComplimentSummaryDTO> findByMemberId(@Param("memberId") Long memberId);

    /** 회원의 전체 칭찬 합계 */
    int sumCountByMemberId(@Param("memberId") Long memberId);
}
