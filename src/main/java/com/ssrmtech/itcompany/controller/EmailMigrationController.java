package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.service.EmailLogService;
import com.ssrmtech.itcompany.util.EmailCollectionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email-migration")
@RequiredArgsConstructor
public class EmailMigrationController {

    private final EmailLogService emailLogService;
    private final MongoTemplate mongoTemplate;
    
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        Map<String, Object> response = new HashMap<>();
        
        // Check each collection
        List<EmailLog> defaultLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION);
        List<EmailLog> userLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.USER_COLLECTION);
        List<EmailLog> adminLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.ADMIN_COLLECTION);
        List<EmailLog> parentAdminLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
        
        response.put("default_collection", EmailCollectionResolver.DEFAULT_COLLECTION);
        response.put("default_count", defaultLogs.size());
        
        response.put("user_collection", EmailCollectionResolver.USER_COLLECTION);
        response.put("user_count", userLogs.size());
        
        response.put("admin_collection", EmailCollectionResolver.ADMIN_COLLECTION);
        response.put("admin_count", adminLogs.size());
        
        response.put("parent_admin_collection", EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
        response.put("parent_admin_count", parentAdminLogs.size());
        
        // Check if collections exist
        response.put("collections", mongoTemplate.getCollectionNames());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/run")
    public ResponseEntity<?> runMigration() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int migratedCount = emailLogService.migrateEmailLogs();
            
            response.put("status", "success");
            response.put("migrated_count", migratedCount);
            
            // Get updated counts
            response.put("default_count", mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION).size());
            response.put("user_count", mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.USER_COLLECTION).size());
            response.put("admin_count", mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.ADMIN_COLLECTION).size());
            response.put("parent_admin_count", mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.PARENT_ADMIN_COLLECTION).size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Migration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}