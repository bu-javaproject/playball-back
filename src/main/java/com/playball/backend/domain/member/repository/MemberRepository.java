package com.playball.backend.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playball.backend.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByKakaoId(Long kakaoId);

    Optional<Member> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);
}