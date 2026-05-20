package com.playball.backend.domain.compliment.controller;

import com.playball.backend.common.annotation.CurrentMemberId;
import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.compliment.dto.ComplimentBulkRequest;
import com.playball.backend.domain.compliment.dto.ComplimentDTO;
import com.playball.backend.domain.compliment.dto.ComplimentListResponse;
import com.playball.backend.domain.compliment.dto.ComplimentSummaryDTO;
import com.playball.backend.domain.compliment.service.ComplimentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
            summary = "경기 후 칭찬 일괄 등록",
            description = "경기 종료 후 함께한 참가자들에게 칭찬 태그를 일괄 등록합니다.\n\n"
                    + "- 태그 종류: `MANNERS`(매너), `SKILL`(실력), `PUNCTUAL`(시간약속), `PASSIONATE`(열정), `MOOD_MAKER`(분위기)\n"
                    + "- 한 경기에서 동일 대상에게 중복 칭찬은 불가합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/matches/{matchId}/compliments")
    public ApiResponse<Map<String, Integer>> submitCompliments(
            @CurrentMemberId Long raterId,
            @Parameter(description = "경기 ID", example = "1") @PathVariable Long matchId,
            @Valid @RequestBody ComplimentBulkRequest request) {
        int created = complimentService.submitBulkCompliments(raterId, matchId, request);
        return ApiResponse.ok(created + "명에게 칭찬을 보냈습니다", Map.of("created", created));
    }

    @Operation(
            summary = "특정 회원이 받은 칭찬 목록",
            description = "회원이 받은 칭찬 목록을 커서 기반 페이지네이션으로 조회합니다.\n\n"
                    + "다음 페이지가 있으면 `nextCursor` 값이 반환됩니다. 없으면 `null`입니다.")
    @GetMapping("/api/members/{memberId}/compliments")
    public ApiResponse<ComplimentListResponse> getReceivedCompliments(
            @Parameter(description = "조회할 회원 ID", example = "1") @PathVariable Long memberId,
            @Parameter(description = "커서 ID (이전 응답의 nextCursor 값, 첫 요청 시 생략)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "페이지 크기 (기본 10)", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("칭찬 목록 조회 성공", complimentService.getReceivedCompliments(memberId, cursor, size));
    }

    @Operation(
            summary = "회원 칭찬 통계 (프로필용)",
            description = "회원이 받은 칭찬의 총 개수와 태그별 개수를 반환합니다. 프로필 화면에서 사용합니다.")
    @GetMapping("/api/members/{memberId}/compliments/summary")
    public ApiResponse<ComplimentSummaryDTO> getMemberSummary(
            @Parameter(description = "조회할 회원 ID", example = "1") @PathVariable Long memberId) {
        return ApiResponse.ok("칭찬 통계 조회 성공", complimentService.getMemberSummary(memberId));
    }

    @Operation(
            summary = "특정 경기에서 내가 한/받은 칭찬",
            description = "특정 경기에서 내가 보낸 칭찬(`sent`)과 받은 칭찬(`received`) 목록을 반환합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/matches/{matchId}/compliments/me")
    public ApiResponse<Map<String, List<ComplimentDTO>>> getMyMatchCompliments(
            @CurrentMemberId Long memberId,
            @Parameter(description = "경기 ID", example = "1") @PathVariable Long matchId) {
        return ApiResponse.ok("경기 칭찬 조회 성공", complimentService.getMyMatchCompliments(memberId, matchId));
    }
}
