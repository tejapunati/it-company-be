package com.ssrmtech.itcompany.config;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.service.EmailLogService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class EmailCopyRunner {

    @Bean
    @Order(5)
    public CommandLineRunner copyEmailsToDefaultCollection(EmailLogService emailLogService) {
        return args -> {
            try {
                System.out.println("\n=== Copying Emails to Default Collection ===\n");
                
                // Get counts before copying
                List<EmailLog> defaultLogsBefore = emailLogService.getAllEmailLogs();
                System.out.println("Default collection count before: " + defaultLogsBefore.size());
                
                // Copy emails to default collection
                int copiedCount = emailLogService.copyEmailsToDefaultCollection();
                
                // Get counts after copying
                List<EmailLog> defaultLogsAfter = emailLogService.getAllEmailLogs();
                System.out.println("Default collection count after: " + defaultLogsAfter.size());
                System.out.println("Copied " + copiedCount + " emails to default collection");
                
                System.out.println("\n=== Email Copy Complete ===\n");
            } catch (Exception e) {
                System.err.println("\n=== Error Copying Emails ===\n");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}