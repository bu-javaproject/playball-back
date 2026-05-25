package com.playball.backend.domain.notification.service;


import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@ConditionalOnBean(FirebaseApp.class)
@RequiredArgsConstructor
public class FcmPushServiceImpl implements FcmPushService {

    @Override
    public void sendPush(String token, String title, String content) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(content)
                            .build())
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("[FCM] 푸시 발송 성공: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("[FCM] 푸시 발송 실패: token={}, error={}", token, e.getMessage());
        }
    }
}

