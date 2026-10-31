package com.example.SchoolWebsite.controller;

import com.example.SchoolWebsite.model.UserAccount;
import com.example.SchoolWebsite.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Show the login page (or redirect if already logged in)
    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (role != null) {
            switch (role.toLowerCase()) {
                case "student":
                    return "redirect:/student/dashboard";
                case "teacher":
                    return "redirect:/teacher/dashboard";
                case "staff":
                    return "redirect:/staff/dashboard";
                case "admin":
                    return "redirect:/admin/dashboard";
            }
        }
        return "login";
    }

    // Unified password-based login for all roles using User_Account table
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String password,
            HttpSession session,
            Model model) {

        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("error", "Please enter your User ID or Username.");
            return "login";
        }

        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Please enter your password.");
            model.addAttribute("userId", userId.trim());
            return "login";
        }

        UserAccount user = userRepository.findByUsername(userId.trim());

        if (user == null) {
            model.addAttribute("error", "No user found matching: " + userId.trim());
            model.addAttribute("userId", userId.trim());
            return "login";
        }

        if (!user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid password.");
            model.addAttribute("userId", userId.trim());
            return "login";
        }

        // Set session
        session.setAttribute("userId", user.getUsername());
        session.setAttribute("role", user.getRole().toLowerCase());

        switch (user.getRole().toLowerCase()) {
            case "admin":
                return "redirect:/admin/dashboard";
            case "student":
                return "redirect:/student/dashboard";
            case "teacher":
                return "redirect:/teacher/dashboard";
            case "staff":
                return "redirect:/staff/dashboard";
            default:
                return "redirect:/";
        }
    }

    // Logout — clear session and go home
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
