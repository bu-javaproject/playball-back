package com.playball.backend.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private Long kakaoId;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
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

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "favorite_sport", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "sport_type")
    @Builder.Default
    private List<String> favoriteSports = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void completeSignup(String nickname, String gender, Integer age, String address,
                               Double latitude, Double longitude, String skillLevel, String preferredPosition) {
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.skillLevel = skillLevel;
        this.preferredPosition = preferredPosition;
        this.signupCompleted = true;
    }

    public void updateProfile(String nickname, String address, Double latitude, Double longitude) {
        if (nickname != null && !nickname.isBlank()) this.nickname = nickname;
        if (address != null) this.address = address;
        if (latitude != null) this.latitude = latitude;
        if (longitude != null) this.longitude = longitude;
    }

    public void updateLocation(Double latitude, Double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
