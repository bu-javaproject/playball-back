package com.playball.backend.notification.service;

import com.playball.backend.notification.dto.DeviceTokenRequest;
import com.playball.backend.notification.entity.DeviceToken;
import com.playball.backend.notification.repository.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    @Transactional
    public void registerToken(Long memberId, DeviceTokenRequest request) {
        String token = request.getToken();

        Optional<DeviceToken> existing = deviceTokenRepository.findByToken(token);

        if (existing.isEmpty()) {
            deviceTokenRepository.save(DeviceToken.builder()
                    .memberId(memberId)
                    .token(token)
                    .platform(request.getPlatform())
                    .build());
            log.info("새 FCM 토큰 등록: memberId={}, platform={}", memberId, request.getPlatform());
            return;
        }

        DeviceToken found = existing.get();
        if (found.getMemberId().equals(memberId)) {
            log.debug("이미 등록된 토큰, 갱신 생략: memberId={}", memberId);
            return;
        }

        found.setMemberId(memberId);
        deviceTokenRepository.save(found);
        log.info("FCM 토큰 양도: oldMemberId={} → newMemberId={}", found.getMemberId(), memberId);
    }

    @Transactional
    public void deleteToken(String token) {
        deviceTokenRepository.deleteByToken(token);
        log.info("FCM 토큰 삭제: token={}", maskToken(token));
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 8) return "****";
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }
}