package org.example.remedy.global.error;

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
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "U5", "존재하지 않는 RefreshToken입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U6", "올바르지 않은 비밀번호입니다."),

    DROPPING_ALREADY_EXISTS(HttpStatus.CONFLICT, "D1", "반경 5미터 이내에 이미 드랍이 존재합니다."),
    DROPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "D2", "삭제되었거나 존재하지 않는 드랍입니다."),
    SONG_NOT_FOUND(HttpStatus.NOT_FOUND, "D3", "존재하지 않는 노래입니다."),
    METADATA_NOT_FOUND(HttpStatus.NOT_FOUND, "D4", "유튜브의 메타데이터가 존재하지 않습니다."),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "S1", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "S2", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3", "서버 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "S4", "존재하지 않는 엔티티입니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "S5", "이미 존재하는 엔티티입니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C1", "존재하지 않는 댓글입니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C2", "본인 댓글만 수정/삭제할 수 있습니다."),

    ACHIEVEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "A1", "존재하지 않는 도전과제입니다."),
    USER_ACHIEVEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "A2", "사용자 도전과제 진행 정보를 찾을 수 없습니다."),
    ACHIEVEMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "A3", "이미 완료된 도전과제입니다."),
    ACHIEVEMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "A4", "완료되지 않은 도전과제입니다."),
    REWARD_ALREADY_CLAIMED(HttpStatus.BAD_REQUEST, "A5", "이미 보상을 받은 도전과제입니다."),

    USER_CURRENCY_NOT_FOUND(HttpStatus.NOT_FOUND, "M1", "사용자 재화 정보를 찾을 수 없습니다."),
    INSUFFICIENT_CURRENCY(HttpStatus.BAD_REQUEST, "M2", "재화가 부족합니다."),

    TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "T1", "존재하지 않는 칭호입니다."),
    TITLE_ALREADY_EXISTS(HttpStatus.CONFLICT, "T2", "이미 존재하는 칭호명입니다."),
    TITLE_NOT_OWNED(HttpStatus.FORBIDDEN, "T3", "보유하지 않은 칭호입니다."),
    TITLE_ALREADY_OWNED(HttpStatus.BAD_REQUEST, "T4", "이미 보유한 칭호입니다."),
    TITLE_ALREADY_EQUIPPED(HttpStatus.BAD_REQUEST, "T5", "이미 장착한 칭호입니다."),
    TITLE_NOT_EQUIPPED(HttpStatus.BAD_REQUEST, "T6", "장착되지 않은 칭호입니다."),

    RUNNING_ALREADY_EXISTS(HttpStatus.CONFLICT, "R1", "이미 해당 노래로 러닝 기록이 존재합니다."),
    RUNNING_NOT_FOUND(HttpStatus.NOT_FOUND, "R2", "러닝 기록을 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
