package com.ssrmtech.itcompany.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Data
@Document(collection = "timesheets")
public class Timesheet {
    @Id
    private String id;
    
    private String userId;
    
    private String weekEnding;
    
    private String status;
    
    private Integer totalHours;
    
    private Date submittedDate;
    
    private Date reviewedDate;
    
    private String reviewedBy;
    
    private String comments;
    
    private Map<String, Integer> hours;
}