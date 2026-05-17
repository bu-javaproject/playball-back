package com.playball.backend.domain.auth.controller;

import com.playball.backend.common.annotation.CurrentMemberId;
import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.auth.dto.KakaoLoginRequest;
import com.playball.backend.domain.auth.dto.KakaoLoginResponse;
import com.playball.backend.domain.auth.dto.TokenRefreshRequest;
import com.playball.backend.domain.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "카카오 로그인 / 토큰 재발급 / 로그아웃 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "카카오 로그인",
            description = "카카오 인가코드로 로그인합니다. 신규 회원이면 isNewUser:true를 반환합니다.")
    @PostMapping("/kakao")
    public ApiResponse<KakaoLoginResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
        KakaoLoginResponse response = authService.kakaoLogin(request.getAuthorizationCode());
        String message = response.isNewUser() ? "추가 정보 입력이 필요합니다" : "로그인 성공";
        return ApiResponse.ok(message, response);
    }

    @Operation(summary = "토큰 재발급",
            description = "Refresh Token으로 새로운 Access + Refresh 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ApiResponse<KakaoLoginResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.ok("토큰 재발급 성공", authService.refreshAccessToken(request.getRefreshToken()));
    }

    @Operation(summary = "로그아웃", description = "서버의 Refresh Token을 삭제합니다.")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@CurrentMemberId Long memberId) {
        authService.logout(memberId);
        return ApiResponse.ok("로그아웃 성공", null);
    }
}
