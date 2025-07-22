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
@RequestMapping("/email-copy")
@RequiredArgsConstructor
public class EmailCopyController {

    private final EmailLogService emailLogService;
    private final MongoTemplate mongoTemplate;
    
    @GetMapping("/to-default")
    public ResponseEntity<?> copyToDefaultCollection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get counts before copying
            List<EmailLog> defaultLogsBefore = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION);
            response.put("default_count_before", defaultLogsBefore.size());
            
            // Copy emails to default collection
            int copiedCount = emailLogService.copyEmailsToDefaultCollection();
            
            // Get counts after copying
            List<EmailLog> defaultLogsAfter = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION);
            
            response.put("status", "success");
            response.put("copied_count", copiedCount);
            response.put("default_count_after", defaultLogsAfter.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to copy emails: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getEmailStatus() {
        Map<String, Object> response = new HashMap<>();
        
        // Get counts from each collection
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
        
        return ResponseEntity.ok(response);
    }
}