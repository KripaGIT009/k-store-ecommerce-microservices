package com.kstore.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "K-Store User Service is running! ✅";
    }

    @GetMapping("/")
    public String welcome() {
        return "Welcome to K-Store User Service! 🛒";
    }
}
