package com.playball.backend.domain.compliment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.playball.backend.domain.compliment.enums.ComplimentTag;

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
