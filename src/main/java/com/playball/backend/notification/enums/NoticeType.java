package com.playball.backend.notification.enums;

public enum NoticeType {
    MATCH_FOUND,           // 1. 매칭 성사 (랜덤 매칭 발견 / 신청 수락)
    APPLICATION_REJECTED,  // 2. 신청 거절
    MATCH_REMINDER,        // 3. 경기 시작 1시간 전 리마인더
    MATCH_CANCELLED,       // 4. 경기 취소
    RATING_REQUEST,        // 5. 별점 요청
    SYSTEM_NOTICE          // 6. 시스템 공지
}
