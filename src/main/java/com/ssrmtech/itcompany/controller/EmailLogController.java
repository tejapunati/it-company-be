package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/email-logs")
@RequiredArgsConstructor
public class EmailLogController {
    private final EmailLogService emailLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<List<EmailLog>> getAllEmailLogs() {
        return ResponseEntity.ok(emailLogService.getAllEmailLogs());
    }
    
    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<List<EmailLog>> getAllUserEmailLogs() {
        return ResponseEntity.ok(emailLogService.getAllUserEmailLogs());
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<List<EmailLog>> getAllAdminEmailLogs() {
        return ResponseEntity.ok(emailLogService.getAllAdminEmailLogs());
    }
    
    @GetMapping("/parent-admin")
    @PreAuthorize("hasRole('PARENT_ADMIN')")
    public ResponseEntity<List<EmailLog>> getAllParentAdminEmailLogs() {
        return ResponseEntity.ok(emailLogService.getAllParentAdminEmailLogs());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<EmailLog> getEmailLogById(
            @PathVariable String id,
            @RequestParam(required = false) String collection) {
        return ResponseEntity.ok(emailLogService.getEmailLogById(id, collection));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<EmailLog>> getUserEmailLogs(@PathVariable String email) {
        return ResponseEntity.ok(emailLogService.getUserEmailLogsByEmail(email));
    }
    
    @GetMapping("/admin/{email}")
    public ResponseEntity<List<EmailLog>> getAdminEmailLogs(@PathVariable String email) {
        return ResponseEntity.ok(emailLogService.getAdminEmailLogsByEmail(email));
    }
    
    @GetMapping("/parent-admin/{email}")
    public ResponseEntity<List<EmailLog>> getParentAdminEmailLogs(@PathVariable String email) {
        return ResponseEntity.ok(emailLogService.getParentAdminEmailLogsByEmail(email));
    }
}