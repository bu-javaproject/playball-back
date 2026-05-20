package com.playball.backend.domain.notification.controller;

import com.playball.backend.common.annotation.CurrentMemberId;
import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.notification.dto.NotificationListResponse;
import com.playball.backend.domain.notification.dto.NotificationSettingDTO;
import com.playball.backend.domain.notification.dto.NotificationSettingUpdateRequest;
import com.playball.backend.domain.notification.service.NotificationService;
import com.playball.backend.domain.notification.service.NotificationSettingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "알림", description = "알림 목록 / 읽음 처리 / 알림 설정 / FCM 토큰 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;

    @Operation(
            summary = "내 알림 목록 조회",
            description = "로그인한 회원의 알림 목록을 커서 기반 페이지네이션으로 조회합니다.\n\n"
                    + "- `onlyUnread: true` 로 미읽은 알림만 필터링 가능\n"
                    + "- 다음 페이지가 있으면 `nextCursor` 값이 반환됩니다.")
    @GetMapping
    public ApiResponse<NotificationListResponse> getMyNotifications(
            @CurrentMemberId Long memberId,
            @Parameter(description = "커서 ID (이전 응답의 nextCursor, 첫 요청 시 생략)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "페이지 크기 (기본 20)", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "미읽은 알림만 조회 여부 (기본 false)", example = "false") @RequestParam(defaultValue = "false") boolean onlyUnread) {
        return ApiResponse.ok("알림 목록 조회 성공", notificationService.getMyNotifications(memberId, cursor, size, onlyUnread));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> readNotification(
            @CurrentMemberId Long memberId,
            @Parameter(description = "알림 ID", example = "1") @PathVariable Long notificationId) {
        notificationService.markAsRead(memberId, notificationId);
        return ApiResponse.ok("읽음 처리되었습니다", null);
    }

    @Operation(summary = "전체 알림 읽음 처리", description = "모든 미읽은 알림을 읽음 상태로 변경합니다. `affected`에 처리된 개수가 반환됩니다.")
    @PatchMapping("/read-all")
    public ApiResponse<Map<String, Integer>> readAllNotifications(@CurrentMemberId Long memberId) {
        int affected = notificationService.markAllAsRead(memberId);
        return ApiResponse.ok(affected + "개의 알림이 읽음 처리되었습니다", Map.of("affected", affected));
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(
            @CurrentMemberId Long memberId,
            @Parameter(description = "알림 ID", example = "1") @PathVariable Long notificationId) {
        notificationService.markAsDelete(memberId, notificationId);
        return ApiResponse.ok("삭제되었습니다", null);
    }

    @Operation(summary = "미읽은 알림 개수 조회", description = "미읽은 알림의 총 개수를 반환합니다. 앱 뱃지 표시에 사용합니다.")
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Integer>> getUnreadCount(@CurrentMemberId Long memberId) {
        return ApiResponse.ok("안 읽은 알림 개수 조회 성공", Map.of("count", notificationService.getUnreadCount(memberId)));
    }

    @Operation(summary = "알림 설정 조회", description = "로그인한 회원의 알림 수신 설정을 조회합니다.")
    @GetMapping("/settings")
    public ApiResponse<NotificationSettingDTO> getSettings(@CurrentMemberId Long memberId) {
        return ApiResponse.ok("알림 설정 조회 성공", notificationSettingService.getMySetting(memberId));
    }

    @Operation(summary = "알림 설정 수정", description = "알림 수신 여부를 ON/OFF 합니다.")
    @PatchMapping("/settings")
    public ApiResponse<NotificationSettingDTO> updateSettings(
            @CurrentMemberId Long memberId,
            @Valid @RequestBody NotificationSettingUpdateRequest request) {
        return ApiResponse.ok("알림 설정이 변경되었습니다",
                notificationSettingService.updateMySetting(memberId, request.getEnabled()));
    }
}
