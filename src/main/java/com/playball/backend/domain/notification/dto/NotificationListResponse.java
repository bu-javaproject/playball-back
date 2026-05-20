package com.playball.backend.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "알림 목록 응답 (커서 기반 페이지네이션)")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListResponse {

    @Schema(description = "알림 목록")
    private List<NotificationDTO> items;

    @Schema(description = "다음 페이지 커서 ID (다음 페이지 없으면 null)", example = "15")
    private Long nextCursor;
}
