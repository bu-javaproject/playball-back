package com.playball.backend.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique = true)
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

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "favorite_sport", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "sport_type")
    @Builder.Default
    private List<String> favoriteSports = new ArrayList<>();
}