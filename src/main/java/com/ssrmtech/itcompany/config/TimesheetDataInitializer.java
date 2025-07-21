package com.ssrmtech.itcompany.config;

import com.ssrmtech.itcompany.model.Timesheet;
import com.ssrmtech.itcompany.model.User;
import com.ssrmtech.itcompany.repository.TimesheetRepository;
import com.ssrmtech.itcompany.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
public class TimesheetDataInitializer {

    @Bean
    public CommandLineRunner initTimesheetData(TimesheetRepository timesheetRepository, 
                                              UserRepository userRepository,
                                              MongoTemplate mongoTemplate) {
        return args -> {
            System.out.println("\n=== Checking Test Timesheets ===\n");
            
            // Get the regular user
            Optional<User> userOpt = userRepository.findByEmail("user@ssrmtech.com");
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Check if we have any PENDING timesheets for this user
                List<Timesheet> pendingTimesheets = timesheetRepository.findByUserIdAndStatus(user.getId(), "PENDING");
                
                if (pendingTimesheets.isEmpty()) {
                    System.out.println("No pending timesheets found. Creating a new test timesheet.");
                    
                    // Create a test timesheet
                    Timesheet timesheet = new Timesheet();
                    timesheet.setUserId(user.getId());
                    timesheet.setWeekEnding("2025-07-21");
                    timesheet.setStatus("PENDING");
                    timesheet.setTotalHours(40);
                    timesheet.setSubmittedDate(new Date());
                    
                    // Set hours for each day
                    Map<String, Integer> hours = new HashMap<>();
                    hours.put("Monday", 8);
                    hours.put("Tuesday", 8);
                    hours.put("Wednesday", 8);
                    hours.put("Thursday", 8);
                    hours.put("Friday", 8);
                    hours.put("Saturday", 0);
                    hours.put("Sunday", 0);
                    timesheet.setHours(hours);
                    
                    timesheetRepository.save(timesheet);
                    System.out.println("Created new test timesheet for user: " + user.getEmail());
                } else {
                    System.out.println("Found " + pendingTimesheets.size() + " pending timesheets for user: " + user.getEmail());
                }
            }
            
            System.out.println("Total timesheets in database: " + timesheetRepository.count());
            System.out.println("Pending timesheets in database: " + timesheetRepository.findByStatus("PENDING").size());
            System.out.println("\n=== Test Timesheets Check Complete ===\n");
        };
    }
}