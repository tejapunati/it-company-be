package com.ssrmtech.itcompany.service;

import com.ssrmtech.itcompany.model.EmailLog;

import java.util.List;

public interface EmailLogService {
    List<EmailLog> getAllEmailLogs();
    List<EmailLog> getAllUserEmailLogs();
    List<EmailLog> getAllAdminEmailLogs();
    List<EmailLog> getAllParentAdminEmailLogs();
    EmailLog getEmailLogById(String id, String collectionName);
    List<EmailLog> getEmailLogsByToEmail(String email);
    List<EmailLog> getUserEmailLogsByEmail(String email);
    List<EmailLog> getAdminEmailLogsByEmail(String email);
    List<EmailLog> getParentAdminEmailLogsByEmail(String email);
    EmailLog createEmailLog(EmailLog emailLog);
    int migrateEmailLogs();
    int copyEmailsToDefaultCollection();
}