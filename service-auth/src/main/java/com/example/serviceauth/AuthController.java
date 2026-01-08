package com.example.serviceauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request == null || request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required."));
        }
        String accessToken = jwtService.generateAccessToken(request.username());
        String refreshToken = jwtService.generateRefreshToken(request.username());
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Bearer", jwtService.getAccessTokenTtlSeconds()));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required."));
        }
        try {
            Claims claims = jwtService.parseToken(request.refreshToken());
            if (!"refresh".equals(claims.get("type"))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token."));
            }
            String accessToken = jwtService.generateAccessToken(claims.getSubject());
            String refreshToken = jwtService.generateRefreshToken(claims.getSubject());
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Bearer", jwtService.getAccessTokenTtlSeconds()));
        } catch (JwtException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token expired or invalid."));
        }
    }
}
