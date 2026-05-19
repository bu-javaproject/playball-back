package com.playball.backend.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playball.backend.domain.notification.entity.NotificationSetting;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
}
