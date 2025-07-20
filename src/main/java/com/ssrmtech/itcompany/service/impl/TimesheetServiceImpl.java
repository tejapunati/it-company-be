package com.ssrmtech.itcompany.service.impl;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.model.Timesheet;
import com.ssrmtech.itcompany.model.User;
import com.ssrmtech.itcompany.repository.TimesheetRepository;
import com.ssrmtech.itcompany.service.EmailLogService;
import com.ssrmtech.itcompany.service.TimesheetService;
import com.ssrmtech.itcompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements TimesheetService {
    private final TimesheetRepository timesheetRepository;
    private final UserService userService;
    private final EmailLogService emailLogService;

    @Override
    public List<Timesheet> getAllTimesheets() {
        return timesheetRepository.findAll();
    }

    @Override
    public Timesheet getTimesheetById(String id) {
        return timesheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + id));
    }

    @Override
    public List<Timesheet> getTimesheetsByUserId(String userId) {
        return timesheetRepository.findByUserId(userId);
    }

    @Override
    public List<Timesheet> getTimesheetsByStatus(String status) {
        return timesheetRepository.findByStatus(status);
    }

    @Override
    public Timesheet createTimesheet(Timesheet timesheet) {
        // Set current user ID if not provided
        if (timesheet.getUserId() == null) {
            String currentUserId = getCurrentUserId();
            timesheet.setUserId(currentUserId);
        }
        
        // Calculate total hours if not provided
        if (timesheet.getTotalHours() == null && timesheet.getHours() != null) {
            int totalHours = timesheet.getHours().values().stream().mapToInt(Integer::intValue).sum();
            timesheet.setTotalHours(totalHours);
        }
        
        // Set default values
        timesheet.setStatus("PENDING");
        timesheet.setSubmittedDate(new Date());
        
        return timesheetRepository.save(timesheet);
    }

    @Override
    public Timesheet updateTimesheet(Timesheet timesheet) {
        Timesheet existingTimesheet = getTimesheetById(timesheet.getId());
        
        // Only allow updates if status is PENDING
        if (!"PENDING".equals(existingTimesheet.getStatus())) {
            throw new RuntimeException("Cannot update timesheet that is not in PENDING status");
        }
        
        // Update fields
        existingTimesheet.setHours(timesheet.getHours());
        
        // Recalculate total hours
        int totalHours = timesheet.getHours().values().stream().mapToInt(Integer::intValue).sum();
        existingTimesheet.setTotalHours(totalHours);
        
        return timesheetRepository.save(existingTimesheet);
    }

    @Override
    public Timesheet approveTimesheet(String id, String comments) {
        Timesheet timesheet = getTimesheetById(id);
        timesheet.setStatus("APPROVED");
        timesheet.setReviewedDate(new Date());
        timesheet.setReviewedBy(getCurrentUserId());
        timesheet.setComments(comments);
        
        Timesheet savedTimesheet = timesheetRepository.save(timesheet);
        
        // Send approval email
        sendApprovalEmail(savedTimesheet);
        
        return savedTimesheet;
    }

    @Override
    public Timesheet rejectTimesheet(String id, String comments) {
        Timesheet timesheet = getTimesheetById(id);
        timesheet.setStatus("REJECTED");
        timesheet.setReviewedDate(new Date());
        timesheet.setReviewedBy(getCurrentUserId());
        timesheet.setComments(comments);
        
        Timesheet savedTimesheet = timesheetRepository.save(timesheet);
        
        // Send rejection email
        sendRejectionEmail(savedTimesheet);
        
        return savedTimesheet;
    }

    @Override
    public boolean isOwner(String timesheetId, String userId) {
        Timesheet timesheet = getTimesheetById(timesheetId);
        return timesheet.getUserId().equals(userId);
    }
    
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
    private void sendApprovalEmail(Timesheet timesheet) {
        User user = userService.getUserById(timesheet.getUserId());
        
        EmailLog emailLog = new EmailLog();
        emailLog.setToEmail(user.getEmail());
        emailLog.setFromEmail("noreply@ssrmtech.com");
        emailLog.setSubject("Timesheet Approved");
        emailLog.setBody("Your timesheet for the week ending " + timesheet.getWeekEnding() + " has been approved.");
        emailLog.setType("TIMESHEET_APPROVED");
        emailLog.setStatus("SENT");
        emailLog.setSentDate(new Date());
        
        emailLogService.createEmailLog(emailLog);
    }
    
    private void sendRejectionEmail(Timesheet timesheet) {
        User user = userService.getUserById(timesheet.getUserId());
        
        EmailLog emailLog = new EmailLog();
        emailLog.setToEmail(user.getEmail());
        emailLog.setFromEmail("noreply@ssrmtech.com");
        emailLog.setSubject("Timesheet Rejected");
        emailLog.setBody("Your timesheet for the week ending " + timesheet.getWeekEnding() + 
                " has been rejected. Reason: " + timesheet.getComments());
        emailLog.setType("TIMESHEET_REJECTED");
        emailLog.setStatus("SENT");
        emailLog.setSentDate(new Date());
        
        emailLogService.createEmailLog(emailLog);
    }
}