package com.example.attendance_performance_tracker.repository;

import com.example.attendance_performance_tracker.entity.Test;
import com.example.attendance_performance_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByFaculty(User faculty);
    List<Test> findByClassName(String className);
}