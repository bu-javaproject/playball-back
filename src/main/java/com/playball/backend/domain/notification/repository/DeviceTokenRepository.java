package com.playball.backend.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playball.backend.domain.notification.entity.DeviceToken;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    Optional<DeviceToken> findByToken(String token);

    List<DeviceToken> findByMemberId(Long memberId);

    void deleteByToken(String token);
}
