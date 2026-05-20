package com.playball.backend.domain.compliment.dto;

import com.playball.backend.domain.compliment.enums.ComplimentTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Schema(description = "회원 칭찬 통계 (프로필용)")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplimentSummaryDTO {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "받은 칭찬 총 개수", example = "42")
    private int totalCount;

    @Schema(description = "태그별 칭찬 개수 (MANNERS | SKILL | PUNCTUAL | PASSIONATE | MOOD_MAKER)",
            example = "{\"MANNERS\": 10, \"SKILL\": 15, \"PUNCTUAL\": 8, \"PASSIONATE\": 5, \"MOOD_MAKER\": 4}")
    private Map<ComplimentTag, Integer> tagCounts;
}
