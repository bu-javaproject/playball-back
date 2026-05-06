package com.playball.backend.notification.dto;

import com.playball.backend.notification.enums.NoticeType;
import com.playball.backend.notification.enums.NotificationTargetType;
import lombok.*;

import java.time.LocalDateTime;

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
