package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;

import com.playball.backend.domain.matches.entity.MatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "경기 생성 응답")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchCreateResponse {

    @Schema(description = "생성된 경기 ID", example = "1")
    private Long matchId;

    @Schema(description = "경기 제목", example = "강남 풋살 같이 하실 분!")
    private String title;

    @Schema(description = "종목 (SOCCER | BASKETBALL | RUNNING | BADMINTON)", example = "SOCCER")
    private String sportType;

    @Schema(description = "경기 일시", example = "2024-06-01T18:00:00")
    private LocalDateTime matchDate;

    @Schema(description = "장소명", example = "강남 스포츠센터")
    private String locationName;

    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "최대 참가 인원", example = "10")
    private Integer maxPlayers;

    @Schema(description = "현재 참가 인원 (생성 직후 1: 호스트 포함)", example = "1")
    private Integer currentPlayers;

    @Schema(description = "성별 제한 (null: 무관)", example = "null")
    private String gender;

    @Schema(description = "연령대 제한 (null: 무관)", example = "20")
    private Integer ageRange;

    @Schema(description = "실력 수준 (BEGINNER | INTERMEDIATE | ADVANCED)", example = "INTERMEDIATE")
    private String skillLevel;

    @Schema(description = "참가비", example = "5000")
    private Integer entryFee;

    @Schema(description = "공지 메시지", example = "초보 환영! 풋살화 필참")
    private String description;

    @Schema(description = "경기 상태 (OPEN | CLOSED | COMPLETED | DELETED)", example = "OPEN")
    private MatchStatus status;
}
