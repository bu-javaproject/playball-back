package com.playball.backend.domain.notification.enums;

public enum NoticeType {
    APPLICATION_RECEIVED,  //주최자 수신 : 참가 신청이 들어옴
    APPLICATION_ACCEPTED,  //참가자 수신 : 신청 수락됨
    APPLICATION_REJECTED,  //참가자 수신 : 신청 거절됨
    MATCH_REMINDER,
    MATCH_CANCELLED,
    RATING_REQUEST,
    SYSTEM_NOTICE

}
