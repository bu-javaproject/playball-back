package com.playball.backend.domain.compliment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "칭찬 일괄 등록 요청")
@Getter
@Setter
@NoArgsConstructor
public class ComplimentBulkRequest {

    @Schema(description = "칭찬 목록 (1명 이상)")
    @NotNull
    @Size(min = 1, message = "칭찬 대상이 최소 1명 필요합니다")
    @Valid
    private List<ComplimentSubmitItem> compliments;
}
