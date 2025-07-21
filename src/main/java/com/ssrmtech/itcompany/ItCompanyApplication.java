package com.ssrmtech.itcompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableMongoAuditing
public class ItCompanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItCompanyApplication.class, args);
    }
    
    @Bean
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
}