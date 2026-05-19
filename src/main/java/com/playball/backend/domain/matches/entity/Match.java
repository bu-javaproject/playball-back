package com.playball.backend.domain.matches.entity;

import com.playball.backend.domain.matches.dto.MatchCreateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter
@Setter
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
    @Builder.Default
    private MatchStatus status = MatchStatus.OPEN;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}