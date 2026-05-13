package com.playball.backend.compliment.entity;

import com.playball.backend.compliment.enums.ComplimentTag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compliment_tag")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplimentTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complimentTagId;

    @Column(nullable = false)
    private Long complimentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplimentTag tag;
}
