package com.playball.backend.notification.entity;

import com.playball.backend.notification.enums.NoticeType;
import com.playball.backend.notification.enums.NotificationTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeType noticeType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Builder.Default
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private NotificationTargetType targetType;

    private Long targetId;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) this.isRead = false;
    }

    public void markRead() {
        this.isRead = true;
    }
}
