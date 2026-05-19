package com.playball.backend.domain.matches.entity;

import com.playball.backend.domain.matches.dto.MatchCreateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private SportType sportType;

    private LocalDateTime matchDate;

    private String locationName;

    private Double latitude;
    private Double longitude;

    private String address;

    private Integer maxPlayers;

    private Integer currentPlayers;

    @Enumerated(EnumType.STRING)
    private MatchCreateRequest.SkillLevel skillLevel;

    private Integer entryFee;

    private String description;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = MatchStatus.OPEN;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, LocalDateTime matchDate, Integer maxPlayers,
                       Integer entryFee, String description) {
        if (title != null) this.title = title;
        if (matchDate != null) this.matchDate = matchDate;
        if (maxPlayers != null) this.maxPlayers = maxPlayers;
        if (entryFee != null) this.entryFee = entryFee;
        if (description != null) this.description = description;
    }

    public void markDeleted() {
        this.status = MatchStatus.DELETED;
    }

    public void incrementPlayers(MatchStatus newStatus) {
        this.currentPlayers = this.currentPlayers + 1;
        this.status = newStatus;
    }

    public void updatePlayerCount(Integer currentPlayers, MatchStatus newStatus) {
        this.currentPlayers = currentPlayers;
        this.status = newStatus;
    }
}