package com.example.attendance_performance_tracker.controller;

import com.example.attendance_performance_tracker.entity.User;
import com.example.attendance_performance_tracker.entity.Test;
import com.example.attendance_performance_tracker.entity.Mark;
import com.example.attendance_performance_tracker.repository.UserRepository;
import com.example.attendance_performance_tracker.repository.TestRepository;
import com.example.attendance_performance_tracker.repository.MarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/faculty")
public class FacultyController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TestRepository testRepository;
    
    @Autowired
    private MarkRepository markRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) {
            return "redirect:/login";
        }
        
        model.addAttribute("faculty", user);
        return "faculty/dashboard";
    }
    
    @GetMapping("/upload-marks")
    public String uploadMarksMenu(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) {
            return "redirect:/login";
        }
        
        List<Test> tests = testRepository.findByFaculty(user);
        model.addAttribute("tests", tests);
        model.addAttribute("faculty", user);
        return "faculty/upload-marks";
    }
    
    @GetMapping("/add-test")
    public String addTestForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) {
            return "redirect:/login";
        }
        
        model.addAttribute("faculty", user);
        return "faculty/add-test";
    }
    
    @PostMapping("/add-test")
    public String addTest(@RequestParam String testName,
                         @RequestParam Integer maxMarks,
                         @RequestParam String className,
                         HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) {
            return "redirect:/login";
        }
        
        Test test = new Test();
        test.setTestName(testName);
        test.setMaxMarks(maxMarks);
        test.setClassName(className);
        test.setFaculty(user);
        
        testRepository.save(test);
        return "redirect:/faculty/edit-test/" + test.getId();
    }
    
    @GetMapping("/edit-test/{testId}")
    public String editTest(@PathVariable Long testId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) {
            return "redirect:/login";
        }
        
        Test test = testRepository.findById(testId).orElse(null);
        if (test == null) {
            return "redirect:/faculty/upload-marks";
        }
        
        List<User> students = userRepository.findByRole(User.Role.STUDENT);
        List<Mark> existingMarks = markRepository.findByTest(test);
        
        // Create a map for easy lookup of existing marks
        java.util.Map<Long, Mark> existingMarksMap = new java.util.HashMap<>();
        for (Mark mark : existingMarks) {
            existingMarksMap.put(mark.getStudent().getId(), mark);
        }
        
        model.addAttribute("test", test);
        model.addAttribute("students", students);
        model.addAttribute("existingMarks", existingMarks);
        model.addAttribute("existingMarksMap", existingMarksMap);
        return "faculty/edit-test";
    }
    
    @PostMapping("/update-test")
    public String updateTest(@RequestParam Long testId,
                            @RequestParam String testName,
                            @RequestParam Integer maxMarks,
                            @RequestParam String className,
                            @RequestParam(required = false) List<Long> studentIds,
                            @RequestParam(required = false) List<Integer> marks,
                            HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) {
            return "redirect:/login";
        }
        
        Test test = testRepository.findById(testId).orElse(null);
        if (test == null) {
            return "redirect:/faculty/upload-marks";
        }
        
        // Update test details
        test.setTestName(testName);
        test.setMaxMarks(maxMarks);
        test.setClassName(className);
        testRepository.save(test);
        
        // Debug logging
        System.out.println("Updating marks for test: " + testId);
        System.out.println("Student IDs: " + studentIds);
        System.out.println("Marks: " + marks);
        
        // Update marks
        if (studentIds != null && marks != null) {
            for (int i = 0; i < studentIds.size() && i < marks.size(); i++) {
                Long studentId = studentIds.get(i);
                Integer markValue = marks.get(i);
                
                System.out.println("Processing student " + studentId + " with mark " + markValue);
                
                User student = userRepository.findById(studentId).orElse(null);
                if (student != null && markValue != null) {
                    // Check if mark already exists
                    Mark existingMark = markRepository.findFirstByStudentAndTest(student, test);
                    if (existingMark != null) {
                        System.out.println("Updating existing mark from " + existingMark.getMarksObtained() + " to " + markValue);
                        existingMark.setMarksObtained(markValue);
                        markRepository.save(existingMark);
                    } else {
                        System.out.println("Creating new mark: " + markValue);
                        Mark mark = new Mark();
                        mark.setStudent(student);
                        mark.setTest(test);
                        mark.setMarksObtained(markValue);
                        markRepository.save(mark);
                    }
                }
            }
        }
        
        return "redirect:/faculty/upload-marks";
    }
    
    @GetMapping("/delete-test/{testId}")
    public String deleteTest(@PathVariable Long testId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.FACULTY) {
            return "redirect:/login";
        }
        
        Test test = testRepository.findById(testId).orElse(null);
        if (test != null) {
            // Delete all marks for this test first
            List<Mark> marks = markRepository.findByTest(test);
            markRepository.deleteAll(marks);
            
            // Delete the test
            testRepository.delete(test);
        }
        
        return "redirect:/faculty/upload-marks";
    }
}