package com.playball.backend.domain.notification.controller;

import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.notification.dto.NotificationListResponse;
import com.playball.backend.domain.notification.dto.NotificationSettingDTO;
import com.playball.backend.domain.notification.dto.NotificationSettingUpdateRequest;
import com.playball.backend.domain.notification.service.NotificationService;
import com.playball.backend.domain.notification.service.NotificationSettingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "알림", description = "알림 목록 / 읽음 처리 / 알림 설정 / FCM 토큰 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;

    @Operation(summary = "내 알림 목록 조회")
    @GetMapping
    public ApiResponse<NotificationListResponse> getMyNotifications(
            Authentication authentication,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean onlyUnread) {
        Long memberId = (Long) authentication.getPrincipal();
        return ApiResponse.ok(notificationService.getMyNotifications(memberId, cursor, size, onlyUnread));
    }

    @Operation(summary = "알림 읽음 처리")
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> readNotification(
            Authentication authentication,
            @PathVariable Long notificationId) {
        Long memberId = (Long) authentication.getPrincipal();
        notificationService.markAsRead(memberId, notificationId);
        return ApiResponse.ok("읽음 처리되었습니다", null);
    }

    @Operation(summary = "전체 알림 읽음 처리")
    @PatchMapping("/read-all")
    public ApiResponse<Map<String, Integer>> readAllNotifications(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        int affected = notificationService.markAllAsRead(memberId);
        return ApiResponse.ok(affected + "개의 알림이 읽음 처리되었습니다", Map.of("affected", affected));
    }

    @Operation(summary = "알림 삭제")
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(
            Authentication authentication,
            @PathVariable Long notificationId) {
        Long memberId = (Long) authentication.getPrincipal();
        notificationService.markAsDelete(memberId, notificationId);
        return ApiResponse.ok("삭제되었습니다", null);
    }

    @Operation(summary = "안 읽은 알림 개수 조회")
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Integer>> getUnreadCount(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ApiResponse.ok(Map.of("count", notificationService.getUnreadCount(memberId)));
    }

    @Operation(summary = "알림 설정 조회")
    @GetMapping("/settings")
    public ApiResponse<NotificationSettingDTO> getSettings(Authentication authentication) {
        Long memberId = (Long) authentication.getPrincipal();
        return ApiResponse.ok(notificationSettingService.getMySetting(memberId));
    }

    @Operation(summary = "알림 설정 수정")
    @PatchMapping("/settings")
    public ApiResponse<NotificationSettingDTO> updateSettings(
            Authentication authentication,
            @Valid @RequestBody NotificationSettingUpdateRequest request) {
        Long memberId = (Long) authentication.getPrincipal();
        return ApiResponse.ok("알림 설정이 변경되었습니다",
                notificationSettingService.updateMySetting(memberId, request.getEnabled()));
    }
}
