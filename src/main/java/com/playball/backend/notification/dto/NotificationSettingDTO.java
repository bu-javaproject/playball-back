package com.playball.backend.notification.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingDTO {

    //DB 매핑용.
    private Long memberId;

    private Boolean enabled;
    private LocalDateTime updatedAt;
}
