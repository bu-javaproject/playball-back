package com.playball.backend.domain.notification.service;

/**
 * 다른 도메인(매치 등)에서 알림을 트리거할 때 사용하는 인터페이스.
 * 호출하면 자동으로:
 *   1. notification 테이블에 INSERT
 *   2. 사용자 알림 설정 확인 (enabled = false면 push 안 보냄)
 *   3. FCM 푸시 전송 (사용자의 모든 등록 기기로)
 */

public interface NotificationTriggerService {

    /** 참가 신청 수신 - 주최자에게 */
    void sendApplicationReceived(Long hostId, Long matchId, String matchTitle, Long applicantMemberId);

    /** 참가 신청 수락 - 참가자에게 */
    void sendApplicationAccepted(Long memberId, Long matchId, String matchTitle);

    /** 참가 신청 거절 - 참가자에게 */
    void sendApplicationRejected(Long memberId, Long matchId, String matchTitle);

    /** 경기 시작 임박 알림 */
    void sendMatchReminder(Long memberId, Long matchId, String matchTitle);

    /** 경기 취소 알림 */
    void sendMatchCancelled(Long memberId, Long matchId, String matchTitle);

    /** 경기 종료 -> 칭찬 요청 */
    void sendRatingRequest(Long memberId, Long matchId, String matchTitle);

    /** 시스템 공지 (관리자) */
    void sendSystemNotice(Long memberId, String title, String content);
}