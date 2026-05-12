package com.playball.backend.compliment.dto;

import com.playball.backend.compliment.enums.ComplimentTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplimentSummaryDTO {
    private Long memberId;
    private int totalCount;
    private Map<ComplimentTag, Integer> tagCounts; //태그별 개수
}
