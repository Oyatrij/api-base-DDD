package com.example.backend.infrastructure.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 성공 응답 객체
 * 클라이언트에게 일관된 성공 응답 정보를 제공하기 위한 DTO
 * 
 * 응답 예시:
 * {
 *   "code": "SUCCESS",
 *   "message": "요청이 성공적으로 처리되었습니다.",
 *   "data": {...},
 *   "status": 200,
 *   "timestamp": "2024-12-19 15:30:45"
 * }
 */
@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private String code;                                        // 응답 코드 (SUCCESS, CREATED 등)
    private String message;                                     // 응답 메시지
    private T data;                                             // 실제 응답 데이터
    private int status;                                         // HTTP 상태 코드
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;                            // 응답 시간

    /**
     * 데이터 포함 성공 응답 생성
     */
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .status(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 데이터 포함 성공 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .code("SUCCESS")
                .message(message)
                .data(data)
                .status(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 생성(CREATED) 응답
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .code("CREATED")
                .message("리소스가 생성되었습니다.")
                .data(data)
                .status(201)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 데이터 없음 (204 No Content)
     */
    public static ApiResponse<Void> noContent() {
        return ApiResponse.<Void>builder()
                .code("NO_CONTENT")
                .message("응답 데이터가 없습니다.")
                .data(null)
                .status(204)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 커스텀 응답
     */
    public static <T> ApiResponse<T> custom(String code, String message, T data, int status) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
}