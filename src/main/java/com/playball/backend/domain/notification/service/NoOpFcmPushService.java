package com.playball.backend.domain.notification.service;

import org.springframework.stereotype.Service;

@Service
public class NoOpFcmPushService implements FcmPushService {
    @Override
    public void sendPush(String token, String title, String content) {
        // firebase-service-account.json 없을 때 아무것도 안 함
    }
}
