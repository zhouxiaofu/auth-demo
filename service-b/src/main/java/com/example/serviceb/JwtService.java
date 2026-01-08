package com.example.serviceb;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class JwtService {
    private final byte[] secretBytes;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretBytes))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
