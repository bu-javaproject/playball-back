package com.playball.backend.compliment.mapper;

import com.playball.backend.compliment.dto.ComplimentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ComplimentMapper {

    /** 칭찬 1건 INSERT */
    void insertCompliment(ComplimentDTO compliment);

    /** 칭찬 단건 조회 */
    Optional<ComplimentDTO> findById(@Param("complimentId") Long complimentId);

    /** 같은 경기에서 같은 사람을 이미 칭찬했는지 확인 (Service 사전 검증용) */
    int countByMatchRaterRatee(@Param("matchId") Long matchId,
                               @Param("raterId") Long raterId,
                               @Param("rateeId") Long rateeId);

    /** 회원이 받은 칭찬 목록 (Cursor 페이지네이션) */
    List<ComplimentDTO> findReceivedByMember(@Param("memberId") Long memberId,
                                             @Param("cursor") Long cursor,
                                             @Param("size") int size);

    /** 한 경기에서 내가 한 칭찬 */
    List<ComplimentDTO> findGivenByRaterInMatch(@Param("raterId") Long raterId,
                                                @Param("matchId") Long matchId);

    /** 한 경기에서 내가 받은 칭찬 */
    List<ComplimentDTO> findReceivedByRateeInMatch(@Param("rateeId") Long rateeId,
                                                   @Param("matchId") Long matchId);
}
