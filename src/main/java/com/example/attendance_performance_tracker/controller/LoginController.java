package com.example.attendance_performance_tracker.controller;

import com.example.attendance_performance_tracker.entity.User;
import com.example.attendance_performance_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                       @RequestParam String role, HttpSession session, Model model) {
        
        return userRepository.findByUsernameOrEmail(username, username)
            .filter(user -> user.getPassword().equals(password))
            .filter(user -> user.getRole().name().equals(role))
            .map(user -> {
                session.setAttribute("user", user);
                return user.getRole() == User.Role.STUDENT ? 
                    "redirect:/student/dashboard" : "redirect:/faculty/dashboard";
            })
            .orElseGet(() -> {
                model.addAttribute("error", "Invalid credentials or role");
                return "auth/login";
            });
    }
    
    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }
    
    @PostMapping("/signup")
    public String signup(@RequestParam String name, @RequestParam String username,
                        @RequestParam String email, @RequestParam String password,
                        @RequestParam String role, Model model) {
        
        if (userRepository.findByUsernameOrEmail(username, email).isPresent()) {
            model.addAttribute("error", "Username or email already exists");
            return "auth/signup";
        }
        
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(User.Role.valueOf(role));
        userRepository.save(user);
        
        model.addAttribute("success", "Account created successfully! Please login.");
        return "auth/login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}