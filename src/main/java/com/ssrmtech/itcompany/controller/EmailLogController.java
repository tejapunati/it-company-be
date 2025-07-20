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

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<EmailLog> getEmailLogById(@PathVariable String id) {
        return ResponseEntity.ok(emailLogService.getEmailLogById(id));
    }

    @GetMapping("/user/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<List<EmailLog>> getEmailLogsByUserEmail(@PathVariable String email) {
        return ResponseEntity.ok(emailLogService.getEmailLogsByToEmail(email));
    }
}