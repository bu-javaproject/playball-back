package com.playball.backend.notification.mapper;

import com.playball.backend.notification.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NotificationMapper {

    /** 알림 1건 INSERT (Service에서 트리거 시 호출) */
    void insertNotification(NotificationDTO notification);

    /** 알림 1건 조회 (권한 검증/존재 확인용) */
    Optional<NotificationDTO> findById(@Param("notificationId") Long notificationId);

    /**
     * 회원별 알림 목록 조회 (Cursor 페이지네이션)
     *
     * @param memberId    조회 대상 회원
     * @param cursor      이전 응답의 nextCursor. 첫 호출은 null
     * @param size        페이지 크기
     * @param onlyUnread  true면 안 읽은 알림만
     */
    List<NotificationDTO> findByMember(@Param("memberId") Long memberId,
                                       @Param("cursor") Long cursor,
                                       @Param("size") int size,
                                       @Param("onlyUnread") boolean onlyUnread);

    /** 회원의 안 읽은 알림 개수 (빨간 점 표시용) */
    int countUnreadByMember(@Param("memberId") Long memberId);

    /** 알림 1건 읽음 처리, 영향받은 row 수 반환 */
    int markAsRead(@Param("notificationId") Long notificationId);

    /** 회원의 안 읽은 모든 알림을 읽음 처리, 영향받은 row 수 반환 */
    int markAllAsReadByMember(@Param("memberId") Long memberId);

    /** 알림 1건 삭제, 영향받은 row 수 반환 */
    void deleteById(@Param("notificationId") Long notificationId);

}
