package com.ssrmtech.itcompany.util;

import com.ssrmtech.itcompany.service.AdminService;
import com.ssrmtech.itcompany.service.ParentAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailCollectionResolver {
    
    private final AdminService adminService;
    private final ParentAdminService parentAdminService;
    
    public static final String DEFAULT_COLLECTION = "email_logs";
    public static final String USER_COLLECTION = "user_email_logs";
    public static final String ADMIN_COLLECTION = "admin_email_logs";
    public static final String PARENT_ADMIN_COLLECTION = "parent_admin_email_logs";
    
    /**
     * Determines the appropriate collection name based on email type and recipient
     * 
     * @param emailType The type of email (e.g., TIMESHEET_SUBMITTED, ADMIN_NOTIFICATION)
     * @param recipientEmail The email address of the recipient
     * @return The name of the collection where the email log should be stored
     */
    public String resolveCollectionName(String emailType, String recipientEmail) {
        System.out.println("Resolving collection for email type: " + emailType + ", recipient: " + recipientEmail);
        
        // Handle null cases
        if (emailType == null && recipientEmail == null) {
            System.out.println("Both email type and recipient are null, using default collection: " + DEFAULT_COLLECTION);
            return DEFAULT_COLLECTION;
        }
        
        // Check actual database to determine user role
        if (recipientEmail != null) {
            try {
                // Check if recipient is a parent admin
                if (parentAdminService.getAllParentAdmins().stream()
                    .anyMatch(pa -> pa.getEmail().equalsIgnoreCase(recipientEmail))) {
                    System.out.println("Recipient is parent admin (from database), using collection: " + PARENT_ADMIN_COLLECTION);
                    return PARENT_ADMIN_COLLECTION;
                }
                
                // Check if recipient is an admin
                if (adminService.getAllAdmins().stream()
                    .anyMatch(admin -> admin.getEmail().equalsIgnoreCase(recipientEmail))) {
                    System.out.println("Recipient is admin (from database), using collection: " + ADMIN_COLLECTION);
                    return ADMIN_COLLECTION;
                }
                
                // If not found in admin collections, treat as user
                System.out.println("Recipient not found in admin collections, using collection: " + USER_COLLECTION);
                return USER_COLLECTION;
                
            } catch (Exception e) {
                System.err.println("Error checking admin status for " + recipientEmail + ": " + e.getMessage());
                // Fallback to old logic if database check fails
                String lowerEmail = recipientEmail.toLowerCase();
                if (lowerEmail.contains("parent-admin")) {
                    return PARENT_ADMIN_COLLECTION;
                } else if (lowerEmail.contains("admin")) {
                    return ADMIN_COLLECTION;
                } else {
                    return USER_COLLECTION;
                }
            }
        }
        
        // If recipient doesn't give us enough info, try by email type
        if (emailType != null) {
            String upperType = emailType.toUpperCase();
            
            if (upperType.equals("ADMIN_NOTIFICATION") || upperType.contains("ADMIN")) {
                // For admin notifications, we need to check if it's for parent admin
                if (recipientEmail != null && recipientEmail.toLowerCase().contains("parent-admin")) {
                    System.out.println("Admin notification for parent admin, using collection: " + PARENT_ADMIN_COLLECTION);
                    return PARENT_ADMIN_COLLECTION;
                } else {
                    System.out.println("Admin notification, using collection: " + ADMIN_COLLECTION);
                    return ADMIN_COLLECTION;
                }
            } else if (upperType.startsWith("TIMESHEET_") || 
                      upperType.equals("USER_NOTIFICATION") || 
                      upperType.equals("ACCOUNT_CREATED") ||
                      upperType.contains("USER")) {
                System.out.println("User-related email type, using collection: " + USER_COLLECTION);
                return USER_COLLECTION;
            }
        }
        
        // Default collection for other types
        System.out.println("Could not determine specific collection, using default: " + DEFAULT_COLLECTION);
        return DEFAULT_COLLECTION;
    }
}