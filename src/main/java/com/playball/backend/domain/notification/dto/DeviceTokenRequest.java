package com.playball.backend.domain.notification.dto;

import com.playball.backend.domain.notification.enums.DevicePlatform;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeviceTokenRequest {

    @NotBlank(message = "FCM 토큰은 필수입니다")
    private String token;

    @NotNull(message = "플랫폼은 필수입니다")
    private DevicePlatform platform;
}
