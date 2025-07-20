package com.ssrmtech.itcompany.repository;

import com.ssrmtech.itcompany.model.Timesheet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TimesheetRepository extends MongoRepository<Timesheet, String> {
    List<Timesheet> findByUserId(String userId);
    List<Timesheet> findByStatus(String status);
    List<Timesheet> findByUserIdAndStatus(String userId, String status);
}