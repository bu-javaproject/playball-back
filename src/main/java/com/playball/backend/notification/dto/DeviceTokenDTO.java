package com.playball.backend.notification.dto;

import com.playball.backend.notification.enums.DevicePlatform;
import lombok.*;

import java.time.LocalDateTime;

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
