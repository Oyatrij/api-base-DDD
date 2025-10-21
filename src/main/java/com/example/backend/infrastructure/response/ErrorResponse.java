package com.example.backend.infrastructure.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 에러 응답 객체
 * 클라이언트에게 일관된 에러 정보를 제공하기 위한 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String code;          // 에러 코드 (예: AUTH_001)
    private String message;       // 에러 메시지
    private int status;           // HTTP 상태 코드
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}