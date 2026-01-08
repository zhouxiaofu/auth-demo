package com.example.serviceauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final byte[] secretBytes;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-ttl}") Duration accessTokenTtl,
            @Value("${jwt.refresh-token-ttl}") Duration refreshTokenTtl
    ) {
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public String generateAccessToken(String subject) {
        return generateToken(subject, accessTokenTtl, Map.of("role", "user", "type", "access"));
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, refreshTokenTtl, Map.of("type", "refresh"));
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretBytes))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtl.getSeconds();
    }

    private String generateToken(String subject, Duration ttl, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .signWith(Keys.hmacShaKeyFor(secretBytes))
                .compact();
    }
}
