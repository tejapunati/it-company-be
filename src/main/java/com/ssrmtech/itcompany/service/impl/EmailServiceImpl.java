package com.ssrmtech.itcompany.service.impl;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.repository.EmailLogRepository;
import com.ssrmtech.itcompany.service.EmailService;
import com.ssrmtech.itcompany.util.EmailCollectionResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final EmailLogRepository emailLogRepository;
    private final JavaMailSender emailSender;
    private final MongoTemplate mongoTemplate;
    private final EmailCollectionResolver collectionResolver;
    
    private static final Logger logger = Logger.getLogger(EmailServiceImpl.class.getName());
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.email.default-from:noreply@ssrmtech.com}")
    private String defaultFromEmail;
    
    @Value("${app.email.sending.enabled:false}")
    private boolean emailSendingEnabled;
    
    @Override
    public void sendEmail(String to, String subject, String body, String type) {
        String actualFromEmail = fromEmail != null ? fromEmail : defaultFromEmail;
        
        try {
            // Only attempt to send if email sending is enabled
            if (emailSendingEnabled) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(actualFromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                
                emailSender.send(message);
                logger.info("Email sent to " + to + " with subject: " + subject);
                
                // Log successful email
                logEmail(to, actualFromEmail, subject, body, type, "SENT");
            } else {
                // Just log the email without sending
                logger.info("Email sending disabled. Would have sent to " + to + " with subject: " + subject);
                logEmail(to, actualFromEmail, subject, body, type, "LOGGED_ONLY");
            }
        } catch (Exception e) {
            // Log failed email
            logger.severe("Failed to send email to " + to + ": " + e.getMessage());
            logEmail(to, actualFromEmail, subject, body, type, "FAILED");
            throw e;
        }
    }
    
    @Override
    public EmailLog logEmail(String to, String from, String subject, String body, String type, String status) {
        EmailLog emailLog = new EmailLog();
        emailLog.setToEmail(to);
        emailLog.setFromEmail(from);
        emailLog.setSubject(subject);
        emailLog.setBody(body);
        emailLog.setType(type);
        emailLog.setStatus(status);
        emailLog.setSentDate(new Date());
        
        // Determine the appropriate collection based on email type and recipient
        String collectionName = collectionResolver.resolveCollectionName(type, to);
        System.out.println("DEBUG - Email type: " + type + ", recipient: " + to + ", selected collection: " + collectionName);
        logger.info("Saving email log to collection: " + collectionName);
        
        try {
            // Ensure the collection exists
            if (!mongoTemplate.collectionExists(collectionName)) {
                System.out.println("Collection does not exist, creating: " + collectionName);
                mongoTemplate.createCollection(collectionName);
            }
            
            // Save to the appropriate collection using MongoTemplate
            EmailLog saved = mongoTemplate.save(emailLog, collectionName);
            System.out.println("DEBUG - Successfully saved email log to collection: " + collectionName + ", ID: " + saved.getId());
            
            // Verify the email was saved
            EmailLog verified = mongoTemplate.findById(saved.getId(), EmailLog.class, collectionName);
            if (verified == null) {
                System.err.println("WARNING - Could not verify email was saved to collection: " + collectionName);
            }
            
            return saved;
        } catch (Exception e) {
            System.err.println("ERROR - Failed to save email log to collection: " + collectionName);
            e.printStackTrace();
            // Fallback to default collection
            System.out.println("DEBUG - Attempting to save to default collection: " + EmailCollectionResolver.DEFAULT_COLLECTION);
            return mongoTemplate.save(emailLog, EmailCollectionResolver.DEFAULT_COLLECTION);
        }
    }
}