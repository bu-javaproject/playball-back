package com.playball.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생하였습니다."),

    // 회원
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    SIGNUP_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 회원가입이 완료된 계정입니다."),

    // 카카오 OAuth
    KAKAO_AUTH_FAILED(HttpStatus.UNAUTHORIZED, "카카오 인증에 실패했습니다."),

    // 토큰
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

    // 경기
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 경기입니다."),
    MATCH_FULL(HttpStatus.CONFLICT, "경기 인원이 꽉 찼습니다."),
    ALREADY_JOINED(HttpStatus.CONFLICT, "이미 참가한 경기입니다."),
    MATCH_DELETED(HttpStatus.NOT_FOUND, "이미 삭제된 경기입니다."),
    MATCH_JOIN_CONFLICT(HttpStatus.CONFLICT, "다른 사용자가 동시에 참가 중입니다. 다시 시도해주세요."),
    NOT_JOINED(HttpStatus.BAD_REQUEST, "참가하지 않은 경기입니다."),
    NOT_A_PARTICIPANT(HttpStatus.FORBIDDEN, "경기에 참가하지 않은 회원입니다."),

    // 칭찬
    SELF_COMPLIMENT(HttpStatus.BAD_REQUEST, "자기 자신은 칭찬할 수 없습니다."),
    DUPLICATE_COMPLIMENT(HttpStatus.CONFLICT, "이미 칭찬한 참가자가 포함되어 있습니다.");

    private final HttpStatus status;
    private final String message;
}
