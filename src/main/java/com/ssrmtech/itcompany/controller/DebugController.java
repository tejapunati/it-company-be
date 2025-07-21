package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.Timesheet;
import com.ssrmtech.itcompany.repository.TimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private TimesheetRepository timesheetRepository;

    @GetMapping("/timesheet-count")
    public Map<String, Object> getTimesheetCount() {
        Map<String, Object> response = new HashMap<>();
        long count = timesheetRepository.count();
        response.put("count", count);
        response.put("message", "Found " + count + " timesheets in the database");
        
        if (count > 0) {
            response.put("timesheets", timesheetRepository.findAll());
        }
        
        return response;
    }
    
    @GetMapping("/pending-timesheets")
    public Map<String, Object> getPendingTimesheets() {
        Map<String, Object> response = new HashMap<>();
        List<Timesheet> pendingTimesheets = timesheetRepository.findByStatus("PENDING");
        response.put("count", pendingTimesheets.size());
        response.put("message", "Found " + pendingTimesheets.size() + " pending timesheets in the database");
        response.put("timesheets", pendingTimesheets);
        
        System.out.println("Debug: Found " + pendingTimesheets.size() + " pending timesheets");
        for (Timesheet timesheet : pendingTimesheets) {
            System.out.println("  - Timesheet ID: " + timesheet.getId() + ", User ID: " + timesheet.getUserId() + ", Status: " + timesheet.getStatus());
        }
        
        return response;
    }
    
    @GetMapping("/all-timesheets")
    public Map<String, Object> getAllTimesheets() {
        Map<String, Object> response = new HashMap<>();
        List<Timesheet> allTimesheets = timesheetRepository.findAll();
        response.put("count", allTimesheets.size());
        response.put("message", "Found " + allTimesheets.size() + " total timesheets in the database");
        response.put("timesheets", allTimesheets);
        
        System.out.println("Debug: Found " + allTimesheets.size() + " total timesheets");
        for (Timesheet timesheet : allTimesheets) {
            System.out.println("  - Timesheet ID: " + timesheet.getId() + ", User ID: " + timesheet.getUserId() + ", Status: " + timesheet.getStatus());
        }
        
        return response;
    }
}