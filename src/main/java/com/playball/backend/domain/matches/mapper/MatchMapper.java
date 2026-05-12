package com.playball.backend.domain.matches.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.playball.backend.domain.matches.dto.RandomMatchRequest;
import com.playball.backend.domain.matches.dto.RandomMatchResponse;
import com.playball.backend.domain.matches.entity.Match;



@Mapper
public interface MatchMapper {

    // 매치 생성
    void insertMatch(Match match);

    // 매치 수정
    void updateMatch(
            Long matchId,
            String title,
            LocalDateTime matchDate,
            Integer maxPlayers,
            Integer entryFee,
            String description
    );

    // 매치 엔티티 조회 및 반환
    Match findById(Long id);

    // 랜덤 매칭 요청 및 응답
    RandomMatchResponse findRandomMatch(RandomMatchRequest request);

    // 경기 리스트로 조회 및 반환(페이징)
    List<Match> findAll(int offset, int size);

    void deleteMatch(Long matchId);
}