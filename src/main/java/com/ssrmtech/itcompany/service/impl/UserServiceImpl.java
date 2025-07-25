package com.ssrmtech.itcompany.service.impl;

import com.ssrmtech.itcompany.model.Admin;
import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.model.ParentAdmin;
import com.ssrmtech.itcompany.model.User;
import com.ssrmtech.itcompany.repository.AdminRepository;
import com.ssrmtech.itcompany.repository.ParentAdminRepository;
import com.ssrmtech.itcompany.repository.UserRepository;
import com.ssrmtech.itcompany.service.EmailLogService;
import com.ssrmtech.itcompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ParentAdminRepository parentAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailLogService emailLogService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(String id) {
        System.out.println("Looking up user by ID: " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        System.out.println("Found user: " + user.getEmail());
        return user;
    }

    @Override
    public User createUser(User user) {
        try {
            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new RuntimeException("Email already in use");
            }
            
            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Set default values
            user.setStatus("ACTIVE");
            user.setCreatedDate(new Date());
            
            User savedUser = userRepository.save(user);
            
            // Send email notification to admins
            sendAdminNotification(savedUser);
            
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    @Override
    public User updateUser(User user) {
        // Try to find in User table first
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        
        if (existingUser != null) {
            // Update regular user
            updateUserFields(existingUser, user);
            return userRepository.save(existingUser);
        }
        
        // Try Admin table
        Admin admin = adminRepository.findById(user.getId()).orElse(null);
        if (admin != null) {
            updateAdminFields(admin, user);
            adminRepository.save(admin);
            return convertAdminToUser(admin);
        }
        
        // Try ParentAdmin table
        ParentAdmin parentAdmin = parentAdminRepository.findById(user.getId()).orElse(null);
        if (parentAdmin != null) {
            updateParentAdminFields(parentAdmin, user);
            parentAdminRepository.save(parentAdmin);
            return convertParentAdminToUser(parentAdmin);
        }
        
        throw new RuntimeException("User not found with id: " + user.getId());
    }
    
    private void updateUserFields(User existingUser, User user) {
        if (user.getName() != null) existingUser.setName(user.getName());
        if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
        if (user.getPhone() != null) existingUser.setPhone(user.getPhone());
        if (user.getDepartment() != null) existingUser.setDepartment(user.getDepartment());
        if (user.getRole() != null) existingUser.setRole(user.getRole());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }
    
    private void updateAdminFields(Admin admin, User user) {
        if (user.getName() != null) admin.setName(user.getName());
        if (user.getEmail() != null) admin.setEmail(user.getEmail());
        if (user.getPhone() != null) admin.setPhone(user.getPhone());
        if (user.getDepartment() != null) admin.setDepartment(user.getDepartment());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }
    
    private void updateParentAdminFields(ParentAdmin parentAdmin, User user) {
        if (user.getName() != null) parentAdmin.setName(user.getName());
        if (user.getEmail() != null) parentAdmin.setEmail(user.getEmail());
        if (user.getPhone() != null) parentAdmin.setPhone(user.getPhone());
        if (user.getDepartment() != null) parentAdmin.setDepartment(user.getDepartment());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            parentAdmin.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }
    
    private User convertAdminToUser(Admin admin) {
        User user = new User();
        user.setId(admin.getId());
        user.setName(admin.getName());
        user.setEmail(admin.getEmail());
        user.setPhone(admin.getPhone());
        user.setDepartment(admin.getDepartment());
        user.setRole(admin.getRole());
        return user;
    }
    
    private User convertParentAdminToUser(ParentAdmin parentAdmin) {
        User user = new User();
        user.setId(parentAdmin.getId());
        user.setName(parentAdmin.getName());
        user.setEmail(parentAdmin.getEmail());
        user.setPhone(parentAdmin.getPhone());
        user.setDepartment(parentAdmin.getDepartment());
        user.setRole(parentAdmin.getRole());
        return user;
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User approveUser(String id) {
        User user = getUserById(id);
        user.setStatus("ACTIVE");
        User savedUser = userRepository.save(user);
        
        // Send approval email
        sendApprovalEmail(savedUser);
        
        return savedUser;
    }

    @Override
    public User rejectUser(String id) {
        User user = getUserById(id);
        user.setStatus("REJECTED");
        User savedUser = userRepository.save(user);
        
        // Send rejection email
        sendRejectionEmail(savedUser);
        
        return savedUser;
    }

    @Override
    public void updateLastLogin(String id) {
        // Try parent admin first
        ParentAdmin parentAdmin = parentAdminRepository.findById(id).orElse(null);
        if (parentAdmin != null) {
            parentAdmin.setLastLogin(new Date());
            parentAdminRepository.save(parentAdmin);
            return;
        }
        
        // Try admin
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin != null) {
            admin.setLastLogin(new Date());
            adminRepository.save(admin);
            return;
        }
        
        // Try regular user
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setLastLogin(new Date());
            userRepository.save(user);
        }
    }
    
    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    public User findUserByEmailInAllTables(String email) {
        // Check Admin table
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            User user = new User();
            user.setId(admin.getId());
            user.setName(admin.getName());
            user.setEmail(admin.getEmail());
            user.setPhone(admin.getPhone());
            user.setDepartment(admin.getDepartment());
            user.setRole(admin.getRole());
            user.setPassword(admin.getPassword());
            return user;
        }
        
        // Check ParentAdmin table
        ParentAdmin parentAdmin = parentAdminRepository.findByEmail(email).orElse(null);
        if (parentAdmin != null) {
            User user = new User();
            user.setId(parentAdmin.getId());
            user.setName(parentAdmin.getName());
            user.setEmail(parentAdmin.getEmail());
            user.setPhone(parentAdmin.getPhone());
            user.setDepartment(parentAdmin.getDepartment());
            user.setRole(parentAdmin.getRole());
            user.setPassword(parentAdmin.getPassword());
            return user;
        }
        
        return null;
    }
    
    private void sendAdminNotification(User user) {
        EmailLog emailLog = new EmailLog();
        emailLog.setToEmail("admin@ssrmtech.com");
        emailLog.setFromEmail("noreply@ssrmtech.com");
        emailLog.setSubject("New User Registration");
        emailLog.setBody("A new user has registered and requires your approval: " + user.getName() + " (" + user.getEmail() + ")");
        emailLog.setType("USER_APPROVAL");
        emailLog.setStatus("SENT");
        emailLog.setSentDate(new Date());
        
        emailLogService.createEmailLog(emailLog);
    }
    
    private void sendApprovalEmail(User user) {
        EmailLog emailLog = new EmailLog();
        emailLog.setToEmail(user.getEmail());
        emailLog.setFromEmail("noreply@ssrmtech.com");
        emailLog.setSubject("Account Approved");
        emailLog.setBody("Your account has been approved. You can now log in to the system.");
        emailLog.setType("USER_APPROVED");
        emailLog.setStatus("SENT");
        emailLog.setSentDate(new Date());
        
        emailLogService.createEmailLog(emailLog);
    }
    
    private void sendRejectionEmail(User user) {
        EmailLog emailLog = new EmailLog();
        emailLog.setToEmail(user.getEmail());
        emailLog.setFromEmail("noreply@ssrmtech.com");
        emailLog.setSubject("Account Rejected");
        emailLog.setBody("Your account registration has been rejected. Please contact support for more information.");
        emailLog.setType("USER_REJECTED");
        emailLog.setStatus("SENT");
        emailLog.setSentDate(new Date());
        
        emailLogService.createEmailLog(emailLog);
    }
}