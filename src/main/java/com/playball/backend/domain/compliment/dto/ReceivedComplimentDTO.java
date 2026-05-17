package com.playball.backend.domain.compliment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.playball.backend.domain.compliment.enums.ComplimentTag;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedComplimentDTO {
    private Long complimentId;
    private Long matchId;
    private String matchTitle;
    private String raterNickname;
    private List<ComplimentTag> tags;
    private String comment;
    private LocalDateTime createdAt;
}
