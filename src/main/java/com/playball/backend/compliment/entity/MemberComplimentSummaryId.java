package com.playball.backend.compliment.entity;

import com.playball.backend.compliment.enums.ComplimentTag;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MemberComplimentSummaryId implements Serializable {
    private Long memberId;
    private ComplimentTag tag;
}