package com.playball.backend.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private Long memberId;
    private String nickname;
    private String gender;
    private Integer age;
    private String profileImage;
    private String address;
    private String skillLevel;
    private String preferredPosition;
    private List<String> favoriteSports;
}
