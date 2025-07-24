package com.ssrmtech.itcompany.service.impl;

import com.ssrmtech.itcompany.model.Admin;
import com.ssrmtech.itcompany.repository.AdminRepository;
import com.ssrmtech.itcompany.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin createAdmin(Admin admin) {
        if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");
        admin.setCreatedDate(new Date());
        
        return adminRepository.save(admin);
    }

    @Override
    public void deleteAdmin(String id) {
        adminRepository.deleteById(id);
    }
}