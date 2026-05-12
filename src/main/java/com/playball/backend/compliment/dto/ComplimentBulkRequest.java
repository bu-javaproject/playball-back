package com.playball.backend.compliment.dto;

import com.playball.backend.compliment.enums.ComplimentTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ComplimentBulkRequest {

    @NotNull
    @Size(min = 1, message = "칭찬 대상이 최소 1명 필요합니다")
    @Valid  // <- 내부의 ComplimentSubmitItem 들도 검증
    private List<ComplimentSubmitItem> compliments;
}
