package com.playball.backend.domain.notification.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.domain.notification.dto.NotificationDTO;
import com.playball.backend.domain.notification.dto.NotificationListResponse;
import com.playball.backend.domain.notification.entity.Notification;
import com.playball.backend.domain.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public NotificationListResponse getMyNotifications(Long memberId, Long cursor, int size, boolean onlyUnread) {
        List<Notification> items = notificationRepository.findByMemberWithCursor(
                memberId, cursor, onlyUnread, PageRequest.ofSize(size));

        Long nextCursor = items.size() < size
                ? null
                : items.get(items.size() - 1).getNotificationId();

        return NotificationListResponse.builder()
                .items(items.stream().map(this::toDto).toList())
                .nextCursor(nextCursor)
                .build();
    }

    @Transactional
    public void markAsRead(Long memberId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!notification.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        notificationRepository.markAsRead(notificationId);
    }

    @Transactional
    public int markAllAsRead(Long memberId) {
        return notificationRepository.markAllAsReadByMember(memberId);
    }

    @Transactional
    public void markAsDelete(Long memberId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!notification.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        notificationRepository.deleteById(notificationId);
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(Long memberId) {
        return notificationRepository.countByMemberIdAndIsReadFalse(memberId);
    }

    private NotificationDTO toDto(Notification n) {
        return NotificationDTO.builder()
                .notificationId(n.getNotificationId())
                .memberId(n.getMemberId())
                .noticeType(n.getNoticeType())
                .title(n.getTitle())
                .content(n.getContent())
                .isRead(n.getIsRead())
                .targetType(n.getTargetType())
                .targetId(n.getTargetId())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
