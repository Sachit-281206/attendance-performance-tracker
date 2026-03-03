package com.example.attendance_performance_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "attendance_sessions")
@Data
public class AttendanceSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String className;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(nullable = false)
    private LocalDate attendanceDate;
    
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private User faculty;
    

    
    @Column(nullable = false)
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}