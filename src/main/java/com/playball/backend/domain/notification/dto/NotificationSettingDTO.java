package com.playball.backend.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "알림 설정 응답")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingDTO {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "알림 수신 활성화 여부 (true: 수신, false: 수신 안 함)", example = "true")
    private Boolean enabled;

    @Schema(description = "설정 수정일시", example = "2024-05-01T10:00:00")
    private LocalDateTime updatedAt;
}
