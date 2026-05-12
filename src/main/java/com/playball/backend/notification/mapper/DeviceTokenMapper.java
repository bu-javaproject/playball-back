package com.playball.backend.notification.mapper;

import com.playball.backend.notification.dto.DeviceTokenDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DeviceTokenMapper {

    /** 토큰 문자열로 검색 (upsert 분기용) */
    Optional<DeviceTokenDTO> findByToken(@Param("token") String token);

    /** 새 토큰 등록 */
    void insertToken(DeviceTokenDTO deviceToken);

    /** 토큰의 소유자 변경 (양도 처리) */
    int updateMemberByToken(@Param("token") String token,
                            @Param("memberId") Long memberId);

    /** 한 사용자의 모든 기기 토큰 (FCM 푸시 시 사용 예정) */
    List<DeviceTokenDTO> findByMemberId(@Param("memberId") Long memberId);

    /** 토큰 삭제 (로그아웃 등) */
    int deleteByToken(@Param("token") String token);
}
