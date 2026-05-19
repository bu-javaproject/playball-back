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
import com.playball.backend.domain.matches.dto.MatchDetailResponse;
import com.playball.backend.domain.matches.dto.MatchResponse;
import com.playball.backend.domain.matches.dto.MatchUpdateRequest;
import com.playball.backend.domain.matches.dto.MatchUpdateResponse;
import com.playball.backend.domain.matches.service.MatchService;

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
    public ApiResponse<MatchCreateResponse> createMatch(@Valid @RequestBody MatchCreateRequest request) {
        return ApiResponse.ok("경기 생성 성공", matchService.createMatch(request));
    }

    @Operation(summary = "경기 수정")
    @PatchMapping("/{matchId}")
    public ApiResponse<MatchUpdateResponse> updateMatch(
            @PathVariable Long matchId,
            @RequestBody MatchUpdateRequest request) {
        return ApiResponse.ok("경기 수정 성공", matchService.updateMatch(matchId, request));
    }

    @Operation(summary = "경기 삭제")
    @DeleteMapping("/{matchId}")
    public ApiResponse<Void> deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return ApiResponse.ok("경기가 삭제되었습니다", null);
    }
}
