package com.playball.backend.domain.matches.entity;

import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import com.playball.backend.domain.matches.dto.MatchCreateRequest;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EntityScan
@Table(name = "matches")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    // 매치 ID PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 매치 제목
    private String title;

    // 스포츠 종목
    @Enumerated(EnumType.STRING)
    private SportType sportType;

    // 경기 날짜/시간
    private LocalDateTime matchDate;

    // 장소명
    private String locationName;

    // 위도, 경도
    private Double latitude;
    private Double longitude;

    // 주소
    private String address;

    // 최대 인원
    private Integer maxPlayers;

    // 현재 인원(참여중인)
    private Integer currentPlayers;

    // 사용자 스킬 레벨
    @Enumerated(EnumType.STRING)
    private MatchCreateRequest.SkillLevel skillLevel;

    // 참가비(0이면 무료)
    private Integer entryFee;

    // 공지 메시지(설명)
    private String description;

    // 상태
    @Enumerated(EnumType.STRING)
    private MatchStatus status; // OPEN, CLOSED, DELETED

    // 수정일시
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

}