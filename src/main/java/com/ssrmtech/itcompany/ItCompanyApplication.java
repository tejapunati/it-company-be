package com.ssrmtech.itcompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class ItCompanyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItCompanyApplication.class, args);
    }
}