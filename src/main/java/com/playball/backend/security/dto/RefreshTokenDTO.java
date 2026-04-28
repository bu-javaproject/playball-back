package com.playball.backend.security.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDTO {

    private Long tokenId;
    private Long memberId;
    private String token;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
}
