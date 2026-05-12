package com.playball.backend.compliment.dto;

import com.playball.backend.compliment.enums.ComplimentTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberComplimentSummaryDTO {
    private Long memberId;
    private ComplimentTag tag;
    private Integer count;
    private LocalDateTime updatedAt;
}
