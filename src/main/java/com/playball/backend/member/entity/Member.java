package com.playball.backend.member.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

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
}