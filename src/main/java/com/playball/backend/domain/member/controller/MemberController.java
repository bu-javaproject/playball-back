package com.playball.backend.domain.member.controller;

import java.util.Map;

import com.playball.backend.common.annotation.CurrentMemberId;
import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.member.dto.LocationUpdateRequest;
import com.playball.backend.domain.member.dto.MemberDTO;
import com.playball.backend.domain.member.dto.ProfileResponse;
import com.playball.backend.domain.member.dto.SignUpCompleteRequest;
import com.playball.backend.domain.member.dto.UpdateProfileRequest;
import com.playball.backend.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "회원가입 추가정보 입력")
    @PostMapping("/signup/complete")
    public ApiResponse<MemberDTO> completeSignup(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody SignUpCompleteRequest request) {
        return ApiResponse.ok("회원가입이 완료되었습니다", memberService.completeSignup(memberId, request));
    }

    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("/check-nickname")
    public ApiResponse<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean available = !memberService.isNicknameDuplicate(nickname);
        String message = available ? "사용 가능한 닉네임입니다" : "이미 사용 중인 닉네임입니다";
        return ApiResponse.ok(message, Map.of("available", available));
    }

    @Operation(summary = "내 프로필 조회")
    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyProfile(@CurrentMemberId Long memberId) {
        return ApiResponse.ok("프로필 조회 성공", memberService.getMyProfile(memberId));
    }

    @Operation(summary = "다른 회원 프로필 조회")
    @GetMapping("/{memberId}")
    public ApiResponse<ProfileResponse> getMember(@PathVariable Long memberId) {
        return ApiResponse.ok("회원 조회 성공", memberService.getMemberById(memberId));
    }

    @Operation(summary = "프로필 수정 (닉네임·활동지역·선호운동)")
    @PatchMapping("/me")
    public ApiResponse<ProfileResponse> updateProfile(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("프로필이 수정되었습니다", memberService.updateProfile(memberId, request));
    }

    @Operation(summary = "위치 정보 업데이트")
    @PatchMapping("/me/location")
    public ApiResponse<Void> updateLocation(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody LocationUpdateRequest request) {
        memberService.updateLocation(memberId, request.getLatitude(), request.getLongitude(), request.getAddress());
        return ApiResponse.ok("위치가 업데이트되었습니다", null);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw(@CurrentMemberId Long memberId) {
        memberService.deleteMember(memberId);
        return ApiResponse.ok("회원 탈퇴가 완료되었습니다", null);
    }
}
