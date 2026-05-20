package com.playball.backend.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "토큰 재발급 요청")
@Getter
@Setter
@NoArgsConstructor
public class TokenRefreshRequest {

    @Schema(description = "만료된 Access Token 갱신에 사용할 Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @NotBlank(message = "Refresh Token은 필수입니다")
    private String refreshToken;
}
