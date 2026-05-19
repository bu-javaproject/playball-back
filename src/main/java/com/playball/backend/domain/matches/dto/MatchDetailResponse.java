package com.playball.backend.domain.matches.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.playball.backend.domain.matches.entity.Match;
import com.playball.backend.domain.matches.entity.MatchStatus;
import com.playball.backend.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchDetailResponse {

    private MatchInfo match;
    private List<MemberInfo> joinedMembers;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MatchInfo {
        private Long matchId;
        private String title;
        private String sportType;
        private LocalDateTime matchDate;
        private String locationName;
        private Double latitude;
        private Double longitude;
        private String address;
        private Integer maxPlayers;
        private Integer currentPlayers;
        private String skillLevel;
        private Integer entryFee;
        private String description;
        private MatchStatus status;
        private LocalDateTime createdAt;
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
                    .skillLevel(match.getSkillLevel() != null ? match.getSkillLevel().name() : null)
                    .entryFee(match.getEntryFee())
                    .description(match.getDescription())
                    .status(match.getStatus())
                    .createdAt(match.getCreatedAt())
                    .updatedAt(match.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfo {
        private Long memberId;
        private String nickname;
        private String gender;
        private Integer age;
        private String skillLevel;
        private String preferredPosition;

        public static MemberInfo from(Member member) {
            return MemberInfo.builder()
                    .memberId(member.getMemberId())
                    .nickname(member.getNickname())
                    .gender(member.getGender())
                    .age(member.getAge())
                    .skillLevel(member.getSkillLevel())
                    .preferredPosition(member.getPreferredPosition())
                    .build();
        }
    }
}
