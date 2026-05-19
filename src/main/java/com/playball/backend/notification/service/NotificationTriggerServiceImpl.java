package com.playball.backend.notification.service;

import com.playball.backend.notification.entity.DeviceToken;
import com.playball.backend.notification.entity.Notification;
import com.playball.backend.notification.enums.NoticeType;
import com.playball.backend.notification.enums.NotificationTargetType;
import com.playball.backend.notification.repository.DeviceTokenRepository;
import com.playball.backend.notification.repository.NotificationRepository;
import com.playball.backend.notification.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTriggerServiceImpl implements NotificationTriggerService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository settingRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final FcmPushService fcmPushService;

    @Override
    @Transactional
    public void sendMatchFound(Long memberId, Long matchId, String matchTitle) {
        send(memberId, NoticeType.MATCH_FOUND, "매칭 성사!",
                matchTitle + "매칭이 성사되었습니다.", NotificationTargetType.MATCH, matchId);
    }

    @Override
    @Transactional
    public void sendApplicationRejected(Long memberId, Long matchId, String matchTitle) {
        send(memberId, NoticeType.APPLICATION_REJECTED, "신청 거절",
                matchTitle + "신청이 거절되었습니다.", NotificationTargetType.MATCH, matchId);
    }

    @Override
    @Transactional
    public void sendMatchReminder(Long memberId, Long matchId, String matchTitle) {
        send(memberId, NoticeType.MATCH_REMINDER, "경기 임박",
                matchTitle + "경기가 곧 시작됩니다.", NotificationTargetType.MATCH, matchId);
    }

    @Override
    @Transactional
    public void sendMatchCancelled(Long memberId, Long matchId, String matchTitle) {
        send(memberId, NoticeType.MATCH_CANCELLED, "경기 취소",
                matchTitle + " 경기가 취소되었습니다.", NotificationTargetType.MATCH, matchId);
    }

    @Override
    @Transactional
    public void sendRatingRequest(Long memberId, Long matchId, String matchTitle) {
        send(memberId, NoticeType.RATING_REQUEST, "칭찬 요청",
                matchTitle + " 같이 뛴 분들에게 칭찬을 남겨주세요.", NotificationTargetType.MATCH, matchId);
    }

    @Override
    @Transactional
    public void sendSystemNotice(Long memberId, String title, String content) {
        send(memberId, NoticeType.SYSTEM_NOTICE, title, content, null, null);
    }

    private void send(Long memberId, NoticeType type, String title, String content,
                      NotificationTargetType targetType, Long targetId) {

        notificationRepository.save(Notification.builder()
                .memberId(memberId)
                .noticeType(type)
                .title(title)
                .content(content)
                .targetType(targetType)
                .targetId(targetId)
                .isRead(false)
                .build());

        boolean enabled = settingRepository.findById(memberId)
                .map(s -> Boolean.TRUE.equals(s.getEnabled()))
                .orElse(true);

        if (!enabled) {
            log.debug("알림 비활성화 사용자: memberId={}, 푸시 skip", memberId);
            return;
        }

        List<DeviceToken> tokens = deviceTokenRepository.findByMemberId(memberId);
        for (DeviceToken dt : tokens) {
            fcmPushService.sendPush(dt.getToken(), title, content);
        }
    }
}