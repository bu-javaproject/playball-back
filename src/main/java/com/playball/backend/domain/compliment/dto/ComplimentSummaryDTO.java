package com.playball.backend.domain.compliment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

import com.playball.backend.domain.compliment.enums.ComplimentTag;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplimentSummaryDTO {
    private Long memberId;
    private int totalCount;
    private Map<ComplimentTag, Integer> tagCounts; //태그별 개수
}
