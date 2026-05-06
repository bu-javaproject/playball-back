package com.playball.backend.notification.controller;

import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.notification.dto.NotificationListResponse;
import com.playball.backend.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림", description = "알림 목록 / 읽음 처리 / 알림 설정 / FCM 토큰 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("")
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
}
