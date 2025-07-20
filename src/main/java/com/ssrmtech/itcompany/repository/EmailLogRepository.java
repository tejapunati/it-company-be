package com.ssrmtech.itcompany.repository;

import com.ssrmtech.itcompany.model.EmailLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmailLogRepository extends MongoRepository<EmailLog, String> {
    List<EmailLog> findByToEmail(String toEmail);
    List<EmailLog> findByType(String type);
    List<EmailLog> findByStatus(String status);
}