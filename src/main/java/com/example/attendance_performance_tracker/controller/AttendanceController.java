package com.example.attendance_performance_tracker.controller;

import com.example.attendance_performance_tracker.entity.*;
import com.example.attendance_performance_tracker.repository.*;
import com.example.attendance_performance_tracker.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {
    
    @Autowired private AttendanceSessionRepository sessionRepository;
    @Autowired private AttendanceRecordRepository recordRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AttendanceService attendanceService;
    
    @GetMapping("/manage")
    public String manageAttendance(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) return "redirect:/login";
        
        model.addAttribute("sessions", sessionRepository.findByFacultyOrderByAttendanceDateDesc(user));
        model.addAttribute("faculty", user);
        return "attendance/manage";
    }
    
    @GetMapping("/create")
    public String createAttendanceForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) return "redirect:/login";
        
        model.addAttribute("faculty", user);
        return "attendance/create";
    }
    
    @PostMapping("/create")
    public String createAttendance(@RequestParam String className, @RequestParam String subject, 
                                 @RequestParam String attendanceDate, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) return "redirect:/login";
        
        LocalDate date = LocalDate.parse(attendanceDate);
        if (sessionRepository.findByClassNameAndAttendanceDateAndSubject(className, date, subject).isPresent()) {
            return "redirect:/attendance/create?error=duplicate";
        }
        
        AttendanceSession session1 = new AttendanceSession();
        session1.setClassName(className);
        session1.setSubject(subject);
        session1.setAttendanceDate(date);
        session1.setFaculty(user);
        sessionRepository.save(session1);
        
        return "redirect:/attendance/mark/" + session1.getId();
    }
    
    @GetMapping("/mark/{sessionId}")
    public String markAttendance(@PathVariable Long sessionId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) return "redirect:/login";
        
        AttendanceSession attendanceSession = sessionRepository.findById(sessionId).orElse(null);
        if (attendanceSession == null) return "redirect:/attendance/manage";
        
        Map<Long, AttendanceRecord> recordMap = new HashMap<>();
        recordRepository.findBySession(attendanceSession)
            .forEach(record -> recordMap.put(record.getStudent().getId(), record));
        
        model.addAttribute("attendanceSession", attendanceSession);
        model.addAttribute("students", userRepository.findByRole(User.Role.STUDENT));
        model.addAttribute("recordMap", recordMap);
        return "attendance/mark";
    }
    
    @PostMapping("/save")
    public String saveAttendance(@RequestParam Long sessionId, @RequestParam List<Long> studentIds,
                               @RequestParam List<String> statuses, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) return "redirect:/login";
        
        AttendanceSession attendanceSession = sessionRepository.findById(sessionId).orElse(null);
        if (attendanceSession != null) {
            recordRepository.deleteAll(recordRepository.findBySession(attendanceSession));
            
            for (int i = 0; i < studentIds.size() && i < statuses.size(); i++) {
                User student = userRepository.findById(studentIds.get(i)).orElse(null);
                if (student != null) {
                    AttendanceRecord record = new AttendanceRecord();
                    record.setSession(attendanceSession);
                    record.setStudent(student);
                    record.setStatus(AttendanceRecord.AttendanceStatus.valueOf(statuses.get(i)));
                    recordRepository.save(record);
                }
            }
        }
        
        model.addAttribute("message", "Attendance saved successfully!");
        return "attendance/success";
    }
    
    @GetMapping("/delete/{sessionId}")
    public String deleteSession(@PathVariable Long sessionId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) return "redirect:/login";
        
        sessionRepository.findById(sessionId).ifPresent(attendanceSession -> {
            recordRepository.deleteAll(recordRepository.findBySession(attendanceSession));
            sessionRepository.delete(attendanceSession);
        });
        
        return "redirect:/attendance/manage";
    }
    
    @GetMapping("/view")
    public String viewAttendance(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.STUDENT) return "redirect:/login";
        
        Map<Long, AttendanceRecord> recordMap = new HashMap<>();
        recordRepository.findByStudent(user)
            .forEach(record -> recordMap.put(record.getSession().getId(), record));
        
        List<Map<String, Object>> attendanceData = new ArrayList<>();
        sessionRepository.findAll().forEach(attendanceSession -> {
            Map<String, Object> data = new HashMap<>();
            data.put("session", attendanceSession);
            AttendanceRecord record = recordMap.get(attendanceSession.getId());
            data.put("status", record != null ? record.getStatus().name() : "NOT_MARKED");
            attendanceData.add(data);
        });
        
        model.addAttribute("student", user);
        model.addAttribute("attendanceData", attendanceData);
        model.addAttribute("stats", attendanceService.getStudentAttendanceStats(user));
        return "attendance/view";
    }
}