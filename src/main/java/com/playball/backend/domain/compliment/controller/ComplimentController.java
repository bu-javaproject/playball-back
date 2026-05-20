package com.playball.backend.domain.compliment.controller;

import com.playball.backend.common.annotation.CurrentMemberId;
import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.compliment.dto.ComplimentBulkRequest;
import com.playball.backend.domain.compliment.dto.ComplimentDTO;
import com.playball.backend.domain.compliment.dto.ComplimentListResponse;
import com.playball.backend.domain.compliment.dto.ComplimentSummaryDTO;
import com.playball.backend.domain.compliment.service.ComplimentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "칭찬", description = "경기 후 칭찬 등록 / 받은 칭찬 조회 / 누적 카운트")
@RestController
@RequiredArgsConstructor
public class ComplimentController {

    private final ComplimentService complimentService;

    @Operation(summary = "경기 후 칭찬 Bulk 등록")
    @PostMapping("/api/matches/{matchId}/compliments")
    public ApiResponse<Map<String, Integer>> submitCompliments(
            @CurrentMemberId Long raterId,
            @PathVariable Long matchId,
            @Valid @RequestBody ComplimentBulkRequest request) {
        int created = complimentService.submitBulkCompliments(raterId, matchId, request);
        return ApiResponse.ok(created + "명에게 칭찬을 보냈습니다", Map.of("created", created));
    }

    @Operation(summary = "특정 회원이 받은 칭찬 목록")
    @GetMapping("/api/members/{memberId}/compliments")
    public ApiResponse<ComplimentListResponse> getReceivedCompliments(
            @PathVariable Long memberId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("칭찬 목록 조회 성공", complimentService.getReceivedCompliments(memberId, cursor, size));
    }

    @Operation(summary = "회원의 칭찬 누적 카운트 (프로필용)")
    @GetMapping("/api/members/{memberId}/compliments/summary")
    public ApiResponse<ComplimentSummaryDTO> getMemberSummary(@PathVariable Long memberId) {
        return ApiResponse.ok("칭찬 통계 조회 성공", complimentService.getMemberSummary(memberId));
    }

    @Operation(summary = "특정 매치에서 내가 한/받은 칭찬")
    @GetMapping("/api/matches/{matchId}/compliments/me")
    public ApiResponse<Map<String, List<ComplimentDTO>>> getMyMatchCompliments(
            @CurrentMemberId Long memberId,
            @PathVariable Long matchId) {
        return ApiResponse.ok("경기 칭찬 조회 성공", complimentService.getMyMatchCompliments(memberId, matchId));
    }
}
