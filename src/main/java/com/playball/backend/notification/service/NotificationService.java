package com.playball.backend.notification.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.notification.dto.NotificationDTO;
import com.playball.backend.notification.dto.NotificationListResponse;
import com.playball.backend.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor //final 필드가 있으면 자동으로 이 필드만 받는 생성자 만들어줌.
public class NotificationService {

    private final NotificationMapper notificationMapper;

    //내 알림 목록 조회
    @Transactional(readOnly = true)
    public NotificationListResponse getMyNotifications(Long memberId,
                                                       Long cursor,
                                                       int size,
                                                       boolean onlyUnread) {

        //1. Mapper에서 내 알림 목록 가져오기
        List<NotificationDTO> items = notificationMapper.findByMember(memberId, cursor, size, onlyUnread);

        //2. nextCursor 계산
        //   items 개수가 size보다 적게 왔으면 -> DB에 더 이상 데이터 없음 -> null 반환 -> 프론트는 무한스크롤 종료
        //   size만큼 가득 찼으면 -> 마지막 item의 ID를 다음 cursor로 사용
        Long nextCursor = items.size() < size
                ? null
                : items.get(items.size() - 1).getNotificationId();

        //3. 응답 객체 빌드
        return NotificationListResponse.builder()
                .items(items)
                .nextCursor(nextCursor)
                .build();
    }

    //내 알림 읽음 (PATCH)
    @Transactional(readOnly = true)
    public void markAsRead(Long memberId, Long notificationId) {

        // 1. 알림이 본인 것인지 확인 (보안)
        NotificationDTO notification = notificationMapper.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!notification.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 2. 읽음 처리
        notificationMapper.markAsRead(notificationId);
    }

    //본인 모든 알림 읽음 처리
    @Transactional(readOnly = true)
    public int markAllAsRead(Long memberId) {
        return notificationMapper.markAllAsReadByMember(memberId);
    }

    //본인 알림 삭제 처리
    @Transactional(readOnly = true)
    public void markAsDelete(Long memberId, Long notificationId) {
        // 1. 알림이 본인 것인지 확인 (보안)
        NotificationDTO notification = notificationMapper.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!notification.getMemberId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        notificationMapper.deleteById(notificationId);          // 삭제 호출
        return;
    }

    //단순 카운트
    @Transactional(readOnly = true)
    public int getUnreadCount(Long memberId) {
        return notificationMapper.countUnreadByMember(memberId);
    }
}
