package com.example.backend.infrastructure.exception;

import lombok.Getter;

/**
 * 애플리케이션에서 사용하는 커스텀 예외
 * ExceptionCode Enum을 사용하여 일관된 에러 정보를 관리
 */
@Getter
public class CustomException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public CustomException(ExceptionCode exceptionCode, String additionalMessage) {
        super(exceptionCode.getMessage() + " - " + additionalMessage);
        this.exceptionCode = exceptionCode;
    }
}