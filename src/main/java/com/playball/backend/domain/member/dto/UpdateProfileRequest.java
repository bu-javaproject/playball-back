package com.playball.backend.domain.member.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 2, max = 10)
    private String nickname;      // null이면 변경 안 함

    private String address;       // 활동지역

    private Double latitude;
    private Double longitude;

    private List<String> favoriteSports;  // 선호운동
}
