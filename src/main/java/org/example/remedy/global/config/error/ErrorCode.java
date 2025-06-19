package org.example.remedy.global.config.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U1", "존재하지 않는 유저입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "U2", "만료된 JWT 토큰입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "U3", "올바르지 않은 JWT 토큰입니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U4", "이미 존재하는 유저입니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "S1", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "S2", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3", "서버 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "S4", "존재하지 않는 엔티티입니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "S5", "이미 존재하는 엔티티입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
