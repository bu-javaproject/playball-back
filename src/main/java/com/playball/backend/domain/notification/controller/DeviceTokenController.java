package com.playball.backend.domain.notification.controller;

import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.notification.dto.DeviceTokenRequest;
import com.playball.backend.domain.notification.service.DeviceTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "알림", description = "FCM 토큰 등록/삭제")
@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @Operation(summary = "FCM 토큰 등록")
    @PostMapping("/token")
    public ResponseEntity<ApiResponse<Void>> createDeviceToken(
            Authentication authentication,
            @Valid @RequestBody DeviceTokenRequest request) {

        Long memberId = (Long) authentication.getPrincipal();

        deviceTokenService.registerToken(memberId, request);

        return ResponseEntity.ok(ApiResponse.ok("기기 토큰이 등록되었습니다", null));

    }

    @Operation(summary = "FCM 토큰 삭제")
    @DeleteMapping("/token")
    public ResponseEntity<ApiResponse<Void>> deleteDeviceToken(@RequestParam String token) {

        deviceTokenService.deleteToken(token);

        return ResponseEntity.ok(ApiResponse.ok("기기 토큰이 삭제되었습니다", null));
    }
}
