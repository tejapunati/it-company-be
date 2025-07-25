package com.ssrmtech.itcompany.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private String department;
    private String currentPassword;
    private String newPassword;
}