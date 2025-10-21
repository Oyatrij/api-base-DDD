package com.example.backend.auth;

import com.example.backend.auth.dto.AuthTokensDto;
import com.example.backend.auth.dto.ReqAuthLoginDto;
import com.example.backend.auth.dto.ReqAuthRefreshDto;
import com.example.backend.infrastructure.exception.CustomException;
import com.example.backend.infrastructure.exception.ExceptionCode;
import com.example.backend.infrastructure.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokensDto>> login(@RequestBody ReqAuthLoginDto req) {
        log.info("Login attempt - username: {}", req.getUsername());
        var tokens = authService.login(req.getUsername(), req.getPassword());
        if (tokens == null) {
            log.warn("Login failed - invalid credentials for username: {}", req.getUsername());
            throw new CustomException(ExceptionCode.INVALID_CREDENTIALS);
        }
        log.info("Login successful - username: {}", req.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(tokens, "로그인에 성공했습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokensDto>> refresh(@RequestBody ReqAuthRefreshDto req) {
        if (req.getRefreshToken() == null || req.getRefreshToken().isBlank()) {
            log.warn("Refresh token is missing");
            throw new CustomException(ExceptionCode.TOKEN_NOT_FOUND);
        }
        var tokens = authService.refresh(req.getRefreshToken());
        if (tokens == null) {
            log.warn("Refresh failed - token expired");
            throw new CustomException(ExceptionCode.EXPIRED_TOKEN);
        }
        log.info("Token refreshed successfully");
        return ResponseEntity.ok(ApiResponse.ok(tokens, "토큰이 갱신되었습니다."));
    }
}
