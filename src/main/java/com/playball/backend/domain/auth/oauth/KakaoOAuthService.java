package com.playball.backend.domain.auth.oauth;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Slf4j
@Service
public class KakaoOAuthService {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    private final WebClient webClient = WebClient.builder().build();

    public String getAccessToken(String authorizationCode, String redirectUri) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "authorization_code");
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("redirect_uri", redirectUri);
            formData.add("code", authorizationCode);

            Map<String, Object> response = webClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(MAP_TYPE)
                    .block();

            if (response == null || !response.containsKey("access_token")) {
                throw new CustomException(ErrorCode.KAKAO_AUTH_FAILED);
            }
            return response.get("access_token").toString();
        } catch (CustomException e) {
            throw e;
        } catch (WebClientResponseException e) {
            log.error("카카오 토큰 교환 실패 : {} / 응답 본문: {}", e.getMessage(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.KAKAO_AUTH_FAILED);
        } catch (Exception e) {
            log.error("카카오 토큰 교환 실패 : {}", e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_AUTH_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(userInfoUri)
                    .header("Authorization", "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .bodyToMono(MAP_TYPE)
                    .block();

            if (response == null || !response.containsKey("id")) {
                throw new CustomException(ErrorCode.KAKAO_AUTH_FAILED);
            }

            Long kakaoUserId = Long.parseLong(response.get("id").toString());

            Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
            Map<String, Object> profile = kakaoAccount != null
                    ? (Map<String, Object>) kakaoAccount.get("profile") : null;

            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            String nickname = profile != null ? (String) profile.get("nickname") : null;
            String profileImage = profile != null ? (String) profile.get("profile_image_url") : null;

            return new KakaoUserInfo(kakaoUserId, email, nickname, profileImage);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_AUTH_FAILED);
        }
    }

    public record KakaoUserInfo(
            Long kakaoId,
            String email,
            String nickname,
            String profileImage
    ) {}
}