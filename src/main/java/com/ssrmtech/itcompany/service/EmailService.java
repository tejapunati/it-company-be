package com.ssrmtech.itcompany.service;

import com.ssrmtech.itcompany.model.EmailLog;

public interface EmailService {
    void sendEmail(String to, String subject, String body, String type);
    EmailLog logEmail(String to, String from, String subject, String body, String type, String status);
}