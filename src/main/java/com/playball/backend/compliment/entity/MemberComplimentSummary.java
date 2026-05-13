package com.playball.backend.compliment.entity;

import com.playball.backend.compliment.enums.ComplimentTag;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_compliment_summary")
@IdClass(MemberComplimentSummaryId.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberComplimentSummary {

    @Id
    private Long memberId;

    @Id
    @Enumerated(EnumType.STRING)
    private ComplimentTag tag;

    @Builder.Default
    private Integer count = 0;

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
