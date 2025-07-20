package com.ssrmtech.itcompany.controller;

import com.ssrmtech.itcompany.model.User;
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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.get("email"), loginRequest.get("password")));

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
}