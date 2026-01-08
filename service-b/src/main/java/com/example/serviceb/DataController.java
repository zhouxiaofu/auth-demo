package com.example.serviceb;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class DataController {
    @GetMapping("/data")
    public Map<String, Object> data() {
        return Map.of(
                "service", "service-b",
                "message", "Protected data from service B.",
                "timestamp", Instant.now().toString()
        );
    }
}
