package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.service.AdminService;
import com.ssrmtech.itcompany.service.TimesheetService;
import com.ssrmtech.itcompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    private final UserService userService;
    private final AdminService adminService;
    private final TimesheetService timesheetService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            int totalUsers = userService.getAllUsers().size();
            int totalAdmins = adminService.getAllAdmins().size();
            int totalTimesheets = timesheetService.getAllTimesheets().size();
            int pendingTimesheets = timesheetService.getTimesheetsByStatus("PENDING").size();
            
            stats.put("totalUsers", totalUsers);
            stats.put("totalAdmins", totalAdmins);
            stats.put("pendingApprovals", pendingTimesheets);
            stats.put("totalTimesheets", totalTimesheets);
            
            System.out.println("Stats calculated: " + stats);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error calculating stats: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> fallbackStats = new HashMap<>();
            fallbackStats.put("totalUsers", 0);
            fallbackStats.put("totalAdmins", 0);
            fallbackStats.put("pendingApprovals", 0);
            fallbackStats.put("totalTimesheets", 0);
            
            return ResponseEntity.ok(fallbackStats);
        }
    }
}