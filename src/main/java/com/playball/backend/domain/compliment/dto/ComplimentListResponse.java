package com.playball.backend.domain.compliment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "받은 칭찬 목록 응답 (커서 기반 페이지네이션)")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplimentListResponse {

    @Schema(description = "칭찬 목록")
    private List<ComplimentDTO> items;

    @Schema(description = "다음 페이지 커서 ID (다음 페이지 없으면 null)", example = "42")
    private Long nextCursor;
}
