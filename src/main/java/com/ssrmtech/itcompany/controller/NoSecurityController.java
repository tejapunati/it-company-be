package com.ssrmtech.itcompany.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class NoSecurityController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/test-mongo")
    public Map<String, Object> testMongo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("status", "connected");
            response.put("database", mongoTemplate.getDb().getName());
            response.put("collections", mongoTemplate.getCollectionNames());
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return response;
    }
}