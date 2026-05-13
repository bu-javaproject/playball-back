package com.playball.backend.compliment.entity;

import com.playball.backend.compliment.enums.ComplimentTag;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberComplimentSummaryId implements Serializable {

    private Long memberId;
    private ComplimentTag tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberComplimentSummaryId that)) return false;
        return Objects.equals(memberId, that.memberId) && tag == that.tag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, tag);
    }
}
