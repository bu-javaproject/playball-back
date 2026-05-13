package com.playball.backend.notification.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import com.playball.backend.notification.dto.NotificationSettingDTO;
import com.playball.backend.notification.entity.NotificationSetting;
import com.playball.backend.notification.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSettingService {

    private final NotificationSettingRepository settingRepository;

    @Transactional(readOnly = true)
    public NotificationSettingDTO getMySetting(Long memberId) {
        return settingRepository.findById(memberId)
                .map(this::toDto)
                .orElseGet(() -> NotificationSettingDTO.builder()
                        .memberId(memberId)
                        .enabled(true)
                        .updatedAt(null)
                        .build());
    }

    @Transactional
    public NotificationSettingDTO updateMySetting(Long memberId, Boolean enabled) {
        if (enabled == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        NotificationSetting setting = settingRepository.findById(memberId)
                .orElseGet(() -> NotificationSetting.builder()
                        .memberId(memberId)
                        .enabled(true)
                        .build());

        setting.updateEnabled(enabled);
        NotificationSetting saved = settingRepository.save(setting);

        return toDto(saved);
    }

    private NotificationSettingDTO toDto(NotificationSetting s) {
        return NotificationSettingDTO.builder()
                .memberId(s.getMemberId())
                .enabled(s.getEnabled())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
