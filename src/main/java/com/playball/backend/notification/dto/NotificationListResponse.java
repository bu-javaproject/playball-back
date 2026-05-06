package com.playball.backend.notification.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListResponse {

    private List<NotificationDTO> items;
    private Long nextCursor; //다음 페이지 없으면 null
}
