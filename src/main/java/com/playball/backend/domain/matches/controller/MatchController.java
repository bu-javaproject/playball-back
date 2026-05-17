package com.playball.backend.domain.matches.controller;

import java.util.List;

import com.playball.backend.common.annotation.CurrentMemberId;
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
import com.playball.backend.domain.matches.dto.MatchDetailResponse;
import com.playball.backend.domain.matches.dto.MatchResponse;
import com.playball.backend.domain.matches.dto.MatchUpdateRequest;
import com.playball.backend.domain.matches.dto.MatchUpdateResponse;
import com.playball.backend.domain.matches.dto.RandomMatchRequest;
import com.playball.backend.domain.matches.dto.RandomMatchResponse;
import com.playball.backend.domain.matches.service.MatchService;
import com.playball.backend.domain.matching.dto.MatchedResponse;
import com.playball.backend.domain.matching.service.MatchRealtimeService;
import com.playball.backend.domain.matching.service.MatchingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "경기", description = "경기 생성 / 조회 / 수정 / 삭제 API")
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final MatchingService matchingService;
    private final MatchRealtimeService matchRealtimeService;

    @Operation(summary = "경기 상세 조회")
    @GetMapping("/{matchId}")
    public ApiResponse<MatchDetailResponse> getMatch(@PathVariable Long matchId) {
        return ApiResponse.ok("경기 조회 성공", matchService.getMatch(matchId));
    }

    @Operation(summary = "경기 목록 조회")
    @GetMapping
    public ApiResponse<List<MatchResponse>> getMatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("경기 목록 조회 성공", matchService.getMatches(page, size));
    }

    @Operation(summary = "경기 생성")
    @PostMapping
    public ApiResponse<MatchCreateResponse> createMatch(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody MatchCreateRequest request) {
        return ApiResponse.ok("경기 생성 성공", matchService.createMatch(request, memberId));
    }

    @Operation(summary = "경기 수정")
    @PatchMapping("/{matchId}")
    public ApiResponse<MatchUpdateResponse> updateMatch(
            @CurrentMemberId Long memberId,
            @PathVariable Long matchId,
            @RequestBody MatchUpdateRequest request) {
        return ApiResponse.ok("경기 수정 성공", matchService.updateMatch(matchId, request, memberId));
    }

    @Operation(summary = "경기 삭제")
    @DeleteMapping("/{matchId}")
    public ApiResponse<Void> deleteMatch(
            @CurrentMemberId Long memberId,
            @PathVariable Long matchId) {
        matchService.deleteMatch(matchId, memberId);
        return ApiResponse.ok("경기가 삭제되었습니다", null);
    }

    @Operation(summary = "경기 참가")
    @PostMapping("/{matchId}/join")
    public ApiResponse<MatchedResponse> joinMatch(
            @CurrentMemberId Long memberId,
            @PathVariable Long matchId) {
        return ApiResponse.ok("경기 참가 성공", matchingService.joinMatch(matchId, memberId));
    }

    @Operation(summary = "경기 참가 취소")
    @DeleteMapping("/{matchId}/join")
    public ApiResponse<Void> leaveMatch(
            @CurrentMemberId Long memberId,
            @PathVariable Long matchId) {
        matchRealtimeService.leaveMatch(matchId, memberId);
        return ApiResponse.ok("경기 참가 취소 성공", null);
    }

    @Operation(summary = "랜덤 매칭", description = "위치 기반으로 조건에 맞는 경기를 랜덤으로 추천합니다.")
    @PostMapping("/random")
    public ApiResponse<RandomMatchResponse> findRandomMatch(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody RandomMatchRequest request) {
        return ApiResponse.ok("랜덤 매칭 성공", matchService.findRandomMatch(request, memberId));
    }
}
