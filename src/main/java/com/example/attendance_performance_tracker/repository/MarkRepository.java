package com.example.attendance_performance_tracker.repository;

import com.example.attendance_performance_tracker.entity.Mark;
import com.example.attendance_performance_tracker.entity.User;
import com.example.attendance_performance_tracker.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByStudent(User student);
    List<Mark> findByTest(Test test);
    List<Mark> findByStudentOrderByTestCreatedDateDesc(User student);
    Mark findFirstByStudentAndTest(User student, Test test);
}