package com.playball.backend.notification.service;

/**
 * FCM 푸시 전송. Mock 구현으로 시작 → 추후 firebase-admin 으로 교체.
 */
public interface FcmPushService {

    //한 기기에 푸시 1건 발송
    void sendPush(String token, String title, String content);

}
