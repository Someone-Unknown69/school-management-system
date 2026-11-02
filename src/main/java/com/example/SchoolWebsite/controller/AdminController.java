package com.example.SchoolWebsite.controller;

import com.example.SchoolWebsite.model.Enrollment;
import com.example.SchoolWebsite.model.FeeReceipt;
import com.example.SchoolWebsite.model.Guardian;
import com.example.SchoolWebsite.model.House;
import com.example.SchoolWebsite.model.Payment;
import com.example.SchoolWebsite.model.SalarySlip;
import com.example.SchoolWebsite.model.SectionInfo;
import com.example.SchoolWebsite.model.Staff;
import com.example.SchoolWebsite.model.Student;
import com.example.SchoolWebsite.model.Subject;
import com.example.SchoolWebsite.model.Teacher;
import com.example.SchoolWebsite.model.VehicleInfo;
import com.example.SchoolWebsite.service.EnrollmentService;
import com.example.SchoolWebsite.service.GuardianService;
import com.example.SchoolWebsite.service.HouseService;
import com.example.SchoolWebsite.service.PaymentService;
import com.example.SchoolWebsite.service.SectionService;
import com.example.SchoolWebsite.service.StaffService;
import com.example.SchoolWebsite.service.StudentService;
import com.example.SchoolWebsite.service.SubjectService;
import com.example.SchoolWebsite.service.TeacherService;
import com.example.SchoolWebsite.service.TransportService;
import com.example.SchoolWebsite.repository.StatsRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final StaffService staffService;
    private final GuardianService guardianService;
    private final PaymentService paymentService;
    private final TransportService transportService;
    private final SubjectService subjectService;
    private final SectionService sectionService;
    private final EnrollmentService enrollmentService;
    private final HouseService houseService;
    private final StatsRepository statsRepository;

    public AdminController(StudentService studentService,
                           TeacherService teacherService,
                           StaffService staffService,
                           GuardianService guardianService,
                           PaymentService paymentService,
                           TransportService transportService,
                           SubjectService subjectService,
                           SectionService sectionService,
                           EnrollmentService enrollmentService,
                           HouseService houseService,
                           StatsRepository statsRepository) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.staffService = staffService;
        this.guardianService = guardianService;
        this.paymentService = paymentService;
        this.transportService = transportService;
        this.subjectService = subjectService;
        this.sectionService = sectionService;
        this.enrollmentService = enrollmentService;
        this.houseService = houseService;
        this.statsRepository = statsRepository;
    }

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        String userId = (String) session.getAttribute("userId");
        return "admin".equals(role) && userId != null;
    }

    // ==========================================
    // ADMIN DASHBOARD
    // ==========================================

    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // 1. Core entity counts
        long totalStudents = statsRepository.countStudents();
        long totalTeachers = statsRepository.countTeachers();
        long totalStaff = statsRepository.countStaff();
        long totalHouses = statsRepository.countHouses();

        // 2. Financial totals
        Map<String, Object> txnStats = paymentService.getTransactionStats();
        double totalFees = txnStats.get("totalFee") != null ? ((Number) txnStats.get("totalFee")).doubleValue() : 0.0;
        double totalSalaries = txnStats.get("totalSalary") != null ? ((Number) txnStats.get("totalSalary")).doubleValue() : 0.0;
        double totalDues = statsRepository.getTotalFeeDues();

        // 3. Transport metrics
        List<VehicleInfo> vehicles = transportService.getAllVehicles();
        int totalVehicles = vehicles != null ? vehicles.size() : 0;
        int totalCapacity = vehicles != null ? vehicles.stream().mapToInt(VehicleInfo::getCapacity).sum() : 0;
        int totalAssigned = vehicles != null ? vehicles.stream().mapToInt(VehicleInfo::getAssignedStudents).sum() : 0;
        double capacityUtilization = totalCapacity > 0 ? (totalAssigned * 100.0 / totalCapacity) : 0.0;

        // 4. Students per class distribution (for Chart.js)
        List<Map<String, Object>> classCounts = statsRepository.getStudentCountByStandard();
        List<String> classLabels = new ArrayList<>();
        List<Number> classData = new ArrayList<>();
        if (classCounts != null) {
            for (Map<String, Object> row : classCounts) {
                classLabels.add("Class " + row.get("Standard"));
                classData.add((Number) row.get("count"));
            }
        }

        // 5. Monthly fees collected (for Chart.js)
        List<Map<String, Object>> monthlyFees = statsRepository.getMonthlyFeeCollections();
        List<String> feeLabels = new ArrayList<>();
        List<Number> feeData = new ArrayList<>();
        if (monthlyFees != null) {
            for (Map<String, Object> row : monthlyFees) {
                feeLabels.add((String) row.get("monthLabel"));
                feeData.add((Number) row.get("total"));
            }
        }

        // 6. Recent payments (last 6 transactions)
        List<Payment> recentPayments = paymentService.getFilteredTransactions(null, null, null, null, null, 1, 6);

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalTeachers", totalTeachers);
        model.addAttribute("totalStaff", totalStaff);
        model.addAttribute("totalHouses", totalHouses);
        model.addAttribute("totalFees", totalFees);
        model.addAttribute("totalSalaries", totalSalaries);
        model.addAttribute("totalDues", totalDues);
        model.addAttribute("totalVehicles", totalVehicles);
        model.addAttribute("totalCapacity", totalCapacity);
        model.addAttribute("totalAssigned", totalAssigned);
        model.addAttribute("capacityUtilization", Math.round(capacityUtilization * 10.0) / 10.0);

        model.addAttribute("classLabels", classLabels);
        model.addAttribute("classData", classData);
        model.addAttribute("feeLabels", feeLabels);
        model.addAttribute("feeData", feeData);

        model.addAttribute("recentPayments", recentPayments);

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/dashboard";
    }

    // ==========================================
    // MANAGE STUDENTS
    // ==========================================

    @GetMapping("/students")
    public String listStudents(
            @RequestParam(required = false) String standard,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) String search,
            HttpSession session,
            Model model) {

        // checks for admin 
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<Student> students = studentService.searchAndFilter(standard, division, search);

        model.addAttribute("students", students);
        model.addAttribute("standard", standard != null ? standard : "");
        model.addAttribute("division", division != null ? division : "");
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("availableClasses", studentService.getAvailableClasses());
        model.addAttribute("availableDivisions", studentService.getAvailableDivisions());
        model.addAttribute("availableSections", studentService.getAvailableSections());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/students";
    }

    @GetMapping("/students/new")
    public String newStudentForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Student student = new Student();
        student.setRemainingFees(0.0);
        student.setAge(15);
        student.setDateOfAdmission(java.time.LocalDate.now().toString());

        model.addAttribute("student", student);
        model.addAttribute("mode", "create");
        model.addAttribute("availableClasses", studentService.getAvailableClasses());
        model.addAttribute("availableDivisions", studentService.getAvailableDivisions());
        model.addAttribute("availableSections", studentService.getAvailableSections());
        model.addAttribute("availableHouses", studentService.getAvailableHouses());
        model.addAttribute("availableVehicles", studentService.getAvailableVehicles());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/student-form";
    }

    @PostMapping("/students/new")
    public String createStudent(
            @ModelAttribute Student student,
            @RequestParam(required = false) String guardianRelationship,
            @RequestParam(required = false) String guardianFirstName,
            @RequestParam(required = false) String guardianMiddleName,
            @RequestParam(required = false) String guardianLastName,
            @RequestParam(required = false) String guardianOccupation,
            @RequestParam(required = false) String guardianPhone,
            @RequestParam(required = false) String guardianEmail,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (!isValidPhone(student.getContactNumber())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student contact number must be a valid 10-digit number.");
            return "redirect:/admin/students/new";
        }
        if (!isValidPhone(guardianPhone)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Guardian contact number must be a valid 10-digit number.");
            return "redirect:/admin/students/new";
        }
        if (!isValidEmail(guardianEmail)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Guardian email address is invalid.");
            return "redirect:/admin/students/new";
        }

        try {
            studentService.saveStudent(student, guardianRelationship, guardianFirstName,
                    guardianMiddleName, guardianLastName, guardianOccupation, guardianPhone, guardianEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Student " + student.getAdmissionNo() + " created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create student: " + e.getMessage());
        }

        return "redirect:/admin/students";
    }

    @GetMapping("/students/edit/{admissionNo}")
    public String editStudentForm(@PathVariable String admissionNo, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Student student = studentService.findByAdmissionNo(admissionNo);
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found with Admission No: " + admissionNo);
            return "redirect:/admin/students";
        }

        model.addAttribute("student", student);
        model.addAttribute("mode", "edit");

        List<Guardian> guardians = guardianService.findByStudentId(admissionNo);
        model.addAttribute("guardian", guardians.isEmpty() ? null : guardians.get(0));

        model.addAttribute("availableClasses", studentService.getAvailableClasses());
        model.addAttribute("availableDivisions", studentService.getAvailableDivisions());
        model.addAttribute("availableSections", studentService.getAvailableSections());
        model.addAttribute("availableHouses", studentService.getAvailableHouses());
        model.addAttribute("availableVehicles", studentService.getAvailableVehicles());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/student-form";
    }

    @PostMapping("/students/edit/{admissionNo}")
    public String updateStudent(
            @PathVariable String admissionNo,
            @ModelAttribute Student student,
            @RequestParam(required = false) String guardianRelationship,
            @RequestParam(required = false) String guardianFirstName,
            @RequestParam(required = false) String guardianMiddleName,
            @RequestParam(required = false) String guardianLastName,
            @RequestParam(required = false) String guardianOccupation,
            @RequestParam(required = false) String guardianPhone,
            @RequestParam(required = false) String guardianEmail,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (!isValidPhone(student.getContactNumber())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student contact number must be a valid 10-digit number.");
            return "redirect:/admin/students/edit/" + admissionNo;
        }
        if (!isValidPhone(guardianPhone)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Guardian contact number must be a valid 10-digit number.");
            return "redirect:/admin/students/edit/" + admissionNo;
        }
        if (!isValidEmail(guardianEmail)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Guardian email address is invalid.");
            return "redirect:/admin/students/edit/" + admissionNo;
        }

        student.setAdmissionNo(admissionNo);
        try {
            studentService.updateStudent(student, guardianRelationship, guardianFirstName,
                    guardianMiddleName, guardianLastName, guardianOccupation, guardianPhone, guardianEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Student " + admissionNo + " updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update student: " + e.getMessage());
        }

        return "redirect:/admin/students";
    }

    @PostMapping("/students/delete/{admissionNo}")
    public String deleteStudent(@PathVariable String admissionNo, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            studentService.deleteStudent(admissionNo);
            redirectAttributes.addFlashAttribute("successMessage", "Student " + admissionNo + " deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete student: " + e.getMessage());
        }

        return "redirect:/admin/students";
    }

    // ==========================================
    // MANAGE FACULTY (TEACHERS & STAFF)
    // ==========================================

    @GetMapping("/faculty")
    public String listFaculty(
            @RequestParam(defaultValue = "teachers") String tab,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<Teacher> teachers = teacherService.getAllTeachers();
        List<Staff> staffList = staffService.getAllStaff();

        model.addAttribute("teachers", teachers);
        model.addAttribute("staffList", staffList);
        model.addAttribute("activeTab", tab);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/faculty";
    }

    // --- Teacher CRUD ---

    @GetMapping("/faculty/teacher/new")
    public String newTeacherForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Teacher teacher = new Teacher();
        teacher.setDateOfJoining(java.time.LocalDate.now().toString());

        model.addAttribute("teacher", teacher);
        model.addAttribute("mode", "create");
        model.addAttribute("qualificationsInput", "");
        model.addAttribute("specializationsInput", "");
        model.addAttribute("availableHouses", studentService.getAvailableHouses());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/teacher-form";
    }

    @PostMapping("/faculty/teacher/new")
    public String createTeacher(
            @ModelAttribute Teacher teacher,
            @RequestParam(required = false) String qualificationsInput,
            @RequestParam(required = false) String specializationsInput,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (!isValidPhone(teacher.getPhoneNo())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Teacher phone number must be a valid 10-digit number.");
            return "redirect:/admin/faculty/teacher/new";
        }
        if (!isValidEmail(teacher.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Teacher email address is invalid.");
            return "redirect:/admin/faculty/teacher/new";
        }

        try {
            List<String> quals = parseCommaList(qualificationsInput);
            List<String> specs = parseCommaList(specializationsInput);
            teacherService.saveTeacher(teacher, quals, specs);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher " + teacher.getEmployeeId() + " created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create teacher: " + e.getMessage());
        }

        return "redirect:/admin/faculty?tab=teachers";
    }

    @GetMapping("/faculty/teacher/edit/{employeeId}")
    public String editTeacherForm(@PathVariable String employeeId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Teacher teacher = teacherService.getFullProfile(employeeId);
        if (teacher == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Teacher not found: " + employeeId);
            return "redirect:/admin/faculty?tab=teachers";
        }

        String quals = teacher.getQualifications() != null ? String.join(", ", teacher.getQualifications()) : "";
        String specs = teacher.getSpecializations() != null ? String.join(", ", teacher.getSpecializations()) : "";

        model.addAttribute("teacher", teacher);
        model.addAttribute("mode", "edit");
        model.addAttribute("qualificationsInput", quals);
        model.addAttribute("specializationsInput", specs);
        model.addAttribute("availableHouses", studentService.getAvailableHouses());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/teacher-form";
    }

    @PostMapping("/faculty/teacher/edit/{employeeId}")
    public String updateTeacher(
            @PathVariable String employeeId,
            @ModelAttribute Teacher teacher,
            @RequestParam(required = false) String qualificationsInput,
            @RequestParam(required = false) String specializationsInput,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (!isValidPhone(teacher.getPhoneNo())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Teacher phone number must be a valid 10-digit number.");
            return "redirect:/admin/faculty/teacher/edit/" + employeeId;
        }
        if (!isValidEmail(teacher.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Teacher email address is invalid.");
            return "redirect:/admin/faculty/teacher/edit/" + employeeId;
        }

        teacher.setEmployeeId(employeeId);
        try {
            List<String> quals = parseCommaList(qualificationsInput);
            List<String> specs = parseCommaList(specializationsInput);
            teacherService.updateTeacher(teacher, quals, specs);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher " + employeeId + " updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update teacher: " + e.getMessage());
        }

        return "redirect:/admin/faculty?tab=teachers";
    }

    @PostMapping("/faculty/teacher/delete/{employeeId}")
    public String deleteTeacher(@PathVariable String employeeId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            teacherService.deleteTeacher(employeeId);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher " + employeeId + " deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete teacher: " + e.getMessage());
        }

        return "redirect:/admin/faculty?tab=teachers";
    }

    // --- Staff CRUD ---

    @GetMapping("/faculty/staff/new")
    public String newStaffForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Staff staff = new Staff();
        staff.setDateOfJoining(java.time.LocalDate.now().toString());

        model.addAttribute("staff", staff);
        model.addAttribute("roles", staffService.getAllRoles());
        model.addAttribute("mode", "create");
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/staff-form";
    }

    @PostMapping("/faculty/staff/new")
    public String createStaff(@ModelAttribute Staff staff, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            staffService.saveStaff(staff);
            redirectAttributes.addFlashAttribute("successMessage", "Staff member " + staff.getEmployeeId() + " created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create staff member: " + e.getMessage());
        }

        return "redirect:/admin/faculty?tab=staff";
    }

    @GetMapping("/faculty/staff/edit/{employeeId}")
    public String editStaffForm(@PathVariable String employeeId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Staff staff = staffService.getStaffProfile(employeeId);
        if (staff == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Staff member not found: " + employeeId);
            return "redirect:/admin/faculty?tab=staff";
        }

        model.addAttribute("staff", staff);
        model.addAttribute("roles", staffService.getAllRoles());
        model.addAttribute("mode", "edit");
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/staff-form";
    }

    @PostMapping("/faculty/staff/edit/{employeeId}")
    public String updateStaff(@PathVariable String employeeId, @ModelAttribute Staff staff, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        staff.setEmployeeId(employeeId);
        try {
            staffService.updateStaff(staff);
            redirectAttributes.addFlashAttribute("successMessage", "Staff member " + employeeId + " updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update staff member: " + e.getMessage());
        }

        return "redirect:/admin/faculty?tab=staff";
    }

    @PostMapping("/faculty/staff/delete/{employeeId}")
    public String deleteStaff(@PathVariable String employeeId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            staffService.deleteStaff(employeeId);
            redirectAttributes.addFlashAttribute("successMessage", "Staff member " + employeeId + " deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete staff member: " + e.getMessage());
        }

        return "redirect:/admin/faculty?tab=staff";
    }

    private List<String> parseCommaList(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true;
        }
        return phone.trim().matches("^[0-9]{10}$");
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        return email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // ==========================================
    // FEE MANAGEMENT (DUES, COLLECTION, RECEIPTS)
    // ==========================================

    @GetMapping("/fees")
    public String feesRoot() {
        return "redirect:/admin/fees/dues";
    }

    @GetMapping("/fees/dues")
    public String listFeeDues(
            @RequestParam(required = false) String standard,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) String search,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<Student> students = studentService.getStudentsWithDues(standard, division, search);
        double totalOutstanding = studentService.getTotalOutstandingDues(standard, division, search);
        double totalSchoolDues = studentService.getTotalOutstandingDues(null, null, null);

        model.addAttribute("students", students);
        model.addAttribute("totalOutstanding", totalOutstanding);
        model.addAttribute("dueStudentsCount", students.size());
        model.addAttribute("totalSchoolDues", totalSchoolDues);
        model.addAttribute("availableClasses", studentService.getAvailableClasses());
        model.addAttribute("availableDivisions", studentService.getAvailableDivisions());
        model.addAttribute("selectedStandard", standard != null ? standard : "");
        model.addAttribute("selectedDivision", division != null ? division : "");
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/fee-dues";
    }

    @GetMapping("/fees/record")
    public String recordFeePaymentForm(
            @RequestParam(required = false) String admissionNo,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (admissionNo != null) {
            String clean = "";
            for (String part : admissionNo.split(",")) {
                if (!part.trim().isEmpty()) {
                    clean = part.trim();
                    break;
                }
            }
            admissionNo = clean;
        }

        Student selectedStudent = null;
        if (admissionNo != null && !admissionNo.trim().isEmpty()) {
            selectedStudent = studentService.findByAdmissionNo(admissionNo.trim());
        }

        List<Student> studentsWithDues = studentService.getAllStudentsWithDues();
        List<String> paymentModes = Arrays.asList("UPI", "Cash", "NetBanking", "Cheque", "Card");

        model.addAttribute("selectedStudent", selectedStudent);
        model.addAttribute("admissionNo", admissionNo != null ? admissionNo.trim() : "");
        model.addAttribute("studentsWithDues", studentsWithDues);
        model.addAttribute("paymentModes", paymentModes);
        model.addAttribute("todayDate", LocalDate.now().toString());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/fee-record";
    }

    @PostMapping("/fees/record")
    public String handleRecordFeePayment(
            @RequestParam(required = false) String admissionNo,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) String paymentDate,
            @RequestParam(required = false) String paymentMode,
            @RequestParam(required = false) String remitterName,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (admissionNo != null) {
            for (String part : admissionNo.split(",")) {
                if (!part.trim().isEmpty()) {
                    admissionNo = part.trim();
                    break;
                }
            }
        }

        if (admissionNo == null || admissionNo.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select or enter a student admission number.");
            return "redirect:/admin/fees/record";
        }

        String trimmedAdmission = admissionNo.trim();
        Student student = studentService.findByAdmissionNo(trimmedAdmission);
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found with Admission No: " + trimmedAdmission);
            return "redirect:/admin/fees/record";
        }

        if (amount == null || amount <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment amount must be greater than zero.");
            return "redirect:/admin/fees/record?admissionNo=" + trimmedAdmission;
        }

        if (amount > student.getRemainingFees()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("Payment amount (₹%.2f) cannot exceed student's remaining fees dues (₹%.2f).", amount, student.getRemainingFees()));
            return "redirect:/admin/fees/record?admissionNo=" + trimmedAdmission;
        }

        try {
            String transactionId = paymentService.recordFeePayment(trimmedAdmission, amount, paymentDate, paymentMode, remitterName);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Payment of ₹%.2f recorded successfully for %s (%s). Receipt generated: %s",
                            amount, student.getFullName(), trimmedAdmission, transactionId));
            return "redirect:/admin/fees/receipt/" + transactionId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to record payment: " + e.getMessage());
            return "redirect:/admin/fees/record?admissionNo=" + trimmedAdmission;
        }
    }

    @GetMapping("/fees/receipt/{transactionId}")
    public String showFeeReceipt(
            @PathVariable String transactionId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String role = (String) session.getAttribute("role");
        String userId = (String) session.getAttribute("userId");
        if (role == null || userId == null) {
            return "redirect:/login";
        }

        FeeReceipt receipt = paymentService.getFeeReceipt(transactionId);
        if (receipt == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fee receipt not found for transaction: " + transactionId);
            return "student".equals(role) ? "redirect:/student/fees" : "redirect:/admin/fees/dues";
        }

        // If accessed by a student, ensure they only view their own receipt
        if ("student".equals(role) && !receipt.getStudentId().equalsIgnoreCase(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access to fee receipt.");
            return "redirect:/student/fees";
        }

        model.addAttribute("receipt", receipt);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "admin/fee-receipt";
    }

    // ==========================================
    // SALARY DISBURSEMENT
    // ==========================================

    @GetMapping("/salary")
    public String salaryRoot() {
        return "redirect:/admin/salary/pay";
    }

    @GetMapping("/salary/pay")
    public String showPaySalaryForm(
            @RequestParam(required = false, defaultValue = "Teacher") String employeeType,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        int currentMonth = (month != null && month >= 1 && month <= 12) ? month : LocalDate.now().getMonthValue();
        int currentYear = (year != null && year >= 2000) ? year : LocalDate.now().getYear();

        List<Teacher> teachers = teacherService.getAllTeachers();
        List<Staff> staffList = staffService.getAllStaff();

        Object selectedEmployee = null;
        boolean isAlreadyPaid = false;
        double defaultSalary = 0.0;
        String employeeName = "";

        if (employeeId != null && !employeeId.trim().isEmpty()) {
            String trimmedId = employeeId.trim();
            if ("Teacher".equalsIgnoreCase(employeeType)) {
                Teacher t = teacherService.getFullProfile(trimmedId);
                if (t != null) {
                    selectedEmployee = t;
                    defaultSalary = t.getSalary();
                    employeeName = t.getFullName();
                }
            } else {
                Staff s = staffService.getStaffProfile(trimmedId);
                if (s != null) {
                    selectedEmployee = s;
                    defaultSalary = s.getSalary();
                    employeeName = s.getFullName();
                }
            }

            if (selectedEmployee != null) {
                isAlreadyPaid = paymentService.isSalaryPaid(employeeType, trimmedId, currentYear, currentMonth);
            }
        }

        // Available months list for selector
        List<Map<String, Object>> months = Arrays.stream(Month.values())
                .map(m -> Map.of(
                        "value", (Object) m.getValue(),
                        "name", (Object) m.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                ))
                .collect(Collectors.toList());

        List<Integer> years = List.of(currentYear - 1, currentYear, currentYear + 1);
        List<String> paymentModes = List.of("Bank Transfer", "Cheque", "Cash", "UPI", "Card");

        String monthName = Month.of(currentMonth).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        model.addAttribute("employeeType", employeeType);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("selectedEmployee", selectedEmployee);
        model.addAttribute("employeeName", employeeName);
        model.addAttribute("defaultSalary", defaultSalary);
        model.addAttribute("isAlreadyPaid", isAlreadyPaid);
        model.addAttribute("selectedMonth", currentMonth);
        model.addAttribute("selectedYear", currentYear);
        model.addAttribute("monthName", monthName);
        model.addAttribute("paymentDate", LocalDate.now().toString());
        model.addAttribute("teachers", teachers);
        model.addAttribute("staffList", staffList);
        model.addAttribute("months", months);
        model.addAttribute("years", years);
        model.addAttribute("paymentModes", paymentModes);

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", session.getAttribute("role"));

        return "admin/salary-pay";
    }

    @PostMapping("/salary/pay")
    public String processPaySalary(
            @RequestParam String employeeType,
            @RequestParam String employeeId,
            @RequestParam(required = false) Double amount,
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(required = false) String paymentDate,
            @RequestParam(required = false, defaultValue = "Bank Transfer") String paymentMode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            String txnId = paymentService.recordSalaryPayment(employeeType, employeeId, amount, year, month, paymentDate, paymentMode);
            redirectAttributes.addFlashAttribute("successMessage", "Salary disbursed successfully! Transaction ID: " + txnId);
            return "redirect:/admin/salary/slip/" + txnId;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/salary/pay?employeeType=" + employeeType + "&employeeId=" + employeeId + "&month=" + month + "&year=" + year;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error recording salary payment: " + ex.getMessage());
            return "redirect:/admin/salary/pay?employeeType=" + employeeType + "&employeeId=" + employeeId + "&month=" + month + "&year=" + year;
        }
    }

    @PostMapping("/salary/bulk-pay")
    public String processBulkSalary(
            @RequestParam(defaultValue = "all") String targetGroup,
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(defaultValue = "Bank Transfer") String paymentMode,
            @RequestParam(required = false) String paymentDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> result = paymentService.bulkDisburseSalaries(targetGroup, year, month, paymentMode, paymentDate);
            int paidCount = (int) result.get("paidCount");
            int skippedCount = (int) result.get("skippedCount");
            double total = (double) result.get("totalAmount");
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            if (paidCount == 0) {
                redirectAttributes.addFlashAttribute("infoMessage",
                        "All eligible employees have already received their salary for " + monthName + " " + year + ". (Skipped " + skippedCount + ")");
            } else {
                redirectAttributes.addFlashAttribute("successMessage",
                        String.format("Successfully disbursed salary to %d employee(s) totaling ₹%,.2f for %s %d. (%d already paid and skipped)",
                                paidCount, total, monthName, year, skippedCount));
            }
            return "redirect:/admin/transactions?type=Salary";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bulk salary disbursement failed: " + ex.getMessage());
            return "redirect:/admin/salary/pay?month=" + month + "&year=" + year;
        }
    }

    @GetMapping("/salary/slip/{transactionId}")
    public String showSalarySlip(
            @PathVariable String transactionId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String role = (String) session.getAttribute("role");
        String userId = (String) session.getAttribute("userId");
        if (role == null || userId == null) {
            return "redirect:/login";
        }

        SalarySlip slip = paymentService.getSalarySlip(transactionId);
        if (slip == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Salary slip not found for transaction: " + transactionId);
            if ("teacher".equals(role)) return "redirect:/teacher/salary";
            if ("staff".equals(role)) return "redirect:/staff/salary";
            return "redirect:/admin/transactions?type=Salary";
        }

        // If teacher or staff is logged in, ensure they can only view their own slip
        if (("teacher".equals(role) || "staff".equals(role)) && !slip.getEmployeeId().equalsIgnoreCase(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access to salary slip.");
            return "teacher".equals(role) ? "redirect:/teacher/salary" : "redirect:/staff/salary";
        }

        model.addAttribute("slip", slip);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", userId);
        model.addAttribute("loggedInRole", role);

        return "admin/salary-slip";
    }

    // ==========================================
    // TRANSACTIONS
    // ==========================================

    @GetMapping("/transactions")
    public String listTransactions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        int pageSize = 25;
        int currentPage = Math.max(1, page);
        int totalCount = paymentService.countFilteredTransactions(type, mode, startDate, endDate, search);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        List<Payment> transactions = paymentService.getFilteredTransactions(type, mode, startDate, endDate, search, currentPage, pageSize);
        Map<String, Object> stats = paymentService.getTransactionStats();
        Map<String, Object> financialSummary = paymentService.getFinancialSummary(startDate, endDate);
        List<Map<String, Object>> monthlyBreakdown = paymentService.getMonthlyFinancialBreakdown(startDate, endDate);

        model.addAttribute("transactions", transactions);
        model.addAttribute("stats", stats);
        model.addAttribute("financialSummary", financialSummary);
        model.addAttribute("monthlyBreakdown", monthlyBreakdown);
        model.addAttribute("selectedType", type != null ? type : "");
        model.addAttribute("selectedMode", mode != null ? mode : "");
        model.addAttribute("startDate", startDate != null ? startDate : "");
        model.addAttribute("endDate", endDate != null ? endDate : "");
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("availableTypes", paymentService.getAvailablePaymentTypes());
        model.addAttribute("availableModes", paymentService.getAvailablePaymentModes());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/transactions";
    }

    @GetMapping("/transactions/export")
    public void exportTransactionsCsv(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String search,
            HttpSession session,
            HttpServletResponse response) throws Exception {

        if (!isAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        List<Payment> transactions = paymentService.getAllFilteredTransactions(type, mode, startDate, endDate, search);

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions_ledger.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("Transaction ID,Date,Type,Category,Payment Mode,Remitter (Sender),Beneficiary (Receiver),Amount (INR)");

        for (Payment p : transactions) {
            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f%n",
                    p.getTransactionId() != null ? p.getTransactionId() : "",
                    p.getDateOfPayment() != null ? p.getDateOfPayment() : "",
                    p.getPaymentType() != null ? p.getPaymentType() : "",
                    p.getSubType() != null ? p.getSubType() : "",
                    p.getPaymentMode() != null ? p.getPaymentMode() : "",
                    p.getSender() != null ? p.getSender().replace("\"", "\"\"") : "",
                    p.getReceiver() != null ? p.getReceiver().replace("\"", "\"\"") : "",
                    p.getAmount());
        }
        writer.flush();
    }

    @GetMapping("/transactions/statement")
    public String showFinancialStatement(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Map<String, Object> financialSummary = paymentService.getFinancialSummary(startDate, endDate);
        List<Map<String, Object>> monthlyBreakdown = paymentService.getMonthlyFinancialBreakdown(startDate, endDate);

        model.addAttribute("financialSummary", financialSummary);
        model.addAttribute("monthlyBreakdown", monthlyBreakdown);
        model.addAttribute("startDate", startDate != null ? startDate : "");
        model.addAttribute("endDate", endDate != null ? endDate : "");
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/financial-statement";
    }

    // ==========================================
    // TRANSPORT
    // ==========================================

    @GetMapping("/transport")
    public String listTransport(
            @RequestParam(required = false) String selectedVehicle,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<VehicleInfo> vehicles = transportService.getAllVehicles();
        Map<String, Object> stats = transportService.getTransportStats();

        VehicleInfo activeVehicle = null;
        List<Student> passengers = Collections.emptyList();

        if (selectedVehicle != null && !selectedVehicle.trim().isEmpty()) {
            activeVehicle = transportService.getVehicleById(selectedVehicle);
            if (activeVehicle != null) {
                passengers = transportService.getStudentsByVehicleId(selectedVehicle);
            }
        } else if (!vehicles.isEmpty()) {
            activeVehicle = vehicles.get(0);
            passengers = transportService.getStudentsByVehicleId(activeVehicle.getVehicleId());
        }

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("stats", stats);
        model.addAttribute("activeVehicle", activeVehicle);
        model.addAttribute("passengers", passengers);
        model.addAttribute("unassignedStudents", transportService.getUnassignedStudents());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/transport";
    }

    @GetMapping("/transport/vehicle/new")
    public String newVehicleForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        VehicleInfo vehicle = new VehicleInfo();
        vehicle.setCapacity(40);

        model.addAttribute("vehicle", vehicle);
        model.addAttribute("mode", "create");
        model.addAttribute("drivers", transportService.getAllDrivers());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/vehicle-form";
    }

    @PostMapping("/transport/vehicle/new")
    public String createVehicle(
            @ModelAttribute VehicleInfo vehicle,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            transportService.saveVehicle(vehicle);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle " + vehicle.getVehicleId() + " created successfully!");
            return "redirect:/admin/transport?selectedVehicle=" + vehicle.getVehicleId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create vehicle: " + e.getMessage());
            return "redirect:/admin/transport";
        }
    }

    @GetMapping("/transport/vehicle/edit/{vehicleId}")
    public String editVehicleForm(@PathVariable String vehicleId, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        VehicleInfo vehicle = transportService.getVehicleById(vehicleId);
        if (vehicle == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vehicle not found: " + vehicleId);
            return "redirect:/admin/transport";
        }

        model.addAttribute("vehicle", vehicle);
        model.addAttribute("mode", "edit");
        model.addAttribute("drivers", transportService.getAllDrivers());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/vehicle-form";
    }

    @PostMapping("/transport/vehicle/edit/{vehicleId}")
    public String updateVehicle(
            @PathVariable String vehicleId,
            @ModelAttribute VehicleInfo vehicle,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        vehicle.setVehicleId(vehicleId);
        try {
            transportService.updateVehicle(vehicle);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle " + vehicleId + " updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update vehicle: " + e.getMessage());
        }

        return "redirect:/admin/transport?selectedVehicle=" + vehicleId;
    }

    @PostMapping("/transport/vehicle/delete/{vehicleId}")
    public String deleteVehicle(@PathVariable String vehicleId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            transportService.deleteVehicle(vehicleId);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle " + vehicleId + " deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete vehicle: " + e.getMessage());
        }

        return "redirect:/admin/transport";
    }

    @PostMapping("/transport/remove-passenger")
    public String removePassenger(
            @RequestParam String admissionNo,
            @RequestParam String vehicleId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            transportService.removeStudentFromVehicle(admissionNo);
            redirectAttributes.addFlashAttribute("successMessage", "Student " + admissionNo + " removed from vehicle " + vehicleId + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove passenger: " + e.getMessage());
        }

        return "redirect:/admin/transport?selectedVehicle=" + vehicleId;
    }

    @PostMapping("/transport/add-passenger")
    public String addPassenger(
            @RequestParam(required = false) String admissionNo,
            @RequestParam(required = false) String manualAdmissionNo,
            @RequestParam String vehicleId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        String targetAdmissionNo = (manualAdmissionNo != null && !manualAdmissionNo.trim().isEmpty())
                ? manualAdmissionNo.trim()
                : (admissionNo != null ? admissionNo.trim() : "");

        if (targetAdmissionNo.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select or enter a student admission number.");
            return "redirect:/admin/transport?selectedVehicle=" + vehicleId + "#passengers";
        }

        Student student = studentService.findByAdmissionNo(targetAdmissionNo);
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student with admission number '" + targetAdmissionNo + "' not found.");
            return "redirect:/admin/transport?selectedVehicle=" + vehicleId + "#passengers";
        }

        VehicleInfo vehicle = transportService.getVehicleById(vehicleId);
        if (vehicle == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vehicle '" + vehicleId + "' not found.");
            return "redirect:/admin/transport";
        }

        if (vehicle.getAssignedStudents() >= vehicle.getCapacity()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot add student: Vehicle " + vehicleId + " has reached maximum capacity (" + vehicle.getCapacity() + " seats).");
            return "redirect:/admin/transport?selectedVehicle=" + vehicleId + "#passengers";
        }

        try {
            transportService.assignStudentToVehicle(targetAdmissionNo, vehicleId);
            String fullName = student.getFirstName() + (student.getLastName() != null ? " " + student.getLastName() : "");
            redirectAttributes.addFlashAttribute("successMessage", "Student " + fullName + " (" + targetAdmissionNo + ") added to vehicle " + vehicleId + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to assign student to vehicle: " + e.getMessage());
        }

        return "redirect:/admin/transport?selectedVehicle=" + vehicleId + "#passengers";
    }

    // ==========================================
    // STEP 6: SUBJECT MANAGEMENT
    // ==========================================

    @GetMapping("/subjects")
    public String listSubjects(
            @RequestParam(required = false, defaultValue = "all") String classFilter,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<Subject> subjects = subjectService.getAllSubjects(classFilter);
        List<String> availableClasses = subjectService.getAvailableClasses();

        model.addAttribute("subjects", subjects);
        model.addAttribute("classFilter", classFilter);
        model.addAttribute("availableClasses", availableClasses);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/subjects";
    }

    @GetMapping("/subjects/new")
    public String newSubjectForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Subject subject = new Subject();
        subject.setPeriodsPerWeek(5);

        model.addAttribute("subject", subject);
        model.addAttribute("referenceBooksText", "");
        model.addAttribute("mode", "add");
        model.addAttribute("availableClasses", subjectService.getAvailableClasses());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/subject-form";
    }

    @PostMapping("/subjects/new")
    public String createSubject(
            @ModelAttribute Subject subject,
            @RequestParam(required = false) String referenceBooksText,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<String> books = parseReferenceBooks(referenceBooksText);
        try {
            subjectService.createSubject(subject, books);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Subject " + subject.getSubjectCode() + " (" + subject.getSubjectName() + ") created successfully!");
            return "redirect:/admin/subjects";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/subjects/new";
        }
    }

    @GetMapping("/subjects/edit/{code}")
    public String editSubjectForm(@PathVariable String code, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Subject subject = subjectService.getSubject(code);
        if (subject == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Subject not found: " + code);
            return "redirect:/admin/subjects";
        }

        String booksText = String.join("\n", subject.getReferenceBooks());

        model.addAttribute("subject", subject);
        model.addAttribute("referenceBooksText", booksText);
        model.addAttribute("mode", "edit");
        model.addAttribute("availableClasses", subjectService.getAvailableClasses());
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/subject-form";
    }

    @PostMapping("/subjects/edit/{code}")
    public String updateSubject(
            @PathVariable String code,
            @ModelAttribute Subject subject,
            @RequestParam(required = false) String referenceBooksText,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        subject.setSubjectCode(code);
        List<String> books = parseReferenceBooks(referenceBooksText);
        try {
            subjectService.updateSubject(subject, books);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Subject " + code + " updated successfully!");
            return "redirect:/admin/subjects";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/subjects/edit/" + code;
        }
    }

    @GetMapping("/subjects/{code}")
    public String viewSubjectDetail(@PathVariable String code, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Subject subject = subjectService.getSubject(code);
        if (subject == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Subject not found: " + code);
            return "redirect:/admin/subjects";
        }

        List<Teacher> unassignedTeachers = subjectService.getUnassignedTeachers(code);

        model.addAttribute("subject", subject);
        model.addAttribute("unassignedTeachers", unassignedTeachers);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/subject-detail";
    }

    @PostMapping("/subjects/{code}/teachers/add")
    public String addTeacherToSubject(
            @PathVariable String code,
            @RequestParam String teacherId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            subjectService.addTeacherToSubject(code, teacherId);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher " + teacherId + " assigned to subject " + code + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/subjects/" + code;
    }

    @PostMapping("/subjects/{code}/teachers/remove")
    public String removeTeacherFromSubject(
            @PathVariable String code,
            @RequestParam String teacherId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            subjectService.removeTeacherFromSubject(code, teacherId);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher " + teacherId + " removed from subject " + code + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/subjects/" + code;
    }

    @PostMapping("/subjects/delete/{code}")
    public String deleteSubject(
            @PathVariable String code,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            subjectService.deleteSubject(code);
            redirectAttributes.addFlashAttribute("successMessage", "Subject " + code + " deleted successfully!");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete subject " + code + ": " + ex.getMessage());
        }

        return "redirect:/admin/subjects";
    }

    private List<String> parseReferenceBooks(String text) {
        if (text == null || text.trim().isEmpty()) return List.of();
        return Arrays.stream(text.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // ==========================================
    // STEP 7: CLASSES AND SECTIONS
    // ==========================================

    @GetMapping("/sections")
    public String listSections(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<SectionInfo> sections = sectionService.getAllSections();
        List<Teacher> teachers = teacherService.getAllTeachers();

        model.addAttribute("sections", sections);
        model.addAttribute("teachers", teachers);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/sections";
    }

    @PostMapping("/sections/{standard}/{division}/teacher")
    public String appointClassTeacher(
            @PathVariable String standard,
            @PathVariable String division,
            @RequestParam(required = false) String teacherId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            sectionService.appointClassTeacher(standard, division, teacherId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Class teacher for Section " + standard + "-" + division + " updated successfully!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/sections";
    }

    @GetMapping("/sections/promote")
    public String showPromoteStudentsForm(
            @RequestParam(required = false) String sourceStandard,
            @RequestParam(required = false) String sourceDivision,
            @RequestParam(required = false) String targetStandard,
            @RequestParam(required = false) String targetDivision,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<SectionInfo> allSections = sectionService.getAllSections();
        List<Student> students = List.of();

        if (sourceStandard != null && sourceDivision != null &&
                !sourceStandard.trim().isEmpty() && !sourceDivision.trim().isEmpty()) {
            students = sectionService.getStudentsInSection(sourceStandard.trim(), sourceDivision.trim());
        }

        model.addAttribute("allSections", allSections);
        model.addAttribute("sourceStandard", sourceStandard);
        model.addAttribute("sourceDivision", sourceDivision);
        model.addAttribute("targetStandard", targetStandard);
        model.addAttribute("targetDivision", targetDivision);
        model.addAttribute("students", students);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/promote-students";
    }

    @PostMapping("/sections/promote")
    public String processStudentPromotion(
            @RequestParam String sourceStandard,
            @RequestParam String sourceDivision,
            @RequestParam String targetStandard,
            @RequestParam String targetDivision,
            @RequestParam(name = "studentIds", required = false) List<String> studentIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            int count = sectionService.promoteStudents(sourceStandard, sourceDivision, targetStandard, targetDivision, studentIds);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Successfully promoted %d student(s) from Class %s-%s to Class %s-%s!",
                            count, sourceStandard, sourceDivision, targetStandard, targetDivision));
            return "redirect:/admin/sections";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return String.format("redirect:/admin/sections/promote?sourceStandard=%s&sourceDivision=%s&targetStandard=%s&targetDivision=%s",
                    sourceStandard, sourceDivision, targetStandard, targetDivision);
        }
    }

    // ==========================================
    // STEP 8: ASSIGN SUBJECTS TO STUDENTS
    // ==========================================

    @GetMapping("/students/{admissionNo}")
    public String showStudentDetails(
            @PathVariable String admissionNo,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Student student = studentService.findByAdmissionNo(admissionNo);
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found with Admission No: " + admissionNo);
            return "redirect:/admin/students";
        }

        List<Guardian> guardians = guardianService.findByStudentId(admissionNo);
        List<Enrollment> enrolledSubjects = enrollmentService.getEnrolledSubjects(admissionNo);
        List<Subject> availableSubjects = enrollmentService.getAvailableSubjectsForStudent(admissionNo, student.getStandard());

        model.addAttribute("student", student);
        model.addAttribute("guardians", guardians);
        model.addAttribute("enrolledSubjects", enrolledSubjects);
        model.addAttribute("availableSubjects", availableSubjects);
        model.addAttribute("allowedGrades", List.of("A+", "A", "B+", "B", "C", "D", "F", ""));
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/student-detail";
    }

    @PostMapping("/students/{admissionNo}/enroll")
    public String enrollStudentInSubject(
            @PathVariable String admissionNo,
            @RequestParam String subjectCode,
            @RequestParam(required = false) String grade,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            enrollmentService.enrollStudent(admissionNo, subjectCode, grade);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Student " + admissionNo + " successfully enrolled in subject " + subjectCode + "!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/students/" + admissionNo;
    }

    @PostMapping("/students/{admissionNo}/unenroll")
    public String unenrollStudentFromSubject(
            @PathVariable String admissionNo,
            @RequestParam String subjectCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            enrollmentService.unenrollStudent(admissionNo, subjectCode);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Student " + admissionNo + " unenrolled from subject " + subjectCode + ".");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/students/" + admissionNo;
    }

    @PostMapping("/students/{admissionNo}/grade")
    public String updateStudentGrade(
            @PathVariable String admissionNo,
            @RequestParam String subjectCode,
            @RequestParam(required = false) String grade,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            enrollmentService.updateGrade(admissionNo, subjectCode, grade);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Grade updated successfully for subject " + subjectCode + ".");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/students/" + admissionNo;
    }

    @PostMapping("/students/{admissionNo}/bulk-enroll-section")
    public String bulkEnrollSection(
            @PathVariable String admissionNo,
            @RequestParam String standard,
            @RequestParam String division,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            int enrolledCount = enrollmentService.bulkEnrollSection(standard, division);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Bulk enrollment completed: %d new course enrollment(s) added for Class %s-%s!",
                            enrolledCount, standard, division));
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bulk enrollment failed: " + ex.getMessage());
        }

        return "redirect:/admin/students/" + admissionNo;
    }

    // ==========================================
    // STEP 9: HOUSES MODULE
    // ==========================================

    @GetMapping("/houses")
    public String listHouses(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<House> houses = houseService.getAllHouses();
        model.addAttribute("houses", houses);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/houses";
    }

    @GetMapping("/houses/{color}")
    public String showHouseDetail(
            @PathVariable String color,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        House house = houseService.getHouse(color);
        if (house == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "House not found: " + color);
            return "redirect:/admin/houses";
        }

        List<Teacher> availableMentors = houseService.getAvailableMentors(color);

        model.addAttribute("house", house);
        model.addAttribute("availableMentors", availableMentors);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/house-detail";
    }

    @PostMapping("/houses/{color}/tournaments")
    public String updateHouseTournaments(
            @PathVariable String color,
            @RequestParam int noOfWins,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            houseService.updateTournamentsWon(color, noOfWins);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tournaments won updated to " + noOfWins + " for " + color + " House.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/houses/" + color;
    }


    @PostMapping("/houses/{color}/mentors/add")
    public String addHouseMentor(
            @PathVariable String color,
            @RequestParam String teacherId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            houseService.addMentor(color, teacherId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Teacher " + teacherId + " assigned as mentor to " + color + " House.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/houses/" + color;
    }

    @PostMapping("/houses/{color}/mentors/remove")
    public String removeHouseMentor(
            @PathVariable String color,
            @RequestParam String teacherId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            houseService.removeMentor(color, teacherId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Teacher " + teacherId + " removed from " + color + " House mentoring duties.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/houses/" + color;
    }

    // ==========================================
    // STEP 10: CONSOLIDATED STUDENT PROFILE
    // ==========================================

    @GetMapping("/students/{admissionNo}/overview")
    public String showStudentOverview(
            @PathVariable String admissionNo,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Student student = studentService.findByAdmissionNo(admissionNo);
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found with Admission No: " + admissionNo);
            return "redirect:/admin/students";
        }

        // Guardians
        List<Guardian> guardians = guardianService.findByStudentId(admissionNo);

        // Section & Class Teacher
        SectionInfo section = sectionService.getSection(student.getStandard(), student.getDivision());

        // Enrolled Subjects & Grades
        List<Enrollment> enrolledSubjects = enrollmentService.getEnrolledSubjects(admissionNo);

        // House
        House house = houseService.getHouse(student.getHouse());

        // Transport
        VehicleInfo vehicle = null;
        if (student.getVehicleNo() != null && !student.getVehicleNo().trim().isEmpty()) {
            vehicle = transportService.getVehicleById(student.getVehicleNo());
        }

        // Fee Payments & Totals
        List<Payment> feePayments = paymentService.getStudentPayments(admissionNo);
        double totalPaid = feePayments.stream().mapToDouble(Payment::getAmount).sum();

        model.addAttribute("student", student);
        model.addAttribute("guardians", guardians);
        model.addAttribute("section", section);
        model.addAttribute("enrolledSubjects", enrolledSubjects);
        model.addAttribute("house", house);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("feePayments", feePayments);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loggedInUser", session.getAttribute("userId"));
        model.addAttribute("loggedInRole", "admin");

        return "admin/student-overview";
    }
}
