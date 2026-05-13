package com.playball.backend.domain.member.controller;

import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.member.dto.MemberDTO;
import com.playball.backend.domain.member.dto.SignUpCompleteRequest;
import com.playball.backend.domain.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "회원", description = "회원가입 추가정보 / 프로필 조회·수정 / 탈퇴 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입 추가정보 입력")
    @PostMapping("/signup/complete")
    public ApiResponse<MemberDTO> completeSignup(
            Authentication authentication,
            @Valid @RequestBody SignUpCompleteRequest request) {
        Long memberId = (Long) authentication.getPrincipal();
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
    public ApiResponse<MemberDTO> getMyProfile(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ApiResponse.ok(memberService.getMyProfile(memberId));
    }

    @Operation(summary = "다른 회원 프로필 조회")
    @GetMapping("/{memberId}")
    public ApiResponse<MemberDTO> getMember(@PathVariable Long memberId) {
        return ApiResponse.ok(memberService.getMemberById(memberId));
    }

    @Operation(summary = "프로필 수정")
    @PutMapping("/me/edit")
    public ApiResponse<MemberDTO> updateProfile(
            Authentication authentication,
            @RequestBody MemberDTO updateRequest) {
        Long memberId = (Long) authentication.getPrincipal();
        return ApiResponse.ok("프로필이 수정되었습니다", memberService.updateProfile(memberId, updateRequest));
    }

    @Operation(summary = "위치 정보 업데이트")
    @PatchMapping("/me/location")
    public ApiResponse<Void> updateLocation(
            Authentication authentication,
            @RequestBody Map<String, Object> locationData) {
        Long memberId = (Long) authentication.getPrincipal();
        Double latitude = Double.valueOf(locationData.get("latitude").toString());
        Double longitude = Double.valueOf(locationData.get("longitude").toString());
        String address = (String) locationData.get("address");
        memberService.updateLocation(memberId, latitude, longitude, address);
        return ApiResponse.ok("위치가 업데이트되었습니다", null);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/withdraw")
    public ApiResponse<Void> withdraw(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        memberService.deleteMember(memberId);
        return ApiResponse.ok("회원 탈퇴가 완료되었습니다", null);
    }
}
