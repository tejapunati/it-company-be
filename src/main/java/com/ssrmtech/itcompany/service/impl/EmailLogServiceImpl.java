package com.ssrmtech.itcompany.service.impl;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.repository.EmailLogRepository;
import com.ssrmtech.itcompany.service.EmailLogService;
import com.ssrmtech.itcompany.util.EmailCollectionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailLogServiceImpl implements EmailLogService {
    private final MongoTemplate mongoTemplate;
    private final EmailCollectionResolver collectionResolver;

    @Override
    public List<EmailLog> getAllEmailLogs() {
        List<EmailLog> allLogs = new ArrayList<>();
        allLogs.addAll(mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION));
        allLogs.addAll(getAllUserEmailLogs());
        allLogs.addAll(getAllAdminEmailLogs());
        allLogs.addAll(getAllParentAdminEmailLogs());
        return allLogs;
    }
    
    @Override
    public List<EmailLog> getAllUserEmailLogs() {
        return mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.USER_COLLECTION);
    }
    
    @Override
    public List<EmailLog> getAllAdminEmailLogs() {
        return mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.ADMIN_COLLECTION);
    }
    
    @Override
    public List<EmailLog> getAllParentAdminEmailLogs() {
        return mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
    }

    @Override
    public EmailLog getEmailLogById(String id, String collectionName) {
        if (collectionName == null) {
            collectionName = EmailCollectionResolver.DEFAULT_COLLECTION;
        }
        
        EmailLog emailLog = mongoTemplate.findById(id, EmailLog.class, collectionName);
        if (emailLog == null) {
            throw new RuntimeException("Email log not found with id: " + id + " in collection: " + collectionName);
        }
        return emailLog;
    }

    @Override
    public List<EmailLog> getEmailLogsByToEmail(String email) {
        List<EmailLog> allLogs = new ArrayList<>();
        
        // Search in all collections
        Query query = new Query(Criteria.where("toEmail").is(email));
        
        allLogs.addAll(mongoTemplate.find(query, EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION));
        allLogs.addAll(mongoTemplate.find(query, EmailLog.class, EmailCollectionResolver.USER_COLLECTION));
        allLogs.addAll(mongoTemplate.find(query, EmailLog.class, EmailCollectionResolver.ADMIN_COLLECTION));
        allLogs.addAll(mongoTemplate.find(query, EmailLog.class, EmailCollectionResolver.PARENT_ADMIN_COLLECTION));
        
        return allLogs;
    }

    @Override
    public EmailLog createEmailLog(EmailLog emailLog) {
        String collectionName = collectionResolver.resolveCollectionName(emailLog.getType(), emailLog.getToEmail());
        return mongoTemplate.save(emailLog, collectionName);
    }
    
    @Override
    public int migrateEmailLogs() {
        System.out.println("Starting email log migration...");
        int migratedCount = 0;
        
        // Get all emails from the default collection
        List<EmailLog> defaultEmails = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION);
        System.out.println("Found " + defaultEmails.size() + " emails in default collection");
        
        // Process each email
        for (EmailLog email : defaultEmails) {
            try {
                // Skip emails without type or recipient
                if (email.getType() == null || email.getToEmail() == null) {
                    System.out.println("Skipping email ID " + email.getId() + ": missing type or recipient");
                    continue;
                }
                
                // Determine the correct collection
                String targetCollection = collectionResolver.resolveCollectionName(email.getType(), email.getToEmail());
                
                // Skip if it's already in the correct collection
                if (targetCollection.equals(EmailCollectionResolver.DEFAULT_COLLECTION)) {
                    System.out.println("Email ID " + email.getId() + " belongs in default collection");
                    continue;
                }
                
                // Ensure the collection exists
                if (!mongoTemplate.collectionExists(targetCollection)) {
                    System.out.println("Creating collection: " + targetCollection);
                    mongoTemplate.createCollection(targetCollection);
                }
                
                // Save to the correct collection
                mongoTemplate.save(email, targetCollection);
                migratedCount++;
                System.out.println("Migrated email ID " + email.getId() + " to " + targetCollection);
            } catch (Exception e) {
                System.err.println("Error migrating email ID " + email.getId() + ": " + e.getMessage());
            }
        }
        
        System.out.println("Migration complete. Migrated " + migratedCount + " emails.");
        return migratedCount;
    }
    
    @Override
    public int copyEmailsToDefaultCollection() {
        System.out.println("Copying emails to default collection for backward compatibility...");
        int copiedCount = 0;
        
        // Get emails from specific collections
        List<EmailLog> userEmails = getAllUserEmailLogs();
        List<EmailLog> adminEmails = getAllAdminEmailLogs();
        List<EmailLog> parentAdminEmails = getAllParentAdminEmailLogs();
        
        System.out.println("Found " + userEmails.size() + " user emails, " + 
                           adminEmails.size() + " admin emails, " + 
                           parentAdminEmails.size() + " parent admin emails");
        
        // Copy user emails to default collection
        for (EmailLog email : userEmails) {
            try {
                mongoTemplate.save(email, EmailCollectionResolver.DEFAULT_COLLECTION);
                copiedCount++;
            } catch (Exception e) {
                System.err.println("Error copying user email: " + e.getMessage());
            }
        }
        
        // Copy admin emails to default collection
        for (EmailLog email : adminEmails) {
            try {
                mongoTemplate.save(email, EmailCollectionResolver.DEFAULT_COLLECTION);
                copiedCount++;
            } catch (Exception e) {
                System.err.println("Error copying admin email: " + e.getMessage());
            }
        }
        
        // Copy parent admin emails to default collection
        for (EmailLog email : parentAdminEmails) {
            try {
                mongoTemplate.save(email, EmailCollectionResolver.DEFAULT_COLLECTION);
                copiedCount++;
            } catch (Exception e) {
                System.err.println("Error copying parent admin email: " + e.getMessage());
            }
        }
        
        System.out.println("Copied " + copiedCount + " emails to default collection");
        return copiedCount;
    }
}