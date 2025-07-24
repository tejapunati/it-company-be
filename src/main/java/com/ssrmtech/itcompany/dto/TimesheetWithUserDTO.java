package com.ssrmtech.itcompany.dto;

import com.ssrmtech.itcompany.model.Timesheet;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class TimesheetWithUserDTO {
    private String id;
    private String userId;
    private String userName;
    private String userEmail;
    private String weekEnding;
    private Map<String, Integer> hours;
    private Integer totalHours;
    private String comments;
    private String status;
    private Date submittedDate;
    private Date reviewedDate;
    private String reviewedBy;
    
    public static TimesheetWithUserDTO fromTimesheet(Timesheet timesheet, String userName, String userEmail) {
        TimesheetWithUserDTO dto = new TimesheetWithUserDTO();
        dto.setId(timesheet.getId());
        dto.setUserId(timesheet.getUserId());
        dto.setUserName(userName);
        dto.setUserEmail(userEmail);
        dto.setWeekEnding(timesheet.getWeekEnding());
        dto.setHours(timesheet.getHours());
        dto.setTotalHours(timesheet.getTotalHours());
        dto.setComments(timesheet.getComments());
        dto.setStatus(timesheet.getStatus());
        dto.setSubmittedDate(timesheet.getSubmittedDate());
        dto.setReviewedDate(timesheet.getReviewedDate());
        dto.setReviewedBy(timesheet.getReviewedBy());
        return dto;
    }
}