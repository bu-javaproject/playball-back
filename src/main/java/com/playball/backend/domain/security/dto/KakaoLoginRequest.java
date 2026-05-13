package com.playball.backend.domain.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoLoginRequest {

    @NotBlank(message = "인가코드는 필수입니다")
    private String authorizationCode;

    private String redirectUri;


}
