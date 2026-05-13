package com.playball.backend.compliment.controller;

import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.compliment.dto.ComplimentBulkRequest;
import com.playball.backend.compliment.dto.ComplimentDTO;
import com.playball.backend.compliment.dto.ComplimentListResponse;
import com.playball.backend.compliment.dto.ComplimentSummaryDTO;
import com.playball.backend.compliment.service.ComplimentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Tag(name = "칭찬", description = "경기 후 칭찬 등록 / 받은 칭찬 조회 / 누적 카운트")
@RestController
@RequiredArgsConstructor
public class ComplimentController {

    private final ComplimentService complimentService;


    @Operation(summary = "경기 후 칭찬 Bulk 등록")
    @PostMapping("/api/matches/{matchId}/compliments")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> submitCompliments(
            Authentication authentication,
            @PathVariable Long matchId,
            @Valid @RequestBody ComplimentBulkRequest request) {

        Long raterId = (Long) authentication.getPrincipal();
        int created = complimentService.submitBulkCompliments(raterId, matchId, request);

        return ResponseEntity.ok(
                ApiResponse.ok(created + "명에게 칭찬을 보냈습니다",
                        Map.of("created", created))
        );
    }


    @Operation(summary = "특정 회원이 받은 칭찬 목록")
    @GetMapping("/api/members/{memberId}/compliments")
    public ResponseEntity<ApiResponse<ComplimentListResponse>> getReceivedCompliments(
            @PathVariable Long memberId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {

        ComplimentListResponse response = complimentService
                .getReceivedCompliments(memberId, cursor, size);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }


    @Operation(summary = "회원의 칭찬 누적 카운트 (프로필용)")
    @GetMapping("/api/members/{memberId}/compliments/summary")
    public ResponseEntity<ApiResponse<ComplimentSummaryDTO>> getMemberSummary(
            @PathVariable Long memberId) {

        ComplimentSummaryDTO summary = complimentService.getMemberSummary(memberId);
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }


    @Operation(summary = "특정 매치에서 내가 한/받은 칭찬")
    @GetMapping("/api/matches/{matchId}/compliments/me")
    public ResponseEntity<ApiResponse<Map<String, List<ComplimentDTO>>>>
    getMyMatchCompliments(
            Authentication authentication,
            @PathVariable Long matchId) {

        Long memberId = (Long) authentication.getPrincipal();
        Map<String, List<ComplimentDTO>> result = complimentService
                .getMyMatchCompliments(memberId, matchId);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
