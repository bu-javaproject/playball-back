package com.playball.backend.domain.matching.entity;


import java.time.LocalDateTime;

import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.member.entity.Member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "match_participant",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_match_member",
            columnNames = {"match_id", "member_id"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    /**
     * 경기
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "match_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_participant_match")
    )
    private Match match;

    /**
     * 참가자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "member_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_participant_member")
    )
    private Member member;

    /**
     * 참가 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipantStatus status;

    /**
     * 신청 시간
     */
    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    /**
     * 수정 시간
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public MatchParticipant(
        Match match,
        Member member,
        ParticipantStatus status
    ) {
        this.match = match;
        this.member = member;
        this.status = status;
    }

    /**
     * 참가 승인
     */
    public void approve() {
        this.status = ParticipantStatus.APPROVED;
    }

    /**
     * 참가 거절
     */
    public void reject() {
        this.status = ParticipantStatus.REJECTED;
    }

    /**
     * 신청 취소
     */
    public void cancel() {
        this.status = ParticipantStatus.CANCELLED;
    }

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = ParticipantStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}