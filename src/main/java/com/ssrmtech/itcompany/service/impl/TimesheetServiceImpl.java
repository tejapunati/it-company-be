package com.ssrmtech.itcompany.service.impl;

import com.ssrmtech.itcompany.dto.TimesheetWithUserDTO;
import com.ssrmtech.itcompany.model.Admin;
import com.ssrmtech.itcompany.model.ParentAdmin;
import com.ssrmtech.itcompany.model.Timesheet;
import com.ssrmtech.itcompany.model.User;
import com.ssrmtech.itcompany.repository.TimesheetRepository;
import com.ssrmtech.itcompany.service.AdminService;
import com.ssrmtech.itcompany.service.EmailLogService;
import com.ssrmtech.itcompany.service.EmailService;
import com.ssrmtech.itcompany.service.ParentAdminService;
import com.ssrmtech.itcompany.service.TimesheetService;
import com.ssrmtech.itcompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements TimesheetService {
    private final TimesheetRepository timesheetRepository;
    private final UserService userService;
    private final AdminService adminService;
    private final ParentAdminService parentAdminService;
    private final EmailService emailService;

    @Override
    public List<Timesheet> getAllTimesheets() {
        List<Timesheet> timesheets = timesheetRepository.findAll();
        System.out.println("TimesheetService: Found " + timesheets.size() + " timesheets in the database");
        return timesheets;
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
        System.out.println("TimesheetService: Getting timesheets by status: " + status);
        List<Timesheet> timesheets = timesheetRepository.findByStatus(status);
        System.out.println("TimesheetService: Found " + timesheets.size() + " timesheets with status: " + status);
        for (Timesheet timesheet : timesheets) {
            System.out.println("  - Timesheet ID: " + timesheet.getId() + ", User ID: " + timesheet.getUserId() + ", Status: " + timesheet.getStatus());
        }
        return timesheets;
    }
    
    @Override
    public List<TimesheetWithUserDTO> getTimesheetsWithUserDetails() {
        List<Timesheet> timesheets = getAllTimesheets();
        return convertToTimesheetWithUserDTO(timesheets);
    }
    
    @Override
    public List<TimesheetWithUserDTO> getTimesheetsWithUserDetailsByStatus(String status) {
        List<Timesheet> timesheets = getTimesheetsByStatus(status);
        return convertToTimesheetWithUserDTO(timesheets);
    }
    
    private List<TimesheetWithUserDTO> convertToTimesheetWithUserDTO(List<Timesheet> timesheets) {
        List<TimesheetWithUserDTO> result = new ArrayList<>();
        for (Timesheet timesheet : timesheets) {
            try {
                User user = userService.getUserById(timesheet.getUserId());
                String userName = user.getName() != null ? user.getName() : "Unknown User";
                String userEmail = user.getEmail() != null ? user.getEmail() : "unknown@email.com";
                result.add(TimesheetWithUserDTO.fromTimesheet(timesheet, userName, userEmail));
            } catch (Exception e) {
                System.err.println("Error getting user details for timesheet " + timesheet.getId() + ": " + e.getMessage());
                result.add(TimesheetWithUserDTO.fromTimesheet(timesheet, "Unknown User", "unknown@email.com"));
            }
        }
        return result;
    }

    @Override
    public Timesheet createTimesheet(Timesheet timesheet) {
        System.out.println("TimesheetService: Creating timesheet: " + timesheet);
        
        // Set current user ID if not provided
        if (timesheet.getUserId() == null) {
            String currentUserId = getCurrentUserId();
            timesheet.setUserId(currentUserId);
            System.out.println("TimesheetService: Set user ID to: " + currentUserId);
        } else {
            System.out.println("TimesheetService: Using provided user ID: " + timesheet.getUserId());
        }
        
        // Calculate total hours if not provided
        if (timesheet.getTotalHours() == null && timesheet.getHours() != null) {
            int totalHours = timesheet.getHours().values().stream().mapToInt(Integer::intValue).sum();
            timesheet.setTotalHours(totalHours);
            System.out.println("TimesheetService: Calculated total hours: " + totalHours);
        }
        
        // Set default values
        timesheet.setStatus("PENDING");
        timesheet.setSubmittedDate(new Date());
        
        System.out.println("TimesheetService: About to save timesheet with status: " + timesheet.getStatus());
        Timesheet savedTimesheet = timesheetRepository.save(timesheet);
        System.out.println("TimesheetService: Saved timesheet with ID: " + savedTimesheet.getId() + ", Status: " + savedTimesheet.getStatus());
        
        // Verify the timesheet was saved correctly
        Timesheet verifiedTimesheet = timesheetRepository.findById(savedTimesheet.getId()).orElse(null);
        if (verifiedTimesheet != null) {
            System.out.println("TimesheetService: Verified timesheet exists in database with ID: " + verifiedTimesheet.getId() + ", Status: " + verifiedTimesheet.getStatus());
            
            // Send notification email to admin
            sendTimesheetSubmissionEmail(savedTimesheet);
        } else {
            System.out.println("TimesheetService: WARNING - Could not verify timesheet in database with ID: " + savedTimesheet.getId());
        }
        
        return savedTimesheet;
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
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("Current user ID from SecurityContext: " + name);
            return name;
        } catch (Exception e) {
            System.err.println("Error getting current user ID: " + e.getMessage());
            throw e;
        }
    }
    
    private void sendApprovalEmail(Timesheet timesheet) {
        try {
            User user = userService.getUserById(timesheet.getUserId());
            String userName = user.getName() != null ? user.getName() : "User";
            String reviewedBy = "Administrator";
            
            // Try to get the name of the reviewer
            if (timesheet.getReviewedBy() != null) {
                try {
                    User reviewer = userService.getUserById(timesheet.getReviewedBy());
                    if (reviewer != null && reviewer.getName() != null) {
                        reviewedBy = reviewer.getName();
                    }
                } catch (Exception e) {
                    // Ignore and use default
                }
            }
            
            String subject = "Timesheet Approved - Status: APPROVED";
            String body = "Dear " + userName + ",\n\n" +
                "Your timesheet for the week ending " + timesheet.getWeekEnding() + " has been APPROVED.\n\n" +
                "Status: APPROVED\n" +
                "Total Hours: " + timesheet.getTotalHours() + "\n" +
                "Approved By: " + reviewedBy + "\n" +
                "Approved Date: " + (timesheet.getReviewedDate() != null ? timesheet.getReviewedDate() : new Date()) + "\n\n" +
                "Thank you for your submission.\n\n" +
                "Best regards,\n" +
                "SSRM Tech System";
            
            // Send and log the email
            emailService.sendEmail(user.getEmail(), subject, body, "TIMESHEET_APPROVED");
            System.out.println("Sent approval email to user: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Error sending approval email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendRejectionEmail(Timesheet timesheet) {
        try {
            User user = userService.getUserById(timesheet.getUserId());
            String userName = user.getName() != null ? user.getName() : "User";
            String comments = timesheet.getComments() != null ? timesheet.getComments() : "No reason provided";
            String reviewedBy = "Administrator";
            
            // Try to get the name of the reviewer
            if (timesheet.getReviewedBy() != null) {
                try {
                    User reviewer = userService.getUserById(timesheet.getReviewedBy());
                    if (reviewer != null && reviewer.getName() != null) {
                        reviewedBy = reviewer.getName();
                    }
                } catch (Exception e) {
                    // Ignore and use default
                }
            }
            
            String subject = "Timesheet Rejected - Status: REJECTED";
            String body = "Dear " + userName + ",\n\n" +
                "Your timesheet for the week ending " + timesheet.getWeekEnding() + " has been REJECTED.\n\n" +
                "Status: REJECTED\n" +
                "Total Hours: " + timesheet.getTotalHours() + "\n" +
                "Rejected By: " + reviewedBy + "\n" +
                "Rejected Date: " + (timesheet.getReviewedDate() != null ? timesheet.getReviewedDate() : new Date()) + "\n" +
                "Reason: " + comments + "\n\n" +
                "Please review and resubmit your timesheet.\n\n" +
                "Best regards,\n" +
                "SSRM Tech System";
            
            // Send and log the email
            emailService.sendEmail(user.getEmail(), subject, body, "TIMESHEET_REJECTED");
            System.out.println("Sent rejection email to user: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Error sending rejection email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void sendTimesheetSubmissionEmail(Timesheet timesheet) {
        try {
            // Get the user who submitted the timesheet
            User user = userService.getUserById(timesheet.getUserId());
            String userName = user.getName() != null ? user.getName() : "User";
            
            // Send email to the user
            String userSubject = "Timesheet Submitted - Status: PENDING";
            String userBody = "Dear " + userName + ",\n\n" +
                "Your timesheet for the week ending " + timesheet.getWeekEnding() + " has been submitted successfully.\n\n" +
                "Status: PENDING\n" +
                "Total Hours: " + timesheet.getTotalHours() + "\n\n" +
                "Your timesheet will be reviewed by an administrator.\n\n" +
                "Best regards,\n" +
                "SSRM Tech System";
            
            emailService.sendEmail(user.getEmail(), userSubject, userBody, "TIMESHEET_SUBMITTED");
            System.out.println("Sent submission email to user: " + user.getEmail());
            
            // Send notifications to all admins from admins collection
            try {
                List<Admin> admins = adminService.getAllAdmins();
                System.out.println("Found " + admins.size() + " admins in database for notification");
                
                for (Admin admin : admins) {
                    System.out.println("Sending notification to admin: " + admin.getEmail() + " (" + admin.getName() + ")");
                    
                    String adminSubject = "New Timesheet Submission - " + userName + " (Week ending " + timesheet.getWeekEnding() + ")";
                    String adminBody = "Dear " + admin.getName() + ",\n\n" +
                        "A new timesheet has been submitted for your review:\n\n" +
                        "Employee: " + userName + "\n" +
                        "Email: " + user.getEmail() + "\n" +
                        "Week Ending: " + timesheet.getWeekEnding() + "\n" +
                        "Status: PENDING\n" +
                        "Total Hours: " + timesheet.getTotalHours() + "\n\n" +
                        "Please review and approve/reject this timesheet in the admin dashboard.\n\n" +
                        "Best regards,\n" +
                        "SSRM Tech System";
                    
                    emailService.sendEmail(admin.getEmail(), adminSubject, adminBody, "ADMIN_NOTIFICATION");
                    System.out.println("Successfully sent notification email to admin: " + admin.getEmail());
                }
            } catch (Exception e) {
                System.err.println("Error sending admin notifications: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Send notifications to all parent admins from parent_admins collection
            try {
                List<ParentAdmin> parentAdmins = parentAdminService.getAllParentAdmins();
                System.out.println("Found " + parentAdmins.size() + " parent admins in database for notification");
                
                for (ParentAdmin parentAdmin : parentAdmins) {
                    System.out.println("Sending notification to parent admin: " + parentAdmin.getEmail() + " (" + parentAdmin.getName() + ")");
                    
                    String parentAdminSubject = "New Timesheet Submission - " + userName + " (Week ending " + timesheet.getWeekEnding() + ")";
                    String parentAdminBody = "Dear " + parentAdmin.getName() + ",\n\n" +
                        "A new timesheet has been submitted for your review:\n\n" +
                        "Employee: " + userName + "\n" +
                        "Email: " + user.getEmail() + "\n" +
                        "Week Ending: " + timesheet.getWeekEnding() + "\n" +
                        "Status: PENDING\n" +
                        "Total Hours: " + timesheet.getTotalHours() + "\n\n" +
                        "Please review and approve/reject this timesheet in the admin dashboard.\n\n" +
                        "Best regards,\n" +
                        "SSRM Tech System";
                    
                    emailService.sendEmail(parentAdmin.getEmail(), parentAdminSubject, parentAdminBody, "PARENT_ADMIN_NOTIFICATION");
                    System.out.println("Successfully sent notification email to parent admin: " + parentAdmin.getEmail());
                }
            } catch (Exception e) {
                System.err.println("Error sending parent admin notifications: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Error sending timesheet submission email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}