package com.playball.backend.compliment.entity;

import com.playball.backend.compliment.enums.ComplimentTag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private Long matchId;
    private Long raterId;
    private Long rateeId;
    private String comment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "compliment_tag", joinColumns = @JoinColumn(name = "compliment_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    @Builder.Default
    private List<ComplimentTag> tags = new ArrayList<>();
}