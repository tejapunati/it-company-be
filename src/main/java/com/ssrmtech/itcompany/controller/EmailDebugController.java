package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.service.EmailLogService;
import com.ssrmtech.itcompany.util.EmailCollectionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug/emails")
@RequiredArgsConstructor
public class EmailDebugController {

    private final MongoTemplate mongoTemplate;
    private final EmailLogService emailLogService;
    
    @GetMapping("/counts")
    public ResponseEntity<?> getEmailCounts() {
        Map<String, Object> response = new HashMap<>();
        
        // Get counts from each collection
        List<EmailLog> defaultLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION);
        List<EmailLog> userLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.USER_COLLECTION);
        List<EmailLog> adminLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.ADMIN_COLLECTION);
        List<EmailLog> parentAdminLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
        
        response.put("default_collection", EmailCollectionResolver.DEFAULT_COLLECTION);
        response.put("default_count", defaultLogs.size());
        response.put("default_emails", defaultLogs);
        
        response.put("user_collection", EmailCollectionResolver.USER_COLLECTION);
        response.put("user_count", userLogs.size());
        response.put("user_emails", userLogs);
        
        response.put("admin_collection", EmailCollectionResolver.ADMIN_COLLECTION);
        response.put("admin_count", adminLogs.size());
        response.put("admin_emails", adminLogs);
        
        response.put("parent_admin_collection", EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
        response.put("parent_admin_count", parentAdminLogs.size());
        response.put("parent_admin_emails", parentAdminLogs);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllEmails() {
        Map<String, Object> response = new HashMap<>();
        
        // Get all emails from all collections
        List<EmailLog> defaultLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION);
        List<EmailLog> userLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.USER_COLLECTION);
        List<EmailLog> adminLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.ADMIN_COLLECTION);
        List<EmailLog> parentAdminLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
        
        // Combine all emails
        response.put("all_emails", List.of(defaultLogs, userLogs, adminLogs, parentAdminLogs));
        response.put("total_count", defaultLogs.size() + userLogs.size() + adminLogs.size() + parentAdminLogs.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/copy-to-default")
    public ResponseEntity<?> copyToDefaultCollection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Copy emails to default collection
            int copiedCount = emailLogService.copyEmailsToDefaultCollection();
            
            response.put("status", "success");
            response.put("copied_count", copiedCount);
            
            // Get updated counts
            List<EmailLog> defaultLogs = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION);
            response.put("default_count", defaultLogs.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to copy emails: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}