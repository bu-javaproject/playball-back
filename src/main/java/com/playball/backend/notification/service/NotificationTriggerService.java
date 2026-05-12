package com.playball.backend.notification.service;

public interface NotificationTriggerService {

    /** 매칭 성사 시 호출 */
    void sendMatchFound(Long memberId, Long matchId, String matchTitle);

    /** 신청 거절 시 호출 */
    void sendApplicationRejected(Long memberId, Long matchId, String matchTitle);

    /** 경기 시작 임박 알림 */
    void sendMatchReminder(Long memberId, Long matchId, String matchTitle);

    /** 경기 취소 알림 */
    void sendMatchCancelled(Long memberId, Long matchId, String matchTitle);

    /** 별점 요청 (매치 종료 시) */
    void sendRatingRequest(Long memberId, Long matchId, String matchTitle);

    /** 시스템 공지 (관리자) */
    void sendSystemNotice(Long memberId, String title, String content);
}