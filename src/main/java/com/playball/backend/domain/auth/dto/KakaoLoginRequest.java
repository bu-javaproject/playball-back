package com.playball.backend.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "카카오 로그인 요청")
@Getter
@Setter
@NoArgsConstructor
public class KakaoLoginRequest {

    @Schema(description = "카카오 OAuth 2.0 인가코드 (redirect_uri로 전달된 code 파라미터)", example = "authorization_code_here")
    @NotBlank(message = "인가코드는 필수입니다")
    private String authorizationCode;
}
