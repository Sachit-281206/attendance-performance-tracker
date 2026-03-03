package com.example.attendance_performance_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "marks")
@Data
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;
    
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
    
    @Column(nullable = false)
    private Integer marksObtained;
    
    public Double getPercentage() {
        if (test != null && test.getMaxMarks() != null && marksObtained != null) {
            return (marksObtained.doubleValue() / test.getMaxMarks()) * 100;
        }
        return 0.0;
    }
}