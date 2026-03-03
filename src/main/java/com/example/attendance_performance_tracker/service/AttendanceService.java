package com.example.attendance_performance_tracker.service;

import com.example.attendance_performance_tracker.entity.*;
import com.example.attendance_performance_tracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AttendanceService {
    
    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;
    
    public Map<String, Object> getStudentAttendanceStats(User student) {
        Long totalClasses = attendanceRecordRepository.countTotalByStudent(student);
        Long presentClasses = attendanceRecordRepository.countPresentByStudent(student);
        
        double percentage = totalClasses > 0 ? (presentClasses.doubleValue() / totalClasses) * 100 : 0;
        
        String performanceClass = "low";
        if (percentage >= 75) performanceClass = "high";
        else if (percentage >= 60) performanceClass = "medium";
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClasses", totalClasses);
        stats.put("presentClasses", presentClasses);
        stats.put("percentage", Math.round(percentage * 100.0) / 100.0);
        stats.put("performanceClass", performanceClass);
        
        return stats;
    }
}