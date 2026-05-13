package com.playball.backend.compliment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliment")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compliment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complimentId;

    @Column(nullable = false)
    private Long matchId;

    @Column(nullable = false)
    private Long raterId;

    @Column(nullable = false)
    private Long rateeId;

    private String comment;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
