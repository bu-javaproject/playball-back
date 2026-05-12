package com.playball.backend.notification.service;

import com.playball.backend.notification.dto.DeviceTokenDTO;
import com.playball.backend.notification.dto.DeviceTokenRequest;
import com.playball.backend.notification.enums.DevicePlatform;
import com.playball.backend.notification.mapper.DeviceTokenMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenMapper deviceTokenMapper;


    /** FCM 토큰 등록 (upsert + 토큰 양도 처리) */
    @Transactional
    public void registerToken(Long memberId, DeviceTokenRequest request) {
        String token = request.getToken();
        DevicePlatform platform = request.getPlatform();

        Optional<DeviceTokenDTO> existing = deviceTokenMapper.findByToken(token);

        if (existing.isEmpty()) {
            // 경우 A: 토큰 없음 → 새로 등록
            deviceTokenMapper.insertToken(DeviceTokenDTO.builder()
                    .memberId(memberId)
                    .token(token)
                    .platform(platform)
                    .build());
            log.info("새 FCM 토큰 등록: memberId={}, platform={}", memberId, platform);
            return;
        }

        DeviceTokenDTO found = existing.get();
        if (found.getMemberId().equals(memberId)) {
            // 경우 B: 같은 사용자 → 아무것도 안 함
            //   (필요하면 updated_at 갱신만 — UPDATE 한번 쳐도 무방)
            log.debug("이미 등록된 토큰, 갱신 생략: memberId={}", memberId);
            return;
        }

        // 경우 C: 다른 사용자에게 등록된 토큰 → 본인에게로 옮김
        deviceTokenMapper.updateMemberByToken(token, memberId);
        log.info("FCM 토큰 양도: oldMemberId={} → newMemberId={}",
                found.getMemberId(), memberId);
    }


    /** FCM 토큰 삭제 (로그아웃 시) */
    @Transactional
    public void deleteToken(String token) {
        deviceTokenMapper.deleteByToken(token);
        log.info("FCM 토큰 삭제: token={}", maskToken(token));
    }


    /** 로그용 — 긴 토큰 일부만 보이게 */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) return "****";
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }
}