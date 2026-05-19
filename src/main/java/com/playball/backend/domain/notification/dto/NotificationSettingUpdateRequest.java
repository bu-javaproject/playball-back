package com.playball.backend.domain.notification.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationSettingUpdateRequest {

    @NotNull(message = "알림 설정 값은 필수입니다")
    private Boolean enabled;

}
