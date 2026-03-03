package com.example.attendance_performance_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tests")
@Data
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String testName;
    
    @Column(nullable = false)
    private Integer maxMarks;
    
    @Column(nullable = false)
    private String className;
    
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private User faculty;
    
    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<Mark> marks;
}