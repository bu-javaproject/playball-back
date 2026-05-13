package com.playball.backend.domain.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_setting")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSetting {

    @Id
    private Long memberId;

    @Builder.Default
    private Boolean enabled = true;

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
