package com.playball.backend.domain.compliment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.playball.backend.domain.compliment.enums.ComplimentTag;

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

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
