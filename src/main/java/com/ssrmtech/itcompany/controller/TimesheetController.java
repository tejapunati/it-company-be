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
        if (status != null) {
            return ResponseEntity.ok(timesheetService.getTimesheetsByStatus(status));
        }
        return ResponseEntity.ok(timesheetService.getAllTimesheets());
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
        return ResponseEntity.ok(timesheetService.createTimesheet(timesheet));
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