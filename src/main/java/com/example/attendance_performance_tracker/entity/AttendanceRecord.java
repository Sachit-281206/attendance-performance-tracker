package com.example.attendance_performance_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attendance_records")
@Data
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "session_id")
    private AttendanceSession session;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;
    
    public enum AttendanceStatus {
        PRESENT, ABSENT
    }
}