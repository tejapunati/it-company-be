package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.Admin;
import com.ssrmtech.itcompany.model.ParentAdmin;
import com.ssrmtech.itcompany.model.User;
import com.ssrmtech.itcompany.repository.AdminRepository;
import com.ssrmtech.itcompany.repository.ParentAdminRepository;
import com.ssrmtech.itcompany.repository.UserRepository;
import com.ssrmtech.itcompany.security.JwtUtils;
import com.ssrmtech.itcompany.security.UserDetailsImpl;
import com.ssrmtech.itcompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ParentAdminRepository parentAdminRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        String selectedRole = loginRequest.get("role");
        
        // Find actual user role in database
        String actualRole = findUserRole(email);
        
        if (actualRole == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Validate selected role matches actual role
        if (selectedRole != null && !actualRole.equalsIgnoreCase(selectedRole)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid role selected. You are registered as: " + actualRole);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Update last login time
        userService.updateLastLogin(userDetails.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("id", userDetails.getId());
        response.put("name", userDetails.getName());
        response.put("email", userDetails.getEmail());
        response.put("role", userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
        
        return ResponseEntity.ok(response);
    }
    
    private String findUserRole(String email) {
        // Check users collection
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return "USER";
        }
        
        // Check admins collection
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            return "ADMIN";
        }
        
        // Check parent_admins collection
        Optional<ParentAdmin> parentAdmin = parentAdminRepository.findByEmail(email);
        if (parentAdmin.isPresent()) {
            return "PARENT_ADMIN";
        }
        
        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        user.setStatus("PENDING");
        user.setCreatedDate(new Date());
        User createdUser = userService.createUser(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("id", createdUser.getId());
        response.put("status", createdUser.getStatus());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        // Find user in any collection
        String actualRole = findUserRole(email);
        
        if (actualRole == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Email not found in our system");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Generate temporary password
        String tempPassword = generateTempPassword();
        
        // Update password in appropriate collection
        boolean updated = updatePasswordByRole(email, actualRole, tempPassword);
        
        if (updated) {
            // Send email with temporary password
            sendPasswordResetEmail(email, tempPassword, actualRole);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Temporary password sent to your email");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to reset password");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    private String generateTempPassword() {
        return "temp" + System.currentTimeMillis() % 10000;
    }
    
    private boolean updatePasswordByRole(String email, String role, String tempPassword) {
        try {
            switch (role.toUpperCase()) {
                case "USER":
                    Optional<User> user = userRepository.findByEmail(email);
                    if (user.isPresent()) {
                        User u = user.get();
                        u.setPassword(tempPassword); // In production, encode this
                        userRepository.save(u);
                        return true;
                    }
                    break;
                case "ADMIN":
                    Optional<Admin> admin = adminRepository.findByEmail(email);
                    if (admin.isPresent()) {
                        Admin a = admin.get();
                        a.setPassword(tempPassword); // In production, encode this
                        adminRepository.save(a);
                        return true;
                    }
                    break;
                case "PARENT_ADMIN":
                    Optional<ParentAdmin> parentAdmin = parentAdminRepository.findByEmail(email);
                    if (parentAdmin.isPresent()) {
                        ParentAdmin pa = parentAdmin.get();
                        pa.setPassword(tempPassword); // In production, encode this
                        parentAdminRepository.save(pa);
                        return true;
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
        return false;
    }
    
    private void sendPasswordResetEmail(String email, String tempPassword, String role) {
        // Mock email sending - in production, use actual email service
        System.out.println("Password reset email sent to: " + email);
        System.out.println("Temporary password: " + tempPassword);
        System.out.println("User role: " + role);
    }
}