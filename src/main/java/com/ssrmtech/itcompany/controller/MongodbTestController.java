package com.ssrmtech.itcompany.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mongodb-test")
public class MongodbTestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("/connection")
    public ResponseEntity<?> testConnection(@RequestBody Map<String, String> request) {
        try {
            // We're already connected if this code runs
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Connection successful");
            response.put("database", mongoTemplate.getDb().getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Connection failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/collections")
    public ResponseEntity<?> getCollections() {
        try {
            return ResponseEntity.ok(mongoTemplate.getCollectionNames());
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to get collections: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}