package com.playball.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "회원 전체 정보 (회원가입 완료 응답용)")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "카카오 고유 ID", example = "3123456789")
    private Long kakaoId;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "닉네임", example = "플레이볼러")
    private String nickname;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "성별 (M | F)", example = "M")
    private String gender;

    @Schema(description = "나이", example = "25")
    private Integer age;

    @Schema(description = "프로필 이미지 URL", example = "https://k.kakaocdn.net/...")
    private String profileImage;

    @Schema(description = "실력 수준 (BEGINNER | INTERMEDIATE | ADVANCED)", example = "INTERMEDIATE")
    private String skillLevel;

    @Schema(description = "선호 포지션", example = "공격수")
    private String preferredPosition;

    @Schema(description = "위도", example = "37.5665")
    private Double latitude;

    @Schema(description = "경도", example = "126.9780")
    private Double longitude;

    @Schema(description = "활동지역 주소", example = "서울특별시 강남구")
    private String address;

    @Schema(description = "권한 (ROLE_USER | ROLE_ADMIN)", example = "ROLE_USER")
    private String role;

    @Schema(description = "회원가입 완료 여부", example = "true")
    private Boolean signupCompleted;

    @Schema(description = "가입일시", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-01-01T00:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "선호 종목 목록 (favorite_sport 테이블에서 조합)", example = "[\"SOCCER\", \"BASKETBALL\"]")
    private List<String> favoriteSports;
}
