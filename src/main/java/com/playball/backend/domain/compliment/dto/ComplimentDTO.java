package com.playball.backend.domain.compliment.dto;

import com.playball.backend.domain.compliment.enums.ComplimentTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "칭찬 항목")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplimentDTO {

    @Schema(description = "칭찬 ID", example = "1")
    private Long complimentId;

    @Schema(description = "경기 ID", example = "10")
    private Long matchId;

    @Schema(description = "칭찬한 회원 ID (rater)", example = "2")
    private Long raterId;

    @Schema(description = "칭찬받은 회원 ID (ratee)", example = "5")
    private Long rateeId;

    @Schema(description = "자유 코멘트", example = "같이 뛰어서 즐거웠어요!")
    private String comment;

    @Schema(description = "칭찬 등록일시", example = "2024-06-02T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "칭찬 태그 목록 (compliment_tag 테이블에서 조합)", example = "[\"MANNERS\", \"SKILL\"]")
    private List<ComplimentTag> tags;
}
