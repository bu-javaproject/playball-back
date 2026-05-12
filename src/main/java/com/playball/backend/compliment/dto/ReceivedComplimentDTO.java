package com.playball.backend.compliment.dto;

import com.playball.backend.compliment.enums.ComplimentTag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
