package com.ssrmtech.itcompany.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RestController
public class DirectMongoController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/direct-mongo-test")
    public Map<String, Object> mongoTestEndpoint() {
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