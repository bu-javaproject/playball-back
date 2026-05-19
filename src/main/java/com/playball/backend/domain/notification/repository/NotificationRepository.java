package com.playball.backend.domain.notification.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.playball.backend.domain.notification.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.memberId = :memberId " +
           "AND (:cursor IS NULL OR n.notificationId < :cursor) " +
           "AND (:onlyUnread = false OR n.isRead = false) " +
           "ORDER BY n.notificationId DESC")
    List<Notification> findByMemberWithCursor(
            @Param("memberId") Long memberId,
            @Param("cursor") Long cursor,
            @Param("onlyUnread") boolean onlyUnread,
            Pageable pageable);

    int countByMemberIdAndIsReadFalse(Long memberId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notificationId = :id AND n.isRead = false")
    int markAsRead(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.memberId = :memberId AND n.isRead = false")
    int markAllAsReadByMember(@Param("memberId") Long memberId);
}
