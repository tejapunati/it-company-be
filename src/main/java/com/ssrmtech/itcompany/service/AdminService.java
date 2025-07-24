package com.ssrmtech.itcompany.service;

import com.ssrmtech.itcompany.model.Admin;

import java.util.List;

public interface AdminService {
    List<Admin> getAllAdmins();
    Admin createAdmin(Admin admin);
    void deleteAdmin(String id);
}