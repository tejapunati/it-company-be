package com.ssrmtech.itcompany.config;

import com.ssrmtech.itcompany.model.Admin;
import com.ssrmtech.itcompany.model.ParentAdmin;
import com.ssrmtech.itcompany.model.User;
import com.ssrmtech.itcompany.repository.AdminRepository;
import com.ssrmtech.itcompany.repository.ParentAdminRepository;
import com.ssrmtech.itcompany.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ParentAdminRepository parentAdminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if parent admin exists
        if (parentAdminRepository.findByEmail("parent-admin@ssrmtech.com").isEmpty()) {
            ParentAdmin parentAdmin = new ParentAdmin();
            parentAdmin.setName("Parent Admin");
            parentAdmin.setEmail("parent-admin@ssrmtech.com");
            parentAdmin.setPassword(passwordEncoder.encode("admin123"));
            parentAdmin.setDepartment("Administration");
            parentAdmin.setPhone("123-456-7890");
            parentAdmin.setCreatedDate(new Date());
            
            parentAdminRepository.save(parentAdmin);
        }
        
        // Check if regular admin exists
        if (adminRepository.findByEmail("admin@ssrmtech.com").isEmpty()) {
            Admin admin = new Admin();
            admin.setName("Regular Admin");
            admin.setEmail("admin@ssrmtech.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setDepartment("IT");
            admin.setPhone("123-456-7891");
            admin.setCreatedDate(new Date());
            
            adminRepository.save(admin);
        }
        
        // Check if regular user exists
        if (userRepository.findByEmail("user@ssrmtech.com").isEmpty()) {
            User regularUser = new User();
            regularUser.setName("Regular User");
            regularUser.setEmail("user@ssrmtech.com");
            regularUser.setPassword(passwordEncoder.encode("user123"));
            regularUser.setRole("USER");
            regularUser.setStatus("ACTIVE");
            regularUser.setDepartment("Development");
            regularUser.setPhone("123-456-7892");
            regularUser.setCreatedDate(new Date());
            
            userRepository.save(regularUser);
        }
    }
}