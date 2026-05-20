package com.playball.backend.domain.notification.controller;

import com.playball.backend.common.annotation.CurrentMemberId;
import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.notification.dto.DeviceTokenRequest;
import com.playball.backend.domain.notification.service.DeviceTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "알림", description = "FCM 토큰 등록/삭제")
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @Operation(summary = "FCM 토큰 등록")
    @PostMapping("/token")
    public ApiResponse<Void> createDeviceToken(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody DeviceTokenRequest request) {
        deviceTokenService.registerToken(memberId, request);
        return ApiResponse.ok("기기 토큰이 등록되었습니다", null);
    }

    @Operation(summary = "FCM 토큰 삭제")
    @DeleteMapping("/token")
    public ApiResponse<Void> deleteDeviceToken(
            @CurrentMemberId Long memberId,
            @RequestParam String token) {
        deviceTokenService.deleteToken(token, memberId);
        return ApiResponse.ok("기기 토큰이 삭제되었습니다", null);
    }
}
