package com.ssrmtech.itcompany.config;

import com.ssrmtech.itcompany.model.User;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user exists
        if (userRepository.findByEmail("parent-admin@ssrmtech.com").isEmpty()) {
            User adminUser = new User();
            adminUser.setName("Parent Admin");
            adminUser.setEmail("parent-admin@ssrmtech.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole("PARENT_ADMIN");
            adminUser.setStatus("ACTIVE");
            adminUser.setDepartment("Administration");
            adminUser.setPhone("123-456-7890");
            adminUser.setCreatedDate(new Date());
            
            userRepository.save(adminUser);
        }
        
        // Check if regular admin exists
        if (userRepository.findByEmail("admin@ssrmtech.com").isEmpty()) {
            User adminUser = new User();
            adminUser.setName("Regular Admin");
            adminUser.setEmail("admin@ssrmtech.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole("ADMIN");
            adminUser.setStatus("ACTIVE");
            adminUser.setDepartment("IT");
            adminUser.setPhone("123-456-7891");
            adminUser.setCreatedDate(new Date());
            
            userRepository.save(adminUser);
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