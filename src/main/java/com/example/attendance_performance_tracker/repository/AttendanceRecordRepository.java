package com.example.attendance_performance_tracker.repository;

import com.example.attendance_performance_tracker.entity.AttendanceRecord;
import com.example.attendance_performance_tracker.entity.AttendanceSession;
import com.example.attendance_performance_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findBySession(AttendanceSession session);
@Query("SELECT ar FROM AttendanceRecord ar WHERE ar.student = ?1")
    List<AttendanceRecord> findByStudent(User student);
    AttendanceRecord findBySessionAndStudent(AttendanceSession session, User student);
    
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar WHERE ar.student = ?1 AND ar.status = 'PRESENT'")
    Long countPresentByStudent(User student);
    
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar WHERE ar.student = ?1")
    Long countTotalByStudent(User student);
}