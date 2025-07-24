package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.Admin;
import com.ssrmtech.itcompany.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping
    @PreAuthorize("hasRole('PARENT_ADMIN')")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @PostMapping
    @PreAuthorize("hasRole('PARENT_ADMIN')")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.createAdmin(admin));
    }
    
    @PostMapping("/secure")
    @PreAuthorize("hasRole('PARENT_ADMIN')")
    public ResponseEntity<Admin> createAdminSecure(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.createAdmin(admin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARENT_ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok().build();
    }
}