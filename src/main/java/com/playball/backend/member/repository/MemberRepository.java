package com.playball.backend.member.repository;

import com.playball.backend.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByKakaoId(Long kakaoId);

    Optional<Member> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);
}