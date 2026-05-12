package com.playball.backend.compliment.mapper;

import com.playball.backend.compliment.enums.ComplimentTag;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface ComplimentTagMapper {

    /** 한 칭찬에 태그 1개 추가 (여러 태그면 반복 호출) */
    void insertTag(@Param("complimentId") Long complimentId,
                   @Param("tag") ComplimentTag tag);

    /** 한 칭찬의 모든 태그 조회 */
    List<ComplimentTag> findByComplimentId(@Param("complimentId") Long complimentId);
}
