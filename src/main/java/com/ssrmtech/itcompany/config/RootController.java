package com.ssrmtech.itcompany.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class RootController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/mongo-status")
    public Map<String, Object> getMongoStatus() {
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