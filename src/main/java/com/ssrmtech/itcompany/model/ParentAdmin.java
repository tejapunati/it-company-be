package com.ssrmtech.itcompany.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "parent_admins")
public class ParentAdmin {
    @Id
    private String id;
    
    private String name;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    
    private String phone;
    
    private String department;
    
    private String role = "PARENT_ADMIN";
    
    private String status = "ACTIVE";
    
    private Date createdDate;
    
    private Date lastLogin;
}