package com.playball.backend.domain.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playball.backend.domain.security.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByMemberId(Long memberId);

    void deleteByToken(String token);
}
