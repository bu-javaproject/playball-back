package com.playball.backend.notification.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.notification.dto.NotificationSettingDTO;
import com.playball.backend.notification.mapper.NotificationSettingMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingMapper settingMapper;

    //내 알림 설정 조회. row 없으면 기본값 응답
    @Transactional(readOnly = true)
    public NotificationSettingDTO getMySetting(Long memberId) {
        return settingMapper.findByMemberId(memberId)
                .orElseGet(() -> NotificationSettingDTO.builder()
                        .memberId(memberId)
                        .enabled(true)    //기본값: 알림 ON
                        .updatedAt(null)  //한 번도 설정한 적 없음
                        .build());
    }

    //알림 ON/OFF 토글 (upsert)
    @Transactional
    public NotificationSettingDTO updateMySetting(Long memberId, Boolean enabled) {
        if(enabled == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        //1. 기존 설정 있는지 조회
        boolean exists = settingMapper.findByMemberId(memberId).isPresent();

        //2. 있으면 update, 없으면 insert
        if(exists) {
            settingMapper.updateEnable(memberId, enabled);
        } else {
            settingMapper.insertSetting(
                    NotificationSettingDTO.builder()
                            .memberId(memberId)
                            .enabled(enabled)
                            .build()
            );
        }

        //3. 변경된 결과 다시 조회해서 반환
        return settingMapper.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }


}
