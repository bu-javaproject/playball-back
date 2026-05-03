package com.playball.backend.domain.matches.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchCreateRequest {

    @NotBlank
    private String title; // 경기 제목

    @NotBlank
    private String sportType; // 종목

    @NotNull
    private LocalDateTime matchDate; // 경기 날짜/시간

    private String locationName; // 장소명

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String address;

    @NotNull
    @Min(1)
    private Integer maxPlayers; // 최대 인원

    private SkillLevel skillLevel;

    @Builder.Default
    private Integer entryFee = 0; // 참가비 (기본 0)

    private String description; // 공지 메시지

    public enum SkillLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }
}