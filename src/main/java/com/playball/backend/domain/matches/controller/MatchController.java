package com.playball.backend.domain.matches.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.matches.dto.MatchCreateRequest;
import com.playball.backend.domain.matches.dto.MatchCreateResponse;
import com.playball.backend.domain.matches.dto.MatchResponse;
import com.playball.backend.domain.matches.dto.MatchUpdateRequest;
import com.playball.backend.domain.matches.dto.MatchUpdateResponse;
import com.playball.backend.domain.matches.service.MatchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // 경기 생성
    @PostMapping
    public ApiResponse<MatchCreateResponse> createMatch(
            @Valid @RequestBody MatchCreateRequest request
    ) {
        MatchCreateResponse response = matchService.createMatch(request);
        return ApiResponse.ok("경기 생성 성공", response);
    }

    // 겅기 수정
    @PatchMapping("/{matchId}")
    public ApiResponse<MatchUpdateResponse> updateMatch(
            @PathVariable Long matchId,
            @RequestBody MatchUpdateRequest request
    ) {
        MatchUpdateResponse updatedMatch = matchService.updateMatch(matchId, request);

        return ApiResponse.ok("경기 수정 성공", updatedMatch);
    }

    // 경기 상세 조회
    @GetMapping("/{matchId}")
    public ApiResponse<MatchResponse> getMatch(
            @PathVariable Long matchId
    ) {
        MatchResponse response = matchService.getMatch(matchId);
        return ApiResponse.ok("경기 조회 성공", response);
    }


    // 경기 목록 조회
    @GetMapping
    public ApiResponse<List<MatchResponse>> getMatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<MatchResponse> response = matchService.getMatches(page, size);
        return ApiResponse.ok("경기 목록 조회 성공", response);
    }

    // 경기 삭제
    @DeleteMapping("/{matchId}")
    public ApiResponse<Void> deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return ApiResponse.ok("경기가 삭제되었습니다", null);
    }

    // // 랜덤 매칭 요청
    // @PostMapping("/random")
    // public ApiResponse<RandomMatchResponse> getRandomMatch(
    //         @Valid @RequestBody RandomMatchRequest request
    // ) {
    //     RandomMatchResponse response = matchService.findRandomMatch(request);
    //     return ApiResponse.ok("랜덤 매칭 성공", response);
    // }

    

    

}
