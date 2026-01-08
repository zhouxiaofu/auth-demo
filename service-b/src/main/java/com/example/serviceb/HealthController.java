package com.example.serviceb;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health(Authentication authentication) {
        return "ok, user=" + authentication.getName();
    }

    @GetMapping("/public/hello")
    public String publicHello() {
        return "public ok";
    }
}
