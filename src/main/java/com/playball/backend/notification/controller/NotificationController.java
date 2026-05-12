package com.playball.backend.notification.controller;

import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.notification.dto.NotificationListResponse;
import com.playball.backend.notification.dto.NotificationSettingDTO;
import com.playball.backend.notification.dto.NotificationSettingUpdateRequest;
import com.playball.backend.notification.service.NotificationService;
import com.playball.backend.notification.service.NotificationSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Scanner;

@Tag(name = "알림", description = "알림 목록 / 읽음 처리 / 알림 설정 / FCM 토큰 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;

    @Operation(summary = "내 알림 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<NotificationListResponse>> getMyNotifications(
            Authentication authentication,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean onlyUnread) {

        //1. 호출자 ID
        Long memberId = (Long) authentication.getPrincipal();
        //2. Service 호출
        NotificationListResponse response = notificationService.getMyNotifications(memberId, cursor, size, onlyUnread);
        //3. 응답
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "알림 읽음 처리")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationListResponse>> readNotification(
            Authentication authentication,
            @PathVariable Long notificationId) {

        // 1. 로그인된 사용자 ID (Authentication 에서)
        Long memberId = (Long) authentication.getPrincipal();

        // 2. Service 호출 (markAsRead 가 권한 체크 + 읽음 처리 다 함)
        notificationService.markAsRead(memberId, notificationId);

        // 3. 응답 (데이터 없음, 메시지만)
        return ResponseEntity.ok(ApiResponse.ok("읽음 처리되었습니다", null));
    }

    @Operation(summary = "전체 알림 읽음 처리")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> readAllNotifications(
            Authentication authentication) {

        //1. 로그인된 사용자 ID
        Long memberId = (Long) authentication.getPrincipal();
        //2. Service 호출 - 읽음 처리한 알림 개수 반환받음
        int affected = notificationService.markAllAsRead(memberId);
        //3. 응답 - 사용자에게 몇 개 처리됐는지 알림
        return ResponseEntity.ok(ApiResponse.ok(affected + "개의 알림이 읽음 처리되었습니다",
                                                Map.of("affected", affected))
        );
    }

    @Operation(summary = "알림 삭제")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationListResponse>> deleteNotification(
            Authentication authentication,
            @PathVariable Long notificationId) {

        // 1. 로그인된 사용자 ID
        Long memberId = (Long) authentication.getPrincipal();

        // 2. Service 호출 - 권한 체크 + 삭제는 Service 가 책임짐
        notificationService.markAsDelete(memberId, notificationId);

        // 3. 응답 - 데이터 없으니 Void, 메시지만
        return ResponseEntity.ok(ApiResponse.ok("삭제되었습니다", null));
    }

    @Operation(summary = "안 읽은 알림 개수 조회")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUnreadCount(
            Authentication authentication) {

        //1. 로그인된 사용자 ID 꺼내기
        Long memberId = (Long) authentication.getPrincipal();

        //2. Service 호출 - 안 읽은 알림 개수 받기
        int count = notificationService.getUnreadCount(memberId);

        //3. 응답 만들기 - {count:3} 형태로
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", count)));
    }

    @Operation(summary = "알림 설정 조회")
    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<NotificationSettingDTO>> getSettings(
            Authentication authentication) {
        //1. 로그인된 사용자 ID 꺼내기
        Long memberId = (Long) authentication.getPrincipal();

        //2. Service 호출
        NotificationSettingDTO setting = notificationSettingService.getMySetting(memberId);

        //3. 응답에 setting 데이터 담아서 반환
        return ResponseEntity.ok(ApiResponse.ok(setting));

    }

    @Operation(summary = "알림 설정 수정")
    @PatchMapping("/settings")
    public ResponseEntity<ApiResponse<NotificationSettingDTO>> updateSettings(
            Authentication authentication,
            @Valid @RequestBody NotificationSettingUpdateRequest request) {
        //1. 로그인된 사용자 ID 꺼내기
        Long memberId = (Long) authentication.getPrincipal();
        //2. Service 호출
        NotificationSettingDTO updated = notificationSettingService.updateMySetting(memberId, request.getEnabled());
        //3. 응답에 메세지 + DTO 데이터 전달
        return ResponseEntity.ok(ApiResponse.ok("알림 설정이 변경되었습니다" , updated));
    }
}
