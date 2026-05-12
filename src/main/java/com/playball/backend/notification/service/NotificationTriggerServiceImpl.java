package com.playball.backend.notification.service;

import com.playball.backend.notification.dto.DeviceTokenDTO;
import com.playball.backend.notification.dto.NotificationDTO;
import com.playball.backend.notification.dto.NotificationSettingDTO;
import com.playball.backend.notification.enums.NoticeType;
import com.playball.backend.notification.enums.NotificationTargetType;
import com.playball.backend.notification.mapper.DeviceTokenMapper;
import com.playball.backend.notification.mapper.NotificationMapper;
import com.playball.backend.notification.mapper.NotificationSettingMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTriggerServiceImpl implements NotificationTriggerService {

    private final NotificationMapper notificationMapper;
    private final NotificationSettingMapper notificationSettingMapper;
    private final DeviceTokenMapper deviceTokenMapper;
    private final FcmPushService fcmPushService;

    @Override
    @Transactional
    public void sendMatchFound(Long memberId, Long matchId, String matchTitle) {
        send(memberId,
                NoticeType.MATCH_FOUND,
                "매칭 성사!",
                matchTitle + "매칭이 성사되었습니다.",
                NotificationTargetType.MATCH,
                matchId);
    }

    @Override
    @Transactional
    public void sendApplicationRejected(Long memberId, Long matchId, String matchTitle) {
        send(memberId,
                NoticeType.APPLICATION_REJECTED,
                "신청 거절",
                matchTitle + "신청이 거절되었습니다.",
                NotificationTargetType.MATCH,
                matchId);
    }

    @Override
    @Transactional
    public void sendMatchReminder(Long memberId, Long matchId, String matchTitle) {
        send(memberId,
                NoticeType.MATCH_REMINDER,
                "경기 임박",
                matchTitle + "경기가 곧 시작됩니다.",
                NotificationTargetType.MATCH,
                matchId);
    }

    @Override
    @Transactional
    public void sendMatchCancelled(Long memberId, Long matchId, String matchTitle) {
        send(memberId,
                NoticeType.MATCH_CANCELLED,
                "경기 취소",
                matchTitle + " 경기가 취소되었습니다.",
                NotificationTargetType.MATCH,
                matchId);
    }

    @Override
    @Transactional
    public void sendRatingRequest(Long memberId, Long matchId, String matchTitle) {
        send(memberId,
                NoticeType.RATING_REQUEST,
                "칭찬 요청",
                matchTitle + " 같이 뛴 분들에게 칭찬을 남겨주세요.",
                NotificationTargetType.MATCH,
                matchId);
    }

    @Override
    @Transactional
    public void sendSystemNotice(Long memberId, String title, String content) {
        send(memberId,
                NoticeType.SYSTEM_NOTICE,
                title,
                content,
                null,    // 시스템 공지는 이동할 target 없음
                null);
    }

    /**
     * 모든 알림 발송의 공통 로직.
     *   1. 사용자 알림 설정 확인 (꺼져있으면 푸시 X, 단 DB INSERT 는 함)
     *   2. notification 테이블 INSERT
     *   3. 사용자의 모든 FCM 토큰에 푸시 전송
     */
    private void send(Long memberId,
                      NoticeType type,
                      String title,
                      String content,
                      NotificationTargetType targetType,
                      Long targetId) {

        //1. DB INSERT (알림 목록에 표시되도록 - 끔 여부와 무관)
        NotificationDTO notification = NotificationDTO.builder()
                .memberId(memberId)
                .noticeType(type)
                .title(title)
                .content(content)
                .targetType(targetType)
                .targetId(targetId)
                .isRead(false)
                .build();
        notificationMapper.insertNotification(notification);

        //2. 사용자 알림 설정 확인 - 꺼져있으면 푸시 전송 안 함
        boolean enabled = notificationSettingMapper.findByMemberId(memberId)
                .map(NotificationSettingDTO::getEnabled)
                .orElse(true);  //row 없으면 기본 ON

        if (!enabled) {
            log.debug("알림 비활성화 사용자: memberId={}, 푸시 skip", memberId);
            return;
        }

        //3. 사용자의 모든 기기 토큰에 푸시
        List<DeviceTokenDTO> tokens = deviceTokenMapper.findByMemberId(memberId);
        for (DeviceTokenDTO deviceToken : tokens) {
            fcmPushService.sendPush(deviceToken.getToken(), title, content);
        }
    }

}
