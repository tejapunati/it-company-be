package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.Timesheet;
import com.ssrmtech.itcompany.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/timesheets")
@RequiredArgsConstructor
public class TimesheetController {
    private final TimesheetService timesheetService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<List<Timesheet>> getAllTimesheets(@RequestParam(required = false) String status) {
        System.out.println("TimesheetController: Getting all timesheets with status: " + status);
        List<Timesheet> timesheets;
        if (status != null) {
            timesheets = timesheetService.getTimesheetsByStatus(status);
            System.out.println("TimesheetController: Found " + timesheets.size() + " timesheets with status: " + status);
            for (Timesheet timesheet : timesheets) {
                System.out.println("  - Timesheet ID: " + timesheet.getId() + ", User ID: " + timesheet.getUserId() + ", Status: " + timesheet.getStatus());
            }
        } else {
            timesheets = timesheetService.getAllTimesheets();
            System.out.println("TimesheetController: Found " + timesheets.size() + " total timesheets");
            for (Timesheet timesheet : timesheets) {
                System.out.println("  - Timesheet ID: " + timesheet.getId() + ", User ID: " + timesheet.getUserId() + ", Status: " + timesheet.getStatus());
            }
        }
        return ResponseEntity.ok(timesheets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN') or @timesheetService.isOwner(#id, authentication.principal.id)")
    public ResponseEntity<Timesheet> getTimesheetById(@PathVariable String id) {
        return ResponseEntity.ok(timesheetService.getTimesheetById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<Timesheet>> getTimesheetsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(timesheetService.getTimesheetsByUserId(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<Timesheet> createTimesheet(@RequestBody Timesheet timesheet) {
        System.out.println("TimesheetController: Creating timesheet: " + timesheet);
        Timesheet createdTimesheet = timesheetService.createTimesheet(timesheet);
        System.out.println("TimesheetController: Created timesheet with ID: " + createdTimesheet.getId() + ", Status: " + createdTimesheet.getStatus());
        return ResponseEntity.ok(createdTimesheet);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@timesheetService.isOwner(#id, authentication.principal.id)")
    public ResponseEntity<Timesheet> updateTimesheet(@PathVariable String id, @RequestBody Timesheet timesheet) {
        timesheet.setId(id);
        return ResponseEntity.ok(timesheetService.updateTimesheet(timesheet));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<Timesheet> approveTimesheet(@PathVariable String id, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(timesheetService.approveTimesheet(id, request.get("comments")));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<Timesheet> rejectTimesheet(@PathVariable String id, @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(timesheetService.rejectTimesheet(id, request.get("comments")));
    }
}