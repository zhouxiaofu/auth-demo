package com.example.serviceb;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/health")) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing bearer token.");
            return;
        }
        String token = header.substring("Bearer ".length());
        try {
            Claims claims = jwtService.parseToken(token);
            String role = claims.get("role", String.class);
            if (!"user".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Insufficient role.");
                return;
            }
        } catch (JwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired or invalid.");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
