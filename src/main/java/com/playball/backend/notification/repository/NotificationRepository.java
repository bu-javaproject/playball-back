package com.playball.backend.notification.repository;

import com.playball.backend.notification.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.memberId = :memberId " +
           "AND (:cursor IS NULL OR n.notificationId < :cursor) " +
           "AND (:onlyUnread = false OR n.isRead = false) " +
           "ORDER BY n.notificationId DESC")
    List<Notification> findByMember(@Param("memberId") Long memberId,
                                    @Param("cursor") Long cursor,
                                    @Param("onlyUnread") boolean onlyUnread,
                                    Pageable pageable);

    int countByMemberIdAndIsRead(Long memberId, Boolean isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notificationId = :id")
    int markAsRead(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.memberId = :memberId AND n.isRead = false")
    int markAllAsReadByMember(@Param("memberId") Long memberId);
}