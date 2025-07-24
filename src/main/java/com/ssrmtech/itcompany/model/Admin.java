package com.ssrmtech.itcompany.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "admins")
public class Admin {
    @Id
    private String id;
    
    private String name;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    
    private String phone;
    
    private String department;
    
    private String role = "ADMIN";
    
    private String status = "ACTIVE";
    
    private Date createdDate;
    
    private Date lastLogin;
}