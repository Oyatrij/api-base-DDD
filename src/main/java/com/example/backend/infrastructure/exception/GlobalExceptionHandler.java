package com.example.backend.infrastructure.exception;

import com.example.backend.infrastructure.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 애플리케이션 전체의 예외를 처리하는 글로벌 핸들러
 * @ControllerAdvice를 사용하여 모든 @Controller에서 발생하는 예외를 중앙에서 처리
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     * 비즈니스 로직에서 의도적으로 던지는 예외
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ExceptionCode exceptionCode = ex.getExceptionCode();
        
        log.warn("Custom Exception: code={}, message={}", 
                exceptionCode.getCode(), exceptionCode.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(exceptionCode.getCode())
                .message(exceptionCode.getMessage())
                .status(exceptionCode.getHttpStatus().value())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        return ResponseEntity
                .status(exceptionCode.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * Validation 예외 처리
     * @Valid 또는 @Validated로 검증할 때 실패시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("유효하지 않은 요청입니다");
        
        log.warn("Validation Exception: {}", message);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ExceptionCode.INVALID_REQUEST.getCode())
                .message(ExceptionCode.INVALID_REQUEST.getMessage() + " - " + message)
                .status(ExceptionCode.INVALID_REQUEST.getHttpStatus().value())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        return ResponseEntity
                .status(ExceptionCode.INVALID_REQUEST.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * 모든 예기치 않은 예외 처리
     * 위의 핸들러에서 처리하지 못한 모든 예외를 여기서 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unexpected Exception: ", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ExceptionCode.INTERNAL_SERVER_ERROR.getCode())
                .message(ExceptionCode.INTERNAL_SERVER_ERROR.getMessage())
                .status(ExceptionCode.INTERNAL_SERVER_ERROR.getHttpStatus().value())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        return ResponseEntity
                .status(ExceptionCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(errorResponse);
    }
}