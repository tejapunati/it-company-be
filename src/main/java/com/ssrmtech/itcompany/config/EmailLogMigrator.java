package com.ssrmtech.itcompany.config;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.util.EmailCollectionResolver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Configuration
public class EmailLogMigrator {

    @Bean
    @Order(5)
    public CommandLineRunner migrateEmailLogs(MongoTemplate mongoTemplate, EmailCollectionResolver collectionResolver) {
        return args -> {
            try {
                System.out.println("\n=== Migrating Email Logs ===\n");
                
                // Get all emails from the default collection
                List<EmailLog> defaultEmails = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.DEFAULT_COLLECTION);
                System.out.println("Found " + defaultEmails.size() + " emails in default collection");
                
                int migratedCount = 0;
                
                // Process each email
                for (EmailLog email : defaultEmails) {
                    // Determine the correct collection
                    String targetCollection = collectionResolver.resolveCollectionName(email.getType(), email.getToEmail());
                    
                    // Skip if it's already in the correct collection
                    if (targetCollection.equals(EmailCollectionResolver.DEFAULT_COLLECTION)) {
                        continue;
                    }
                    
                    // Save to the correct collection
                    mongoTemplate.save(email, targetCollection);
                    migratedCount++;
                    
                    // Remove from default collection (optional - uncomment if you want to remove)
                    // mongoTemplate.remove(email, EmailCollectionResolver.DEFAULT_COLLECTION);
                }
                
                System.out.println("Migrated " + migratedCount + " emails to specific collections");
                
                // Check the counts after migration
                List<EmailLog> userEmails = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.USER_COLLECTION);
                List<EmailLog> adminEmails = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.ADMIN_COLLECTION);
                List<EmailLog> parentAdminEmails = mongoTemplate.findAll(EmailLog.class, EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
                
                System.out.println("User emails: " + userEmails.size());
                System.out.println("Admin emails: " + adminEmails.size());
                System.out.println("Parent admin emails: " + parentAdminEmails.size());
                
                System.out.println("\n=== Email Migration Complete ===\n");
            } catch (Exception e) {
                System.err.println("\n=== Error Migrating Email Logs ===\n");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}