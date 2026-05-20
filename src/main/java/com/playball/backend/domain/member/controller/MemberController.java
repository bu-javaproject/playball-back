package com.playball.backend.domain.member.controller;

import java.util.List;
import java.util.Map;

import com.playball.backend.common.annotation.CurrentMemberId;
import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.matches.dto.MyMatchResponse;
import com.playball.backend.domain.matches.service.MatchService;
import com.playball.backend.domain.member.dto.LocationUpdateRequest;
import com.playball.backend.domain.member.dto.MemberDTO;
import com.playball.backend.domain.member.dto.ProfileResponse;
import com.playball.backend.domain.member.dto.SignUpCompleteRequest;
import com.playball.backend.domain.member.dto.UpdateProfileRequest;
import com.playball.backend.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원", description = "회원가입 추가정보 / 프로필 조회·수정 / 탈퇴 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MatchService matchService;

    @Operation(
            summary = "회원가입 추가정보 입력",
            description = "카카오 로그인 후 신규 회원(`isNewUser: true`)이면 이 API로 닉네임, 성별, 나이 등 필수 정보를 등록합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/signup/complete")
    public ApiResponse<MemberDTO> completeSignup(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody SignUpCompleteRequest request) {
        return ApiResponse.ok("회원가입이 완료되었습니다", memberService.completeSignup(memberId, request));
    }

    @Operation(
            summary = "닉네임 중복 확인",
            description = "사용하려는 닉네임의 중복 여부를 확인합니다. `available: true`이면 사용 가능합니다.")
    @GetMapping("/check-nickname")
    public ApiResponse<Map<String, Boolean>> checkNickname(
            @Parameter(description = "확인할 닉네임 (2~10자)", example = "플레이볼러")
            @RequestParam String nickname) {
        boolean available = !memberService.isNicknameDuplicate(nickname);
        String message = available ? "사용 가능한 닉네임입니다" : "이미 사용 중인 닉네임입니다";
        return ApiResponse.ok(message, Map.of("available", available));
    }

    @Operation(summary = "내 프로필 조회", description = "로그인한 본인의 프로필을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyProfile(@CurrentMemberId Long memberId) {
        return ApiResponse.ok("프로필 조회 성공", memberService.getMyProfile(memberId));
    }

    @Operation(summary = "내가 참가한 경기 목록 조회", description = "로그인한 회원이 참가(또는 생성)한 경기 목록을 반환합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me/matches")
    public ApiResponse<List<MyMatchResponse>> getMyMatches(@CurrentMemberId Long memberId) {
        return ApiResponse.ok("내 경기 목록 조회 성공", matchService.getMyMatches(memberId));
    }

    @Operation(summary = "다른 회원 프로필 조회", description = "특정 회원의 공개 프로필을 조회합니다.")
    @GetMapping("/{memberId}")
    public ApiResponse<ProfileResponse> getMember(
            @Parameter(description = "조회할 회원 ID", example = "1")
            @PathVariable Long memberId) {
        return ApiResponse.ok("회원 조회 성공", memberService.getMemberById(memberId));
    }

    @Operation(
            summary = "프로필 수정",
            description = "닉네임, 활동지역, 선호운동을 수정합니다. 변경하지 않을 필드는 null로 보내거나 생략하세요.")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/me")
    public ApiResponse<ProfileResponse> updateProfile(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("프로필이 수정되었습니다", memberService.updateProfile(memberId, request));
    }

    @Operation(
            summary = "위치 정보 업데이트",
            description = "현재 위치(위도/경도)와 주소를 업데이트합니다. 주변 경기 검색에 사용됩니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/me/location")
    public ApiResponse<Void> updateLocation(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody LocationUpdateRequest request) {
        memberService.updateLocation(memberId, request.getLatitude(), request.getLongitude(), request.getAddress());
        return ApiResponse.ok("위치가 업데이트되었습니다", null);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 계정을 삭제합니다. 삭제 후 복구할 수 없습니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw(@CurrentMemberId Long memberId) {
        memberService.deleteMember(memberId);
        return ApiResponse.ok("회원 탈퇴가 완료되었습니다", null);
    }
}
