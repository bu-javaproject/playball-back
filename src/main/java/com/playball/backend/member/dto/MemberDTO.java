package com.playball.backend.member.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private Long memberId;
    private Long kakaoId;
    private String email;
    private String nickname;
    private String name;
    private String phone;
    private String gender;
    private Integer age;
    private String profileImage;
    private String skillLevel;
    private String preferredPosition;
    private Double latitude;
    private Double longitude;
    private String address;
    private String role;
    private Boolean signupCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //DB에는 없지만 favorite_sport 테이블에서 가져와서 합침
    private List<String> favoriteSports;
}
