package com.playball.backend.notification.enums;

//알림이 가리키는 대상 객체의 타입
//알림 클릭 시 client가 어디로 이동할지 결정
//MATCH: 매치 상세 화면
//MEMBER: 사용자 프로필
public enum NotificationTargetType {
    MATCH,
    MEMBER
}
