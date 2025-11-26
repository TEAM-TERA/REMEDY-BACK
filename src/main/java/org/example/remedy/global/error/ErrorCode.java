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
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U5", "올바르지 않은 비밀번호입니다."),
    USER_WITHDRAWN(HttpStatus.FORBIDDEN, "U6", "탈퇴한 회원입니다."),
	OAUTH2_USER_CANNOT_USE_PASSWORD_LOGIN(HttpStatus.BAD_REQUEST, "U7", "소셜 로그인으로 가입한 계정입니다. 해당 소셜 로그인을 이용해주세요."),
	EMAIL_ALREADY_EXISTS_WITH_OAUTH2(HttpStatus.CONFLICT, "U8", "이미 소셜 로그인으로 가입된 이메일입니다. 해당 소셜 로그인을 이용해주세요."),

    DROPPING_ALREADY_EXISTS(HttpStatus.CONFLICT, "D1", "반경 1미터 이내에 이미 드랍이 존재합니다."),
    DROPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "D2", "삭제되었거나 존재하지 않는 드랍입니다."),
    SONG_NOT_FOUND(HttpStatus.NOT_FOUND, "D3", "존재하지 않는 노래입니다."),

    INVALID_DROPPING_DELETE_REQUEST(HttpStatus.BAD_REQUEST, "D5", "다른 사용자가 생성한 드랍핑은 삭제할 수 없습니다."),
    INVALID_VOTE_OPTION(HttpStatus.BAD_REQUEST, "D6", "존재하지 않는 투표 옵션입니다."),
    INVALID_DROPPING_TYPE(HttpStatus.BAD_REQUEST, "D7", "투표 드랍이 아닙니다."),
    EMPTY_VOTE_OPTIONS(HttpStatus.BAD_REQUEST, "D8", "투표 옵션이 비어있습니다."),
    EMPTY_PLAYLIST_SONGS(HttpStatus.BAD_REQUEST, "D9", "플레이리스트 드랍의 곡 목록이 비어있습니다."),
	UNAUTHORIZED_PLAYLIST_ACCESS(HttpStatus.FORBIDDEN, "D10", "다른 사용자의 플레이리스트는 드랍할 수 없습니다."),


	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "S1", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "S2", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3", "서버 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "S4", "존재하지 않는 엔티티입니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "S5", "이미 존재하는 엔티티입니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C1", "존재하지 않는 댓글입니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C2", "본인 댓글만 수정/삭제할 수 있습니다."),

    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F1", "파일 업로드에 실패했습니다."),

    PLAYLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "P1", "존재하지 않는 플레이리스트입니다."),
    PLAYLIST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "P2", "본인 플레이리스트만 수정/삭제할 수 있습니다."),
    SONG_ALREADY_IN_PLAYLIST(HttpStatus.CONFLICT, "P3", "이미 플레이리스트에 존재하는 곡입니다."),
    SONG_NOT_IN_PLAYLIST(HttpStatus.NOT_FOUND, "P4", "플레이리스트에 존재하지 않는 곡입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
