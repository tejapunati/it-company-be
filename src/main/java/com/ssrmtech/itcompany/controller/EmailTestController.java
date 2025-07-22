package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.service.EmailService;
import com.ssrmtech.itcompany.util.EmailCollectionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug/email-test")
@RequiredArgsConstructor
public class EmailTestController {
    
    private final EmailService emailService;
    private final MongoTemplate mongoTemplate;
    private final EmailCollectionResolver collectionResolver;
    
    @PostMapping("/send")
    public ResponseEntity<?> testSendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam String type) {
        
        try {
            emailService.sendEmail(to, subject, body, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email sent and logged successfully");
            response.put("to", to);
            response.put("type", type);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to send email: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/collections")
    public ResponseEntity<?> checkCollections() {
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
    
    @PostMapping("/migrate-emails")
    public ResponseEntity<?> migrateEmails() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all emails from the default collection
            List<EmailLog> defaultEmails = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION);
            response.put("default_emails_found", defaultEmails.size());
            
            List<String> migrationResults = new ArrayList<>();
            int migratedCount = 0;
            
            // Process each email
            for (EmailLog email : defaultEmails) {
                // Skip emails without type or recipient
                if (email.getType() == null || email.getToEmail() == null) {
                    migrationResults.add("Skipped email ID " + email.getId() + ": missing type or recipient");
                    continue;
                }
                
                // Determine the correct collection
                String targetCollection = collectionResolver.resolveCollectionName(email.getType(), email.getToEmail());
                
                // Skip if it's already in the correct collection
                if (targetCollection.equals(EmailCollectionResolver.DEFAULT_COLLECTION)) {
                    migrationResults.add("Email ID " + email.getId() + " belongs in default collection");
                    continue;
                }
                
                // Save to the correct collection
                mongoTemplate.save(email, targetCollection);
                migratedCount++;
                migrationResults.add("Migrated email ID " + email.getId() + " to " + targetCollection);
            }
            
            response.put("migrated_count", migratedCount);
            response.put("migration_details", migrationResults);
            
            // Check the counts after migration
            response.put("user_emails_after", mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.USER_COLLECTION).size());
            response.put("admin_emails_after", mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.ADMIN_COLLECTION).size());
            response.put("parent_admin_emails_after", mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.PARENT_ADMIN_COLLECTION).size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to migrate emails: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/create-collections")
    public ResponseEntity<?> createCollections() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create collections if they don't exist
            if (!mongoTemplate.collectionExists(EmailCollectionResolver.USER_COLLECTION)) {
                mongoTemplate.createCollection(EmailCollectionResolver.USER_COLLECTION);
                response.put("user_collection_created", true);
            } else {
                response.put("user_collection_created", false);
                response.put("user_collection_exists", true);
            }
            
            if (!mongoTemplate.collectionExists(EmailCollectionResolver.ADMIN_COLLECTION)) {
                mongoTemplate.createCollection(EmailCollectionResolver.ADMIN_COLLECTION);
                response.put("admin_collection_created", true);
            } else {
                response.put("admin_collection_created", false);
                response.put("admin_collection_exists", true);
            }
            
            if (!mongoTemplate.collectionExists(EmailCollectionResolver.PARENT_ADMIN_COLLECTION)) {
                mongoTemplate.createCollection(EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
                response.put("parent_admin_collection_created", true);
            } else {
                response.put("parent_admin_collection_created", false);
                response.put("parent_admin_collection_exists", true);
            }
            
            response.put("status", "success");
            response.put("collections", mongoTemplate.getCollectionNames());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to create collections: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}