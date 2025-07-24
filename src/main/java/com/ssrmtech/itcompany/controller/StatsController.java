package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.service.AdminService;
import com.ssrmtech.itcompany.service.TimesheetService;
import com.ssrmtech.itcompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    
    @GetMapping("/debug/timesheets")
    public ResponseEntity<Map<String, Object>> debugTimesheets() {
        Map<String, Object> debug = new HashMap<>();
        
        try {
            List<com.ssrmtech.itcompany.model.Timesheet> allTimesheets = timesheetService.getAllTimesheets();
            debug.put("totalTimesheets", allTimesheets.size());
            
            List<Map<String, Object>> timesheetDetails = new ArrayList<>();
            for (com.ssrmtech.itcompany.model.Timesheet ts : allTimesheets) {
                Map<String, Object> tsInfo = new HashMap<>();
                tsInfo.put("id", ts.getId());
                tsInfo.put("userId", ts.getUserId());
                tsInfo.put("status", ts.getStatus());
                tsInfo.put("totalHours", ts.getTotalHours());
                tsInfo.put("weekEnding", ts.getWeekEnding());
                timesheetDetails.add(tsInfo);
            }
            debug.put("timesheets", timesheetDetails);
            
        } catch (Exception e) {
            debug.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(debug);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String userId) {
        try {
            System.out.println("Calculating user stats for userId: " + userId);
            Map<String, Object> stats = new HashMap<>();
            
            // Get user's timesheets
            List<com.ssrmtech.itcompany.model.Timesheet> userTimesheets = timesheetService.getTimesheetsByUserId(userId);
            System.out.println("Found " + userTimesheets.size() + " timesheets for user " + userId);
            
            // Debug: Print all timesheets to see what's in the database
            List<com.ssrmtech.itcompany.model.Timesheet> allTimesheets = timesheetService.getAllTimesheets();
            System.out.println("Total timesheets in database: " + allTimesheets.size());
            for (com.ssrmtech.itcompany.model.Timesheet ts : allTimesheets) {
                System.out.println("Timesheet - ID: " + ts.getId() + ", UserID: " + ts.getUserId() + ", Status: " + ts.getStatus() + ", Hours: " + ts.getTotalHours());
            }
            
            // Check if user exists
            try {
                com.ssrmtech.itcompany.model.User user = userService.getUserById(userId);
                System.out.println("User found - ID: " + user.getId() + ", Name: " + user.getName() + ", Email: " + user.getEmail());
            } catch (Exception e) {
                System.err.println("User not found with ID: " + userId + ", Error: " + e.getMessage());
            }
            
            int totalSubmitted = userTimesheets.size();
            int approvedTimesheets = (int) userTimesheets.stream().filter(t -> "APPROVED".equals(t.getStatus())).count();
            int pendingTimesheets = (int) userTimesheets.stream().filter(t -> "PENDING".equals(t.getStatus())).count();
            int rejectedTimesheets = (int) userTimesheets.stream().filter(t -> "REJECTED".equals(t.getStatus())).count();
            
            System.out.println("Stats breakdown - Total: " + totalSubmitted + ", Approved: " + approvedTimesheets + ", Pending: " + pendingTimesheets + ", Rejected: " + rejectedTimesheets);
            
            // Calculate total hours from approved timesheets
            int totalApprovedHours = userTimesheets.stream()
                .filter(t -> "APPROVED".equals(t.getStatus()))
                .mapToInt(t -> t.getTotalHours() != null ? t.getTotalHours() : 0)
                .sum();
            
            // Calculate current week hours (sum of all timesheets for current week)
            int currentWeekHours = 0;
            if (!userTimesheets.isEmpty()) {
                // Get current week's date range
                java.time.LocalDate now = java.time.LocalDate.now();
                java.time.LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
                java.time.LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);
                
                // Sum hours from all timesheets in current week
                currentWeekHours = userTimesheets.stream()
                    .filter(t -> {
                        try {
                            java.time.LocalDate weekEnding = java.time.LocalDate.parse(t.getWeekEnding());
                            return !weekEnding.isBefore(startOfWeek) && !weekEnding.isAfter(endOfWeek);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .mapToInt(t -> t.getTotalHours() != null ? t.getTotalHours() : 0)
                    .sum();
            }
            
            stats.put("totalSubmittedTimesheets", totalSubmitted);
            stats.put("approvedTimesheets", approvedTimesheets);
            stats.put("pendingTimesheets", pendingTimesheets);
            stats.put("rejectedTimesheets", rejectedTimesheets);
            stats.put("totalApprovedHours", totalApprovedHours);
            stats.put("currentWeekHours", currentWeekHours);
            
            System.out.println("User stats calculated for " + userId + ": " + stats);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error calculating user stats: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> fallbackStats = new HashMap<>();
            fallbackStats.put("totalSubmittedTimesheets", 0);
            fallbackStats.put("approvedTimesheets", 0);
            fallbackStats.put("pendingTimesheets", 0);
            fallbackStats.put("rejectedTimesheets", 0);
            fallbackStats.put("totalApprovedHours", 0);
            fallbackStats.put("currentWeekHours", 0);
            
            return ResponseEntity.ok(fallbackStats);
        }
    }
}