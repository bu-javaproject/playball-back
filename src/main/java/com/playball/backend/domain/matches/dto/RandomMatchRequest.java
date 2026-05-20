package com.playball.backend.domain.matches.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomMatchRequest {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String address;

    private LocalDate date;

    @NotBlank
    private String sportType;

    private String preferredPosition;

    private Gender gender;

    private Integer ageRange;

    private String skillLevel;

    private Integer maxFee;

    public enum Gender {
        M, F
    }

    @Builder.Default
    private Double radius = 5.0; // km
}