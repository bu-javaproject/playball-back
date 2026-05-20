package com.playball.backend.domain.notification.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.playball.backend.domain.notification.entity.DeviceToken;
import com.playball.backend.domain.notification.entity.Notification;
import com.playball.backend.domain.notification.enums.NoticeType;
import com.playball.backend.domain.notification.enums.NotificationTargetType;
import com.playball.backend.domain.notification.repository.DeviceTokenRepository;
import com.playball.backend.domain.notification.repository.NotificationRepository;
import com.playball.backend.domain.notification.repository.NotificationSettingRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTriggerServiceImpl implements NotificationTriggerService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final FcmPushService fcmPushService;

    @Override
    @Transactional
    public void sendMatchFound(Long memberId, Long matchId, String matchTitle) {
        send(memberId, NoticeType.MATCH_FOUND, "매칭 성사!",
                matchTitle + " 매칭이 성사되었습니다.", NotificationTargetType.MATCH, matchId);
    }

    @Override
    @Transactional
    public void sendApplicationRejected(Long memberId, Long matchId, String matchTitle) {
        send(memberId, NoticeType.APPLICATION_REJECTED, "신청 거절",
                matchTitle + " 신청이 거절되었습니다.", NotificationTargetType.MATCH, matchId);
    }

    @Override
    @Transactional
    public void sendMatchReminder(Long memberId, Long matchId, String matchTitle) {
        send(memberId, NoticeType.MATCH_REMINDER, "경기 임박",
                matchTitle + " 경기가 곧 시작됩니다.", NotificationTargetType.MATCH, matchId);
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
                .build());

        boolean enabled = notificationSettingRepository.findById(memberId)
                .map(s -> s.getEnabled())
                .orElse(true);

        if (!enabled) {
            log.debug("알림 비활성화 사용자: memberId={}, 푸시 skip", memberId);
            return;
        }

        List<DeviceToken> tokens = deviceTokenRepository.findByMemberId(memberId);
        for (DeviceToken deviceToken : tokens) {
            fcmPushService.sendPush(deviceToken.getToken(), title, content);
        }
    }
}
