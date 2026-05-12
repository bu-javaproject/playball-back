package com.playball.backend.domain.matches.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchUpdateRequest {

    private String title;

    private LocalDateTime matchDate;

    @Min(1)
    private Integer maxPlayers;

    private Integer entryFee;

    private String description; // 공지 메시지
}