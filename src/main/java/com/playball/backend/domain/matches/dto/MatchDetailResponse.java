package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "경기 상세 조회 응답")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchDetailResponse {

    @Schema(description = "경기 상세 정보")
    private MatchInfo match;

    @Schema(description = "참가 회원 목록")
    private List<MemberInfo> joinedMembers;

    @Schema(description = "경기 상세 정보")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MatchInfo {

        @Schema(description = "경기 ID", example = "1")
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

        @Schema(description = "현재 참가 인원", example = "5")
        private Integer currentPlayers;

        @Schema(description = "성별 제한 (null: 무관, M: 남성만, F: 여성만)", example = "null")
        private String gender;

        @Schema(description = "연령대 제한 (null: 무관, 20: 20대 등)", example = "20")
        private Integer ageRange;

        @Schema(description = "실력 수준 (BEGINNER | INTERMEDIATE | ADVANCED)", example = "INTERMEDIATE")
        private String skillLevel;

        @Schema(description = "참가비", example = "5000")
        private Integer entryFee;

        @Schema(description = "공지 메시지", example = "초보 환영!")
        private String description;

        @Schema(description = "경기 상태 (OPEN | CLOSED | COMPLETED | DELETED)", example = "OPEN")
        private MatchStatus status;

        @Schema(description = "생성일시", example = "2024-05-01T10:00:00")
        private LocalDateTime createdAt;

        @Schema(description = "수정일시", example = "2024-05-01T10:00:00")
        private LocalDateTime updatedAt;

        public static MatchInfo from(Match match) {
            return MatchInfo.builder()
                    .matchId(match.getId())
                    .title(match.getTitle())
                    .sportType(match.getSportType() != null ? match.getSportType().name() : null)
                    .matchDate(match.getMatchDate())
                    .locationName(match.getLocationName())
                    .latitude(match.getLatitude())
                    .longitude(match.getLongitude())
                    .address(match.getAddress())
                    .maxPlayers(match.getMaxPlayers())
                    .currentPlayers(match.getCurrentPlayers())
                    .gender(match.getGender())
                    .ageRange(match.getAgeRange())
                    .skillLevel(match.getSkillLevel() != null ? match.getSkillLevel().name() : null)
                    .entryFee(match.getEntryFee())
                    .description(match.getDescription())
                    .status(match.getStatus())
                    .createdAt(match.getCreatedAt())
                    .updatedAt(match.getUpdatedAt())
                    .build();
        }
    }

    @Schema(description = "참가 회원 정보")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfo {

        @Schema(description = "회원 ID", example = "1")
        private Long memberId;

        @Schema(description = "닉네임", example = "플레이볼러")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "https://k.kakaocdn.net/...")
        private String profileImage;

        @Schema(description = "성별 (M | F)", example = "M")
        private String gender;

        @Schema(description = "나이", example = "25")
        private Integer age;

        @Schema(description = "실력 수준 (BEGINNER | INTERMEDIATE | ADVANCED)", example = "INTERMEDIATE")
        private String skillLevel;

        @Schema(description = "선호 포지션", example = "공격수")
        private String preferredPosition;

        public static MemberInfo from(Member member) {
            return MemberInfo.builder()
                    .memberId(member.getMemberId())
                    .nickname(member.getNickname())
                    .profileImage(member.getProfileImage())
                    .gender(member.getGender())
                    .age(member.getAge())
                    .skillLevel(member.getSkillLevel())
                    .preferredPosition(member.getPreferredPosition())
                    .build();
        }
    }
}
