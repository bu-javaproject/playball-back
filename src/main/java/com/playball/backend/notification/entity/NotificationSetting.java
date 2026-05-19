package com.playball.backend.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_setting")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSetting {

    @Id
    private Long memberId;

    @Builder.Default
    private Boolean enabled = true;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}