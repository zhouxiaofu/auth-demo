package com.example.serviceauth;

public record AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresInSeconds) {
}
