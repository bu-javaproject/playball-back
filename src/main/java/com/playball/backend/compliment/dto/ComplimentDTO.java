package com.playball.backend.compliment.dto;

import com.playball.backend.compliment.enums.ComplimentTag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplimentDTO {
    private Long complimentId;
    private Long matchId;
    private Long raterId;
    private Long rateeId;
    private String comment;
    private LocalDateTime createdAt;

    //DB에 없지만 tag 테이블에서 가져와서 합침
    private List<ComplimentTag> tags;
}
