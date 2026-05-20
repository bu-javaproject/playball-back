package com.playball.backend.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "알림 설정 수정 요청")
@Getter
@Setter
@NoArgsConstructor
public class NotificationSettingUpdateRequest {

    @Schema(description = "알림 수신 활성화 여부 (true: ON, false: OFF)", example = "true")
    @NotNull(message = "알림 설정 값은 필수입니다")
    private Boolean enabled;
}
