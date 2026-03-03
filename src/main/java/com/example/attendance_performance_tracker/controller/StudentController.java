package com.example.attendance_performance_tracker.controller;

import com.example.attendance_performance_tracker.entity.User;
import com.example.attendance_performance_tracker.entity.Mark;
import com.example.attendance_performance_tracker.repository.MarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {
    
    @Autowired
    private MarkRepository markRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.STUDENT) {
            return "redirect:/login";
        }
        
        model.addAttribute("student", user);
        return "student/dashboard";
    }
    
    @GetMapping("/marks")
    public String viewMarks(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.STUDENT) {
            return "redirect:/login";
        }
        
        List<Mark> marks = markRepository.findByStudentOrderByTestCreatedDateDesc(user);
        model.addAttribute("marks", marks);
        model.addAttribute("student", user);
        return "student/marks";
    }
}