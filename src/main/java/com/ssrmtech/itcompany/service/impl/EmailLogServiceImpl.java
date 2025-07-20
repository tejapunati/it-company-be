package com.ssrmtech.itcompany.service.impl;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.repository.EmailLogRepository;
import com.ssrmtech.itcompany.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailLogServiceImpl implements EmailLogService {
    private final EmailLogRepository emailLogRepository;

    @Override
    public List<EmailLog> getAllEmailLogs() {
        return emailLogRepository.findAll();
    }

    @Override
    public EmailLog getEmailLogById(String id) {
        return emailLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email log not found with id: " + id));
    }

    @Override
    public List<EmailLog> getEmailLogsByToEmail(String email) {
        return emailLogRepository.findByToEmail(email);
    }

    @Override
    public EmailLog createEmailLog(EmailLog emailLog) {
        return emailLogRepository.save(emailLog);
    }
}