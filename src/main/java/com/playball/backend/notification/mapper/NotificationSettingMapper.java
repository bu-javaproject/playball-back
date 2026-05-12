package com.playball.backend.notification.mapper;

import com.playball.backend.notification.dto.NotificationSettingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

@Mapper
public interface NotificationSettingMapper {

    //회원 설정 조회 (없으면 Optional.empty()
    Optional<NotificationSettingDTO> findByMemberId(@Param("memberId") Long memberId);

    //새 설정 INSERT (첫 진입 시)
    void insertSetting(NotificationSettingDTO setting);

    //기존 설정 UPDATE (토글 변경 시)
    int updateEnable(@Param("memberId") Long memberId,
                     @Param("enabled") Boolean enabled);

}
