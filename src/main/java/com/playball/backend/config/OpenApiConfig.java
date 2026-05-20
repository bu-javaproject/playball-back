package com.playball.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Playball API")
                        .description("스포츠 매칭 서비스 Playball의 REST API 문서입니다.\n\n"
                                + "**인증이 필요한 API**는 우측 자물쇠 아이콘을 클릭한 후 "
                                + "로그인 API(/api/auth/kakao)로 발급받은 accessToken을 입력하세요.")
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 개발 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("로그인 후 발급된 JWT Access Token을 입력하세요. 'Bearer ' 없이 토큰만 입력합니다.")));
    }
}
