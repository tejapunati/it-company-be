package com.ssrmtech.itcompany.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "email_logs")
public class EmailLog {
    @Id
    private String id;
    
    private String toEmail;
    
    private String fromEmail;
    
    private String subject;
    
    private String body;
    
    private String type;
    
    private String status;
    
    private Date sentDate;
}