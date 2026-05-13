package com.playball.backend.domain.notification.dto;

import lombok.*;

import java.time.LocalDateTime;

import com.playball.backend.domain.notification.enums.DevicePlatform;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenDTO { //DB 테이블과 매핑되는 별도 DTO (mapper용)

    private Long deliveryTokenId;
    private Long memberId;
    private String token;
    private DevicePlatform platform;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
