package com.ssrmtech.itcompany.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<?> testConnection() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Backend is connected and working");
        return ResponseEntity.ok(response);
    }
}