package com.ssrmtech.itcompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.model.Timesheet;
import com.ssrmtech.itcompany.service.EmailLogService;
import com.ssrmtech.itcompany.service.EmailService;
import com.ssrmtech.itcompany.service.TimesheetService;
import com.ssrmtech.itcompany.util.EmailCollectionResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableMongoAuditing
@RestController
public class ItCompanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItCompanyApplication.class, args);
    }
    
    @Bean
    @Order(1)
    public CommandLineRunner checkMongoConnection(MongoTemplate mongoTemplate) {
        return args -> {
            try {
                System.out.println("\n=== MongoDB Connection Check ===\n");
                System.out.println("MongoDB Connection Status: Connected");
                System.out.println("Database Name: " + mongoTemplate.getDb().getName());
                System.out.println("Available Collections: " + mongoTemplate.getCollectionNames());
                System.out.println("\n=== MongoDB Connection Successful ===\n");
            } catch (Exception e) {
                System.err.println("\n=== MongoDB Connection Failed ===\n");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                System.err.println("\n=== Check MongoDB Connection Settings ===\n");
            }
        };
    }
    
    @Bean
    @Order(2)
    public CommandLineRunner ensureCollections(MongoTemplate mongoTemplate) {
        return args -> {
            try {
                System.out.println("\n=== Ensuring Email Collections Exist ===\n");
                
                // Create collections if they don't exist
                if (!mongoTemplate.collectionExists(EmailCollectionResolver.USER_COLLECTION)) {
                    System.out.println("Creating collection: " + EmailCollectionResolver.USER_COLLECTION);
                    mongoTemplate.createCollection(EmailCollectionResolver.USER_COLLECTION);
                }
                
                if (!mongoTemplate.collectionExists(EmailCollectionResolver.ADMIN_COLLECTION)) {
                    System.out.println("Creating collection: " + EmailCollectionResolver.ADMIN_COLLECTION);
                    mongoTemplate.createCollection(EmailCollectionResolver.ADMIN_COLLECTION);
                }
                
                if (!mongoTemplate.collectionExists(EmailCollectionResolver.PARENT_ADMIN_COLLECTION)) {
                    System.out.println("Creating collection: " + EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
                    mongoTemplate.createCollection(EmailCollectionResolver.PARENT_ADMIN_COLLECTION);
                }
                
                System.out.println("Available collections: " + mongoTemplate.getCollectionNames());
                System.out.println("\n=== Email Collections Check Complete ===\n");
            } catch (Exception e) {
                System.err.println("\n=== Error Creating Email Collections ===\n");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
    
    @Bean
    @Order(3)
    public CommandLineRunner checkTimesheets(TimesheetService timesheetService) {
        return args -> {
            try {
                System.out.println("\n=== Checking Test Timesheets ===\n");
                
                List<Timesheet> pendingTimesheets = timesheetService.getTimesheetsByStatus("PENDING");
                List<Timesheet> allTimesheets = timesheetService.getAllTimesheets();
                
                for (Timesheet timesheet : pendingTimesheets) {
                    System.out.println("Found pending timesheet for user: " + timesheet.getUserId());
                }
                
                System.out.println("Total timesheets in database: " + allTimesheets.size());
                System.out.println("Pending timesheets in database: " + pendingTimesheets.size());
                
                System.out.println("\n=== Test Timesheets Check Complete ===\n");
            } catch (Exception e) {
                System.err.println("\n=== Error Checking Timesheets ===\n");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
    
    @Bean
    @Order(4)
    public CommandLineRunner checkEmailLogs(EmailLogService emailLogService, EmailService emailService) {
        return args -> {
            try {
                System.out.println("\n=== Checking Email Logs ===\n");
                
                List<EmailLog> allEmailLogs = emailLogService.getAllEmailLogs();
                List<EmailLog> userEmailLogs = emailLogService.getAllUserEmailLogs();
                List<EmailLog> adminEmailLogs = emailLogService.getAllAdminEmailLogs();
                List<EmailLog> parentAdminEmailLogs = emailLogService.getAllParentAdminEmailLogs();
                
                System.out.println("Total email logs in database: " + allEmailLogs.size());
                System.out.println("User email logs: " + userEmailLogs.size());
                System.out.println("Admin email logs: " + adminEmailLogs.size());
                System.out.println("Parent admin email logs: " + parentAdminEmailLogs.size());
                
                // Create test email logs if none exist
                if (allEmailLogs.isEmpty()) {
                    System.out.println("No email logs found. Creating test email logs...");
                    
                    // Create a user email log
                    emailService.sendEmail(
                        "user@ssrmtech.com",
                        "Test User Email",
                        "This is a test email for a user.",
                        "TIMESHEET_SUBMITTED"
                    );
                    
                    // Create an admin email log
                    emailService.sendEmail(
                        "admin@ssrmtech.com",
                        "Test Admin Email",
                        "This is a test email for an admin.",
                        "ADMIN_NOTIFICATION"
                    );
                    
                    // Create a parent admin email log
                    emailService.sendEmail(
                        "parent-admin@ssrmtech.com",
                        "Test Parent Admin Email",
                        "This is a test email for a parent admin.",
                        "ADMIN_NOTIFICATION"
                    );
                    
                    System.out.println("Test email logs created successfully.");
                }
                
                System.out.println("\n=== Email Logs Check Complete ===\n");
            } catch (Exception e) {
                System.err.println("\n=== Error Checking Email Logs ===\n");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
    
    @GetMapping("/app-health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "IT Company Backend is running");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}