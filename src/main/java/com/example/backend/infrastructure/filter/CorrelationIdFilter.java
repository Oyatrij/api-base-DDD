package com.example.backend.infrastructure.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 모든 요청마다 Correlation ID를 생성/전달하는 필터
 * 요청 추적 및 로그 분석에 사용
 * 
 * MDC(Mapped Diagnostic Context)를 사용하여 로그에 correlation ID를 자동으로 포함시킴
 */
@Slf4j
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                     FilterChain filterChain) throws ServletException, IOException {
        
        // 1. 요청 헤더에서 Correlation ID 확인, 없으면 생성
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // 2. MDC에 correlation ID 저장 (로그에 자동으로 포함됨)
        org.slf4j.MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        
        // 3. 응답 헤더에 correlation ID 추가
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        try {
            log.info("Request started - Method: {}, URI: {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            log.info("Request completed - Status: {}", response.getStatus());
        } finally {
            // 4. 요청 처리 후 MDC 정리
            org.slf4j.MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}