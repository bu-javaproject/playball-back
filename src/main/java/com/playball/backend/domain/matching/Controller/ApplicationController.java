package com.playball.backend.domain.matching.Controller;

import com.playball.backend.common.annotation.CurrentMemberId;
import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.matching.dto.ApplicationListItemResponse;
import com.playball.backend.domain.matching.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "매칭", description = "참가 신청,수락,거절")
@SecurityRequirement( name = "bearerAuth")
@RestController
@RequestMapping("/api/matches/{matchId}/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "경기 참가 신청")
    @PostMapping
    public ApiResponse<Map<String, Object>> apply(
            @CurrentMemberId Long memberId,
            @PathVariable Long matchId) {
        return ApiResponse.ok("참가 신청이 완료되었습니다", applicationService.apply(matchId, memberId));
    }

    @Operation(summary = "참가 신청 목록 조회 (주최자 전용)")
    @GetMapping
    public ApiResponse<List<ApplicationListItemResponse>> getApplications(
            @CurrentMemberId Long hostId,
            @PathVariable Long matchId) {
        return ApiResponse.ok("신청 목록 조회 성공", applicationService.getApplications(matchId, hostId));
    }

    @Operation(summary = "참가 신청 수락 (주최자 전용)")
    @PatchMapping("/{applicationId}/accept")
    public ApiResponse<Map<String, Object>> accept(
            @CurrentMemberId Long hostId,
            @PathVariable Long matchId,
            @PathVariable Long applicationId) {
        return ApiResponse.ok("신청을 수락했습니다", applicationService.accept(matchId, applicationId, hostId));
    }
    @Operation(summary = "참가 신청 거절 (주최자 전용)")
    @PatchMapping("/{applicationId}/reject")
    public ApiResponse<Map<String, Object>> reject(
            @CurrentMemberId Long hostId,
            @PathVariable Long matchId,
            @PathVariable Long applicationId) {
        return ApiResponse.ok("신청을 거절했습니다", applicationService.reject(matchId, applicationId, hostId));
    }

}
