package com.ssrmtech.itcompany.service;

import com.ssrmtech.itcompany.model.Timesheet;

import java.util.List;

public interface TimesheetService {
    List<Timesheet> getAllTimesheets();
    Timesheet getTimesheetById(String id);
    List<Timesheet> getTimesheetsByUserId(String userId);
    List<Timesheet> getTimesheetsByStatus(String status);
    List<com.ssrmtech.itcompany.dto.TimesheetWithUserDTO> getTimesheetsWithUserDetails();
    List<com.ssrmtech.itcompany.dto.TimesheetWithUserDTO> getTimesheetsWithUserDetailsByStatus(String status);
    Timesheet createTimesheet(Timesheet timesheet);
    Timesheet updateTimesheet(Timesheet timesheet);
    Timesheet approveTimesheet(String id, String comments);
    Timesheet rejectTimesheet(String id, String comments);
    boolean isOwner(String timesheetId, String userId);
}