package com.example.servicea;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class DataController {
    @GetMapping("/data")
    public Map<String, Object> data() {
        return Map.of(
                "service", "service-a",
                "message", "Protected data from service A.",
                "timestamp", Instant.now().toString()
        );
    }
}
