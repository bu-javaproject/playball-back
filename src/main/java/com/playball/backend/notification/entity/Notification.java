package com.playball.backend.notification.entity;

import com.playball.backend.notification.enums.NoticeType;
import com.playball.backend.notification.enums.NotificationTargetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long memberId;

    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    private String title;
    private String content;

    @Builder.Default
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private NotificationTargetType targetType;

    private Long targetId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}