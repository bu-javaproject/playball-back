package com.playball.backend.domain.notification.dto;

import com.playball.backend.domain.notification.enums.NoticeType;
import com.playball.backend.domain.notification.enums.NotificationTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "알림 항목")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    @Schema(description = "알림 ID", example = "1")
    private Long notificationId;

    @Schema(description = "수신 회원 ID", example = "3")
    private Long memberId;

    @Schema(description = "알림 유형 (MATCH_FOUND | APPLICATION_REJECTED | MATCH_REMINDER | MATCH_CANCELLED | RATING_REQUEST | SYSTEM_NOTICE)", example = "MATCH_FOUND")
    private NoticeType noticeType;

    @Schema(description = "알림 제목", example = "매칭이 성사되었습니다!")
    private String title;

    @Schema(description = "알림 내용", example = "강남 풋살 경기에 참가 확정되었습니다.")
    private String content;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean isRead;

    @Schema(description = "이동할 대상 타입 (MATCH: 경기 상세, MEMBER: 프로필)", example = "MATCH")
    private NotificationTargetType targetType;

    @Schema(description = "이동할 대상 ID (경기 ID 또는 회원 ID)", example = "10")
    private Long targetId;

    @Schema(description = "알림 발생일시", example = "2024-06-01T17:00:00")
    private LocalDateTime createdAt;
}
