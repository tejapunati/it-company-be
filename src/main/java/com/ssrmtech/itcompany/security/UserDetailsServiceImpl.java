package com.ssrmtech.itcompany.security;

import com.ssrmtech.itcompany.model.Admin;
import com.ssrmtech.itcompany.model.ParentAdmin;
import com.ssrmtech.itcompany.model.User;
import com.ssrmtech.itcompany.repository.AdminRepository;
import com.ssrmtech.itcompany.repository.ParentAdminRepository;
import com.ssrmtech.itcompany.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ParentAdminRepository parentAdminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check parent_admins collection first
        ParentAdmin parentAdmin = parentAdminRepository.findByEmail(username).orElse(null);
        if (parentAdmin != null) {
            User user = new User();
            user.setId(parentAdmin.getId());
            user.setName(parentAdmin.getName());
            user.setEmail(parentAdmin.getEmail());
            user.setPassword(parentAdmin.getPassword());
            user.setPhone(parentAdmin.getPhone());
            user.setDepartment(parentAdmin.getDepartment());
            user.setRole(parentAdmin.getRole());
            user.setStatus(parentAdmin.getStatus());
            user.setCreatedDate(parentAdmin.getCreatedDate());
            user.setLastLogin(parentAdmin.getLastLogin());
            return UserDetailsImpl.build(user);
        }
        
        // Check admins collection
        Admin admin = adminRepository.findByEmail(username).orElse(null);
        if (admin != null) {
            User user = new User();
            user.setId(admin.getId());
            user.setName(admin.getName());
            user.setEmail(admin.getEmail());
            user.setPassword(admin.getPassword());
            user.setPhone(admin.getPhone());
            user.setDepartment(admin.getDepartment());
            user.setRole(admin.getRole());
            user.setStatus(admin.getStatus());
            user.setCreatedDate(admin.getCreatedDate());
            user.setLastLogin(admin.getLastLogin());
            return UserDetailsImpl.build(user);
        }
        
        // Check users collection
        User user = userRepository.findByEmail(username).orElse(null);
        if (user != null) {
            return UserDetailsImpl.build(user);
        }
        
        throw new UsernameNotFoundException("User Not Found with email: " + username);
    }
}