package com.example.SchoolWebsite.controller;

import com.example.SchoolWebsite.model.Enrollment;
import com.example.SchoolWebsite.model.Guardian;
import com.example.SchoolWebsite.model.House;
import com.example.SchoolWebsite.model.Payment;
import com.example.SchoolWebsite.model.SectionInfo;
import com.example.SchoolWebsite.model.Student;
import com.example.SchoolWebsite.model.VehicleInfo;
import com.example.SchoolWebsite.service.EnrollmentService;
import com.example.SchoolWebsite.service.GuardianService;
import com.example.SchoolWebsite.service.HouseService;
import com.example.SchoolWebsite.service.PaymentService;
import com.example.SchoolWebsite.service.SectionService;
import com.example.SchoolWebsite.service.StudentService;
import com.example.SchoolWebsite.service.TransportService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final GuardianService guardianService;
    private final EnrollmentService enrollmentService;
    private final PaymentService paymentService;
    private final SectionService sectionService;
    private final HouseService houseService;
    private final TransportService transportService;

    public StudentController(StudentService studentService,
                             GuardianService guardianService,
                             EnrollmentService enrollmentService,
                             PaymentService paymentService,
                             SectionService sectionService,
                             HouseService houseService,
                             TransportService transportService) {
        this.studentService = studentService;
        this.guardianService = guardianService;
        this.enrollmentService = enrollmentService;
        this.paymentService = paymentService;
        this.sectionService = sectionService;
        this.houseService = houseService;
        this.transportService = transportService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"student".equals(role)) {
            return "redirect:/login";
        }

        Student student = studentService.getFullProfile(userId);
        if (student == null) {
            student = studentService.findByAdmissionNo(userId);
        }

        SectionInfo section = null;
        if (student != null && student.getStandard() != null && student.getDivision() != null) {
            section = sectionService.getSection(student.getStandard(), student.getDivision());
        }

        List<Enrollment> enrollments = enrollmentService.getEnrolledSubjects(userId);
        int subjectCount = (enrollments != null) ? enrollments.size() : 0;
        double remainingFees = (student != null) ? student.getRemainingFees() : 0.0;

        House house = null;
        if (student != null && student.getHouse() != null) {
            house = houseService.getHouse(student.getHouse());
        }

        VehicleInfo vehicle = null;
        if (student != null && student.getVehicleNo() != null) {
            vehicle = transportService.getVehicleById(student.getVehicleNo());
        }

        model.addAttribute("student", student);
        model.addAttribute("section", section);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("subjectCount", subjectCount);
        model.addAttribute("remainingFees", remainingFees);
        model.addAttribute("house", house);
        model.addAttribute("vehicle", vehicle);

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        // Check if user is logged in as a student
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"student".equals(role)) {
            return "redirect:/login";
        }

        // Fetch full student profile (5-table JOIN)
        Student student = studentService.getFullProfile(userId);
        model.addAttribute("student", student);

        // Fetch guardian info
        List<Guardian> guardians = guardianService.findByStudentId(userId);
        model.addAttribute("guardians", guardians);

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "student/profile";
    }

    @GetMapping("/subjects")
    public String showSubjects(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"student".equals(role)) {
            return "redirect:/login";
        }

        Student student = studentService.findByAdmissionNo(userId);
        List<Enrollment> enrollments = enrollmentService.getEnrolledSubjects(userId);

        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "student/subjects";
    }

    @GetMapping("/fees")
    public String showFees(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session,
            Model model) {
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"student".equals(role)) {
            return "redirect:/login";
        }

        Student student = studentService.findByAdmissionNo(userId);
        List<Payment> payments = paymentService.getStudentPayments(userId, startDate, endDate);

        model.addAttribute("student", student);
        model.addAttribute("payments", payments);
        model.addAttribute("startDate", startDate != null ? startDate : "");
        model.addAttribute("endDate", endDate != null ? endDate : "");
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "student/fees";
    }

    @GetMapping("/fees/receipt/{transactionId}")
    public String showReceipt(
            @PathVariable String transactionId,
            HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || !"student".equals(role)) {
            return "redirect:/login";
        }

        return "redirect:/admin/fees/receipt/" + transactionId;
    }
}
