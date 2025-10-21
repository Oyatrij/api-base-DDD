package com.example.backend.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 전체에서 발생하는 예외의 코드와 메시지를 관리하는 Enum
 * - HttpStatus: HTTP 응답 상태 코드
 * - code: 클라이언트에게 전달할 에러 코드
 * - message: 에러 메시지
 */
@Getter
@AllArgsConstructor
public enum ExceptionCode {
    // 인증 관련 (401 Unauthorized)
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_003", "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_004", "토큰이 없습니다."),

    // 권한 관련 (403 Forbidden)
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_005", "접근 권한이 없습니다."),

    // 요청 데이터 관련 (400 Bad Request)
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "REQ_001", "잘못된 요청입니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "REQ_002", "필수 입력값이 누락되었습니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "REQ_003", "데이터 형식이 올바르지 않습니다."),

    // 리소스 관련 (404 Not Found)
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_001", "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_002", "사용자를 찾을 수 없습니다."),

    // 중복 관련 (409 Conflict)
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "RES_003", "이미 존재하는 리소스입니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "RES_004", "이미 존재하는 아이디입니다."),

    // 서버 에러 (500 Internal Server Error)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_001", "서버 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_002", "데이터베이스 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}