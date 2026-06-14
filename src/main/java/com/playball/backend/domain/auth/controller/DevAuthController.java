package com.playball.backend.domain.auth.controller;

import com.playball.backend.common.dto.ApiResponse;
import com.playball.backend.domain.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Profile("dev")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class DevAuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @RequestMapping(value = "/dev-login", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<Map<String, String>> devLogin(@RequestParam Long memberId) {
        String token = jwtTokenProvider.createAccessToken(String.valueOf(memberId), "USER");
        return ApiResponse.ok("개발용 로그인 성공", Map.of("accessToken", token));
    }
}
