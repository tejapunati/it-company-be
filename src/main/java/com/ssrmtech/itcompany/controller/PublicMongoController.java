package com.ssrmtech.itcompany.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PublicMongoController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/public/mongo-test")
    public ResponseEntity<?> testMongo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("status", "connected");
            response.put("database", mongoTemplate.getDb().getName());
            response.put("collections", mongoTemplate.getCollectionNames());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}