package com.playball.backend.security.mapper;

import com.playball.backend.security.dto.RefreshTokenDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {

    // 토큰 저장
    void insertToken(RefreshTokenDTO token);

    // 토큰 문자열로 조회
    Optional<RefreshTokenDTO> findByToken(@Param("token") String token);

    // 해당 회원의 토큰 전부 삭제 (로그아웃, 재발급 시)
    void deleteByMemberId(@Param("memberId") Long memberId);

    // 특정 토큰 하나만 삭제 (만료된 토큰 정리)
    void deleteByToken(@Param("token") String token);
}
