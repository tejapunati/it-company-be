package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/direct-emails")
@RequiredArgsConstructor
public class DirectEmailController {

    private final EmailLogService emailLogService;
    
    @GetMapping("/{email}")
    public ResponseEntity<List<EmailLog>> getEmailsByAddress(@PathVariable String email) {
        List<EmailLog> emails = emailLogService.getEmailLogsByToEmail(email);
        return ResponseEntity.ok(emails);
    }
}