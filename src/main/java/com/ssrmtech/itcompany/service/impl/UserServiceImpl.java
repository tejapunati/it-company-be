package com.ssrmtech.itcompany.service.impl;

import com.ssrmtech.itcompany.model.EmailLog;
import com.ssrmtech.itcompany.model.User;
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
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default values
        user.setStatus("PENDING");
        user.setCreatedDate(new Date());
        
        User savedUser = userRepository.save(user);
        
        // Send email notification to admins
        sendAdminNotification(savedUser);
        
        return savedUser;
    }

    @Override
    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());
        
        // Update fields
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        existingUser.setDepartment(user.getDepartment());
        
        // Only admins can update role
        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }
        
        // Update password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(existingUser);
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
        User user = getUserById(id);
        user.setLastLogin(new Date());
        userRepository.save(user);
    }
    
    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
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