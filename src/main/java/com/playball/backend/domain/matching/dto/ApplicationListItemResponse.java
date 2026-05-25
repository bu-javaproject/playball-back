package com.playball.backend.domain.matching.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationListItemResponse {
    private Long applicationId;
    private Long memberId;
    private String nickname;
    private String profileImage;
    private String status;
    private LocalDateTime appliedAt;
}
