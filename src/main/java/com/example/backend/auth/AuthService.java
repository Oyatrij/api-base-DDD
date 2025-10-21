package com.example.backend.auth;

import com.example.backend.auth.dto.AuthTokensDto;
import com.example.backend.infrastructure.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // 데모용 하드코딩 사용자(운영에서는 DB/IDP 연동 권장)
    private final String DEMO_USERNAME = "user";
    private final String DEMO_PASSWORD_HASH; // BCrypt 해시

    public AuthService(JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.DEMO_PASSWORD_HASH = passwordEncoder.encode("password");
    }

    public AuthTokensDto login(String username, String password) {
        if (username == null || password == null) return null;
        if (DEMO_USERNAME.equals(username) && passwordEncoder.matches(password, DEMO_PASSWORD_HASH)) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", new String[]{"ROLE_USER"});
            String access = jwtUtil.generateAccessToken(username, claims);
            String refresh = jwtUtil.generateRefreshToken(username);
            return AuthTokensDto.builder().accessToken(access).refreshToken(refresh).build();
        }
        return null;
    }

    public AuthTokensDto refresh(String refreshToken) {
        if (!jwtUtil.isRefreshToken(refreshToken)) return null;
        String subject = jwtUtil.getSubject(refreshToken);
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", new String[]{"ROLE_USER"}); // 실제 구현 시 사용자 저장소에서 조회 필요
        String newAccess = jwtUtil.generateAccessToken(subject, claims);
        String newRefresh = jwtUtil.generateRefreshToken(subject); // 단순 회전(rotate)
        return AuthTokensDto.builder().accessToken(newAccess).refreshToken(newRefresh).build();
    }
}