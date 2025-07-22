package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test-email")
@RequiredArgsConstructor
public class TestEmailController {
    
    private final EmailService emailService;
    
    @PostMapping("/send")
    public ResponseEntity<?> testSendEmail(@RequestParam String to, 
                                          @RequestParam String subject, 
                                          @RequestParam String body) {
        try {
            emailService.sendEmail(to, subject, body, "TEST");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email sent and logged successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to send email: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/log-only")
    public ResponseEntity<?> testLogEmail(@RequestParam String to, 
                                         @RequestParam String from,
                                         @RequestParam String subject, 
                                         @RequestParam String body,
                                         @RequestParam String type,
                                         @RequestParam String status) {
        EmailLog emailLog = emailService.logEmail(to, from, subject, body, type, status);
        
        return ResponseEntity.ok(emailLog);
    }
}