package com.playball.backend.domain.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmPushServiceMock implements FcmPushService {

    @Override
    public void sendPush(String token, String title, String content) {
        //실제 FCM 호출 대신 로그만 출력
        log.info("📱 [FCM-MOCK] 푸시 발송: token={}, title={}, content={}",
                maskToken(token), title, content);
    }

    private String maskToken(String token) {
        if(token == null || token.length() < 8) return "****";
        return token.substring(0, 4) + "...." + token.substring(token.length() - 4);
    }
}
