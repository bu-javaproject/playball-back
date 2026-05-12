package com.playball.backend.compliment.dto;

import com.playball.backend.compliment.enums.ComplimentTag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ComplimentSubmitItem {

    @NotNull(message = "칭찬 대상 ID는 필수입니다")
    private Long rateeId;

    @NotNull(message = "태그를 1개 이상 선택해주세요")
    @Size(min = 1, max = 5, message = "태그는 1~5개")
    private List<ComplimentTag> tags;

    @Size(max = 200, message = "코멘트는 200자 이내")
    private String comment;
}
