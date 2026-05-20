package com.playball.backend.domain.notification.dto;

import com.playball.backend.domain.notification.enums.DevicePlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "FCM 디바이스 토큰 등록 요청")
@Getter
@Setter
@NoArgsConstructor
public class DeviceTokenRequest {

    @Schema(description = "FCM 디바이스 토큰", example = "fCmToKeN_EXAMPLE_1234567890abcdef")
    @NotBlank(message = "FCM 토큰은 필수입니다")
    private String token;

    @Schema(description = "디바이스 플랫폼 (ANDROID | IOS | WEB)", example = "ANDROID")
    @NotNull(message = "플랫폼은 필수입니다")
    private DevicePlatform platform;
}
