package com.playball.backend.domain.matches.dto;

import java.util.List;

import com.playball.backend.domain.matches.entity.Match;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.playball.backend.member.entity.Member;

// 경기 상세 조회시 응답 DTO
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchDetailResponse {
    private Match match;

    // 참가한 회원 목록
    private List<Member> joinedMembers;

}
