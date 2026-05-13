package com.playball.backend.domain.security.controller;

import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.security.dto.KakaoLoginRequest;
import com.playball.backend.domain.security.dto.KakaoLoginResponse;
import com.playball.backend.domain.security.dto.TokenRefreshRequest;
import com.playball.backend.domain.security.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            description = "카카오 인가코드로 로그인합니다. 신규 회원이면 201 + isNewUser:true를 반환합니다.")
    @PostMapping("/kakao")
    public ResponseEntity<ApiResponse<KakaoLoginResponse>> kakaoLogin(
            @Valid @RequestBody KakaoLoginRequest request) {

        KakaoLoginResponse response = authService.kakaoLogin(
                request.getAuthorizationCode(), request.getRedirectUri());

        if (response.isNewUser()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("추가 정보 입력이 필요합니다", response));
        }
        return ResponseEntity.ok(ApiResponse.ok("로그인 성공", response));
    }

    @Operation(summary = "토큰 재발급",
            description = "Refresh Token으로 새로운 Access + Refresh 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<KakaoLoginResponse>> refresh(
            @Valid @RequestBody TokenRefreshRequest request) {

        KakaoLoginResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok("토큰 재발급 성공", response));
    }

    @Operation(summary = "로그아웃",
            description = "서버의 Refresh Token을 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        authService.logout(memberId);
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 성공", null));
    }
}
