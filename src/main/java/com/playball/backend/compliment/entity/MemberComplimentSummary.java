package com.playball.backend.compliment.entity;

import com.playball.backend.compliment.enums.ComplimentTag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "member_compliment_summary")
@IdClass(MemberComplimentSummaryId.class)
@Getter
@Setter
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

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void increment() {
        this.count = (this.count == null ? 0 : this.count) + 1;
    }
}