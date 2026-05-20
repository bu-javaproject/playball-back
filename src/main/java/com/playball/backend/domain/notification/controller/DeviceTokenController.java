package com.playball.backend.domain.notification.controller;

import com.playball.backend.common.annotation.CurrentMemberId;
import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.notification.dto.DeviceTokenRequest;
import com.playball.backend.domain.notification.service.DeviceTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "알림", description = "FCM 디바이스 토큰 등록/삭제")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @Operation(
            summary = "FCM 디바이스 토큰 등록",
            description = "앱 실행 시 FCM 토큰을 서버에 등록합니다. 푸시 알림 수신에 필요합니다.\n\n"
                    + "플랫폼: `ANDROID`, `IOS`, `WEB`")
    @PostMapping("/token")
    public ApiResponse<Void> createDeviceToken(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody DeviceTokenRequest request) {
        deviceTokenService.registerToken(memberId, request);
        return ApiResponse.ok("기기 토큰이 등록되었습니다", null);
    }

    @Operation(
            summary = "FCM 디바이스 토큰 삭제",
            description = "로그아웃 또는 앱 삭제 시 FCM 토큰을 서버에서 삭제합니다.")
    @DeleteMapping("/token")
    public ApiResponse<Void> deleteDeviceToken(
            @CurrentMemberId Long memberId,
            @Parameter(description = "삭제할 FCM 토큰") @RequestParam String token) {
        deviceTokenService.deleteToken(token, memberId);
        return ApiResponse.ok("기기 토큰이 삭제되었습니다", null);
    }
}
