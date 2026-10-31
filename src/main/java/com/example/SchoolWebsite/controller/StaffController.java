package com.example.SchoolWebsite.controller;

import com.example.SchoolWebsite.model.Payment;
import com.example.SchoolWebsite.model.Staff;
import com.example.SchoolWebsite.model.Student;
import com.example.SchoolWebsite.model.VehicleInfo;
import com.example.SchoolWebsite.service.StaffService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"staff".equals(role)) {
            return "redirect:/login";
        }

        Staff staff = staffService.getStaffProfile(userId);
        List<Payment> salaryHistory = staffService.getSalaryHistory(userId);
        Payment lastSalary = (salaryHistory != null && !salaryHistory.isEmpty()) ? salaryHistory.get(0) : null;
        int totalSalaryPayments = (salaryHistory != null) ? salaryHistory.size() : 0;
        double totalSalaryReceived = (salaryHistory != null)
                ? salaryHistory.stream().mapToDouble(Payment::getAmount).sum()
                : 0.0;

        VehicleInfo vehicle = null;
        int busStudentCount = 0;
        boolean isDriver = staff != null && staff.isDriver();
        if (isDriver) {
            vehicle = staffService.getAssignedVehicle(userId);
            if (vehicle != null) {
                List<Student> busStudents = staffService.getBusStudents(vehicle.getVehicleId());
                busStudentCount = (busStudents != null) ? busStudents.size() : 0;
            }
        }

        model.addAttribute("staff", staff);
        model.addAttribute("lastSalary", lastSalary);
        model.addAttribute("totalSalaryPayments", totalSalaryPayments);
        model.addAttribute("totalSalaryReceived", totalSalaryReceived);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("busStudentCount", busStudentCount);
        model.addAttribute("isDriver", isDriver);

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "staff/dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"staff".equals(role)) {
            return "redirect:/login";
        }

        Staff staff = staffService.getStaffProfile(userId);
        VehicleInfo vehicle = null;
        if (staff != null && staff.isDriver()) {
            vehicle = staffService.getAssignedVehicle(userId);
        }

        model.addAttribute("staff", staff);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "staff/profile";
    }

    @GetMapping("/salary")
    public String showSalary(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"staff".equals(role)) {
            return "redirect:/login";
        }

        Staff staff = staffService.getStaffProfile(userId);
        List<Payment> salaryPayments = staffService.getSalaryHistory(userId);

        model.addAttribute("staff", staff);
        model.addAttribute("salaryPayments", salaryPayments);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "staff/salary";
    }

    @GetMapping("/transport")
    public String showTransport(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"staff".equals(role)) {
            return "redirect:/login";
        }

        Staff staff = staffService.getStaffProfile(userId);

        // Strict access check: only Drivers can view vehicle student lists
        if (staff == null || !staff.isDriver()) {
            return "redirect:/staff/profile";
        }

        VehicleInfo vehicle = staffService.getAssignedVehicle(userId);
        List<Student> busStudents = List.of();
        if (vehicle != null) {
            busStudents = staffService.getBusStudents(vehicle.getVehicleId());
        }

        model.addAttribute("staff", staff);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("busStudents", busStudents);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "staff/transport";
    }

    @GetMapping("/salary/slip/{transactionId}")
    public String viewSalarySlip(@PathVariable String transactionId) {
        return "redirect:/admin/salary/slip/" + transactionId;
    }
}
