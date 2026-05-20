package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;

import com.playball.backend.domain.matches.entity.MatchStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyMatchResponse {

    private Long matchId;         // 경기 고유 ID
    private String title;          // 경기 제목
    private String sportType;      // 종목 (예: SOCCER, BASKETBALL)
    private LocalDateTime matchDate; // 경기 일시

    private String locationName;   // 장소명
    private Double latitude;       // 위도
    private Double longitude;      // 경도

    private Integer maxPlayers;    // 최대 참가 인원
    private Integer currentPlayers; // 현재 참가 인원

    private String gender;         // 성별 제한 (예: MALE, FEMALE, ANY)
    private Integer ageRange;      // 연령대

    private MatchStatus status;    // 경기 상태 (OPEN, CLOSED, DELETED 등)

    private LocalDateTime updatedAt; // 최종 수정 일시

    private Long hostId;           // 주최자 멤버 ID
    private String hostName;       // 주최자 닉네임
}
