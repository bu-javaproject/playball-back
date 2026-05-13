package com.playball.backend.domain.notification.dto;

import lombok.*;

import java.time.LocalDateTime;

import com.playball.backend.domain.notification.enums.NoticeType;
import com.playball.backend.domain.notification.enums.NotificationTargetType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    private Long notificationId;
    private Long memberId;
    private NoticeType noticeType;
    private String title;
    private String content;
    private Boolean isRead;
    private NotificationTargetType targetType;
    private Long targetId;
    private LocalDateTime createdAt;
}
