package com.playball.backend.domain.compliment.dto;

import com.playball.backend.domain.compliment.enums.ComplimentTag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "칭찬 대상 1명 항목")
@Getter
@Setter
@NoArgsConstructor
public class ComplimentSubmitItem {

    @Schema(description = "칭찬받는 회원 ID (rateeId)", example = "5")
    @NotNull(message = "칭찬 대상 ID는 필수입니다")
    private Long rateeId;

    @Schema(description = "칭찬 태그 목록 1~5개 (MANNERS | SKILL | PUNCTUAL | PASSIONATE | MOOD_MAKER)", example = "[\"MANNERS\", \"SKILL\"]")
    @NotNull(message = "태그를 1개 이상 선택해주세요")
    @Size(min = 1, max = 5, message = "태그는 1~5개")
    private List<ComplimentTag> tags;

    @Schema(description = "자유 코멘트 (200자 이내, 선택)", example = "같이 뛰어서 즐거웠어요!")
    @Size(max = 200, message = "코멘트는 200자 이내")
    private String comment;
}
