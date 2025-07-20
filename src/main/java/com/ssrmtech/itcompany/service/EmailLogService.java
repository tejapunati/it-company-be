package com.ssrmtech.itcompany.service;

import com.ssrmtech.itcompany.model.EmailLog;

import java.util.List;

public interface EmailLogService {
    List<EmailLog> getAllEmailLogs();
    EmailLog getEmailLogById(String id);
    List<EmailLog> getEmailLogsByToEmail(String email);
    EmailLog createEmailLog(EmailLog emailLog);
}