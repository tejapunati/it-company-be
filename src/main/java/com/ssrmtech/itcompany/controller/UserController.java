package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.dto.ProfileUpdateRequest;
import com.ssrmtech.itcompany.model.Admin;
import com.ssrmtech.itcompany.model.ParentAdmin;
import com.ssrmtech.itcompany.model.User;
import com.ssrmtech.itcompany.repository.AdminRepository;
import com.ssrmtech.itcompany.repository.ParentAdminRepository;
import com.ssrmtech.itcompany.repository.UserRepository;
import com.ssrmtech.itcompany.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ParentAdminRepository parentAdminRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<User> approveUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.approveUser(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN')")
    public ResponseEntity<User> rejectUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.rejectUser(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PARENT_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }


    @PostMapping("/profile-data")
    public ResponseEntity<?> getProfileData(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            User user = userService.findUserByEmailInAllTables(email);
            
            if (user == null) {
                user = userService.getUserByEmail(email);
            }
            
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching profile data: " + e.getMessage());
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request) {
        try {
            // Try to find user in User table first
            User existingUser = userService.getUserByEmail(request.getEmail());
            
            // If not found, try Admin and ParentAdmin tables
            if (existingUser == null) {
                existingUser = userService.findUserByEmailInAllTables(request.getEmail());
            }
            
            if (existingUser == null) {
                return ResponseEntity.badRequest().body("User not found with email: " + request.getEmail());
            }
            
            // Validate current password if new password is provided
            if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
                if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                    return ResponseEntity.badRequest().body("Current password is required to change password");
                }
                
                if (!passwordEncoder.matches(request.getCurrentPassword(), existingUser.getPassword())) {
                    return ResponseEntity.badRequest().body("Current password is incorrect");
                }
            }
            
            // Update user fields
            User updatedUser = new User();
            updatedUser.setId(existingUser.getId());
            updatedUser.setName(request.getName());
            updatedUser.setEmail(request.getEmail());
            updatedUser.setPhone(request.getPhone());
            updatedUser.setDepartment(request.getDepartment());
            
            if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
                updatedUser.setPassword(request.getNewPassword());
            }
            User result = userService.updateUser(updatedUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update profile: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARENT_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}