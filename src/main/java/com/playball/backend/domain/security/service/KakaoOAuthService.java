package com.playball.backend.domain.security.service;

import com.playball.backend.common.exception.CustomException;
import com.playball.backend.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class KakaoOAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.token-uri}")
    private String tokenUri;              // https://kauth.kakao.com/oauth/token

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;           // https://kapi.kakao.com/v2/user/me

    private final WebClient webClient = WebClient.builder().build();

    public String getAccessToken(String authorizationCode, String redirectUri) {
        try {
            Map<String, Object> response = webClient.post()              //POST 요청
                    .uri(tokenUri)                                       // https://kauth.kakao.com/oauth/token
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)  // 카카오가 이 형식을 요구함
                    .bodyValue("grant_type=authorization_code"           // 요청 바디
                            + "&client_id=" + clientId
                            + "&client_secret=" + clientSecret
                            + "&redirect_uri=" + redirectUri
                            + "&code=" + authorizationCode)
                    .retrieve()                                          // 요청 보내기
                    .bodyToMono(Map.class)                               // 응답을 Map으로 변환
                    .block();                                            // 응답 올 때까지 기다리기
            if (response == null || !response.containsKey("access_token")) {
                throw new CustomException(ErrorCode.KAKAO_AUTH_FAILED);
            }

            return response.get("access_token").toString();
        } catch (CustomException e) {
            throw e;     // 우리가 던진 예외 -> 그대로 전달
        } catch (Exception e) {
            log.error("카카오 토큰 교환 실패 : {}", e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_AUTH_FAILED); //예상 못한 에러 -> 감싸서 전달
        }
    }

    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        try{
            Map<String, Object> response = webClient.get()
                    .uri(userInfoUri)
                    .header("Authorization", "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

        if (response == null || !response.containsKey("id")) {
            throw new CustomException(ErrorCode.KAKAO_AUTH_FAILED);
        }

        Long kakaoUserId = Long.parseLong(response.get("id").toString());

        //카카오 계정 정보 파싱
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) response.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null
                ? (Map<String, Object>) kakaoAccount.get("profile")
                : null;

        String email = kakaoAccount != null
                ? (String) kakaoAccount.get("email") : null;
        String nickname = profile != null
                ? (String) profile.get("nickname") : null;
        String profileImage =  profile != null
                ? (String) profile.get("profile_image_url") : null;

        return new KakaoUserInfo(kakaoUserId, email, nickname, profileImage);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.KAKAO_AUTH_FAILED);
        }

    }

    //카카오 정보 AuthService가 이걸 받아서 DB에 저장할지, 기존 회원인지 판단
    public record KakaoUserInfo(
            Long kakaoId,
            String email,
            String nickname,
            String profileImage
    ) {}

}
