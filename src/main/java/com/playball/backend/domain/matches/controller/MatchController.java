package com.playball.backend.domain.matches.controller;

import java.util.List;

import com.playball.backend.common.annotation.CurrentMemberId;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "경기", description = "경기 생성 / 조회 / 수정 / 삭제 / 참가 API")
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final MatchingService matchingService;
    private final MatchRealtimeService matchRealtimeService;

    @Operation(summary = "경기 상세 조회", description = "경기 ID로 상세 정보와 참가 회원 목록을 조회합니다.")
    @GetMapping("/{matchId}")
    public ApiResponse<MatchDetailResponse> getMatch(
            @Parameter(description = "경기 ID", example = "1")
            @PathVariable Long matchId) {
        return ApiResponse.ok("경기 조회 성공", matchService.getMatch(matchId));
    }

    @Operation(
            summary = "경기 목록 조회",
            description = "경기 목록을 조회합니다.\n\n"
                    + "- `latitude` + `longitude` 제공 시: 반경(`radius` km, 기본 5km) 내 OPEN 경기를 거리 가까운 순으로 반환\n"
                    + "- 위치 미제공 시: 전체 목록을 최신순 페이지네이션으로 반환\n"
                    + "- `sportType`으로 종목 필터링 가능 (예: SOCCER, BASKETBALL, RUNNING, BADMINTON)")
    @GetMapping
    public ApiResponse<List<MatchResponse>> getMatches(
            @Parameter(description = "위도 (위치 기반 검색 시 필수)", example = "37.5665") @RequestParam(required = false) Double latitude,
            @Parameter(description = "경도 (위치 기반 검색 시 필수)", example = "126.9780") @RequestParam(required = false) Double longitude,
            @Parameter(description = "검색 반경 (km, 기본 5.0)", example = "5.0") @RequestParam(defaultValue = "5.0") Double radius,
            @Parameter(description = "종목 필터 (SOCCER | BASKETBALL | RUNNING | BADMINTON)", example = "SOCCER") @RequestParam(required = false) String sportType,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("경기 목록 조회 성공",
                matchService.getMatches(latitude, longitude, radius, sportType, page, size));
    }

    @Operation(summary = "경기 생성", description = "새로운 경기를 생성합니다. 생성자는 자동으로 참가자로 등록됩니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ApiResponse<MatchCreateResponse> createMatch(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody MatchCreateRequest request) {
        return ApiResponse.ok("경기 생성 성공", matchService.createMatch(request, memberId));
    }

    @Operation(
            summary = "경기 수정",
            description = "경기 정보를 수정합니다. 수정 권한은 경기 생성자(호스트)에게만 있습니다.\n\n"
                    + "변경하지 않을 필드는 null로 보내거나 생략하세요.")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{matchId}")
    public ApiResponse<MatchUpdateResponse> updateMatch(
            @CurrentMemberId Long memberId,
            @Parameter(description = "수정할 경기 ID", example = "1") @PathVariable Long matchId,
            @RequestBody MatchUpdateRequest request) {
        return ApiResponse.ok("경기 수정 성공", matchService.updateMatch(matchId, request, memberId));
    }

    @Operation(
            summary = "경기 삭제",
            description = "경기를 삭제(DELETED 상태로 변경)합니다. 삭제 권한은 경기 생성자(호스트)에게만 있습니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{matchId}")
    public ApiResponse<Void> deleteMatch(
            @CurrentMemberId Long memberId,
            @Parameter(description = "삭제할 경기 ID", example = "1") @PathVariable Long matchId) {
        matchService.deleteMatch(matchId, memberId);
        return ApiResponse.ok("경기가 삭제되었습니다", null);
    }

    @Operation(
            summary = "경기 참가",
            description = "경기에 참가 신청합니다. 인원이 가득 찬 경우 참가할 수 없습니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{matchId}/join")
    public ApiResponse<MatchedResponse> joinMatch(
            @CurrentMemberId Long memberId,
            @Parameter(description = "참가할 경기 ID", example = "1") @PathVariable Long matchId) {
        return ApiResponse.ok("경기 참가 성공", matchingService.joinMatch(matchId, memberId));
    }

    @Operation(summary = "경기 참가 취소", description = "경기 참가를 취소합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{matchId}/join")
    public ApiResponse<Void> leaveMatch(
            @CurrentMemberId Long memberId,
            @Parameter(description = "취소할 경기 ID", example = "1") @PathVariable Long matchId) {
        matchRealtimeService.leaveMatch(matchId, memberId);
        return ApiResponse.ok("경기 참가 취소 성공", null);
    }

    @Operation(
            summary = "랜덤 매칭",
            description = "현재 위치와 조건(종목, 날짜, 성별, 실력 등)을 기반으로 참가 가능한 경기를 랜덤으로 추천합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/random")
    public ApiResponse<RandomMatchResponse> findRandomMatch(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody RandomMatchRequest request) {
        return ApiResponse.ok("랜덤 매칭 성공", matchService.findRandomMatch(request, memberId));
    }
}
