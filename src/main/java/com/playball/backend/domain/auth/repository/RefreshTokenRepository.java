package com.playball.backend.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playball.backend.domain.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByMemberId(Long memberId);
}
