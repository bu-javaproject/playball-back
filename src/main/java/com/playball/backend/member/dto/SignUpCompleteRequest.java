package com.playball.backend.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SignUpCompleteRequest {

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다")
    private String nickname;

    @NotBlank(message = "성별은 필수입니다")
    private String gender;

    @NotNull(message = "나이는 필수입니다")
    private Integer age;

    private String address;
    private Double  latitude;
    private Double longitude;

    @NotNull(message = "종목을 최소 1개 선택해주세요")
    private List<String> favoriteSports;

    private String skillLevel;
    private String preferedPosition;
}
