package com.example.attendance_performance_tracker.repository;

import com.example.attendance_performance_tracker.entity.AttendanceSession;
import com.example.attendance_performance_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
    List<AttendanceSession> findByFaculty(User faculty);
    @Query("SELECT a FROM AttendanceSession a WHERE a.faculty = ?1 ORDER BY a.attendanceDate DESC")
    List<AttendanceSession> findByFacultyOrderByAttendanceDateDesc(User faculty);
    Optional<AttendanceSession> findByClassNameAndAttendanceDateAndSubject(String className, LocalDate date, String subject);
    List<AttendanceSession> findByClassNameOrderByAttendanceDateDesc(String className);
}