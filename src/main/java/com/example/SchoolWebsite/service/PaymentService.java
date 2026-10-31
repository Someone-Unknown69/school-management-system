package com.example.SchoolWebsite.service;

import com.example.SchoolWebsite.model.FeeReceipt;
import com.example.SchoolWebsite.model.Payment;
import com.example.SchoolWebsite.model.SalarySlip;
import com.example.SchoolWebsite.model.Staff;
import com.example.SchoolWebsite.model.Student;
import com.example.SchoolWebsite.model.Teacher;
import com.example.SchoolWebsite.repository.PaymentRepository;
import com.example.SchoolWebsite.repository.StaffRepository;
import com.example.SchoolWebsite.repository.StudentRepository;
import com.example.SchoolWebsite.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StaffRepository staffRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          StudentRepository studentRepository,
                          TeacherRepository teacherRepository,
                          StaffRepository staffRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.staffRepository = staffRepository;
    }

    public List<Payment> getStudentPayments(String studentId, String startDate, String endDate) {
        return paymentRepository.findFeePaymentsByStudentId(studentId, startDate, endDate);
    }

    public List<Payment> getStudentPayments(String studentId) {
        return paymentRepository.findFeePaymentsByStudentId(studentId);
    }

    public Map<String, Object> getTransactionStats() {
        return paymentRepository.getTransactionStats();
    }

    public List<Payment> getFilteredTransactions(String type, String mode, String startDate, String endDate, String search, int page, int pageSize) {
        return paymentRepository.findTransactions(type, mode, startDate, endDate, search, page, pageSize);
    }

    public List<Payment> getFilteredTransactions(String type, String mode, String search, int page, int pageSize) {
        return paymentRepository.findTransactions(type, mode, search, page, pageSize);
    }

    public int countFilteredTransactions(String type, String mode, String startDate, String endDate, String search) {
        return paymentRepository.countTransactions(type, mode, startDate, endDate, search);
    }

    public int countFilteredTransactions(String type, String mode, String search) {
        return paymentRepository.countTransactions(type, mode, search);
    }

    public List<String> getAvailablePaymentTypes() {
        return paymentRepository.getAvailablePaymentTypes();
    }

    public List<String> getAvailablePaymentModes() {
        return paymentRepository.getAvailablePaymentModes();
    }

    @Transactional
    public String recordFeePayment(String admissionNo, Double amount, String paymentDate, String paymentMode, String remitterName) {
        if (admissionNo == null || admissionNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Admission Number is required.");
        }

        Student student = studentRepository.findByAdmissionNo(admissionNo.trim());
        if (student == null) {
            throw new IllegalArgumentException("Student not found with Admission No: " + admissionNo);
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        if (amount > student.getRemainingFees()) {
            throw new IllegalArgumentException(String.format("Payment amount (₹%.2f) cannot exceed remaining fees dues (₹%.2f).", amount, student.getRemainingFees()));
        }

        // Format datetime
        String formattedDateTime;
        if (paymentDate == null || paymentDate.trim().isEmpty()) {
            formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            String trimmedDate = paymentDate.trim();
            if (trimmedDate.length() == 10) {
                String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                formattedDateTime = trimmedDate + " " + timePart;
            } else {
                formattedDateTime = trimmedDate;
            }
        }

        String mode = (paymentMode != null && !paymentMode.trim().isEmpty()) ? paymentMode.trim() : "Cash";
        String sender = (remitterName != null && !remitterName.trim().isEmpty()) ? remitterName.trim() : student.getFullName();

        // 1. Generate Transaction ID
        String transactionId = paymentRepository.generateNextTransactionId();

        // 2. Insert into Payments
        paymentRepository.savePayment(transactionId, formattedDateTime, "Fee", "School", sender, mode, amount);

        // 3. Insert into Fees
        paymentRepository.saveFeeLink(transactionId, student.getAdmissionNo());

        // 4. Reduce Student.Remaining_Fees
        studentRepository.reduceRemainingFees(student.getAdmissionNo(), amount);

        return transactionId;
    }

    public FeeReceipt getFeeReceipt(String transactionId) {
        return paymentRepository.findFeeReceipt(transactionId);
    }

    public boolean isSalaryPaid(String employeeType, String employeeId, int year, int month) {
        if (employeeType == null || employeeId == null) return false;
        return paymentRepository.isSalaryPaidForMonth(employeeType, employeeId, year, month);
    }

    @Transactional
    public String recordSalaryPayment(String employeeType, String employeeId, Double amount, int year, int month, String paymentDate, String paymentMode) {
        if (employeeType == null || (!employeeType.equalsIgnoreCase("Teacher") && !employeeType.equalsIgnoreCase("Staff"))) {
            throw new IllegalArgumentException("Employee type must be either 'Teacher' or 'Staff'.");
        }
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID is required.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month: " + month);
        }
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Invalid year: " + year);
        }

        String empId = employeeId.trim();
        String receiverName;
        double defaultSalary;

        if ("Teacher".equalsIgnoreCase(employeeType)) {
            Teacher teacher = teacherRepository.findFullProfile(empId);
            if (teacher == null) {
                throw new IllegalArgumentException("Teacher not found with Employee ID: " + empId);
            }
            receiverName = teacher.getFullName();
            defaultSalary = teacher.getSalary();
        } else {
            Staff staff = staffRepository.findById(empId);
            if (staff == null) {
                throw new IllegalArgumentException("Staff member not found with Employee ID: " + empId);
            }
            receiverName = staff.getFullName();
            defaultSalary = staff.getSalary();
        }

        double payAmount = (amount != null && amount > 0) ? amount : defaultSalary;
        if (payAmount <= 0) {
            throw new IllegalArgumentException("Disbursement amount must be greater than zero.");
        }

        // Check duplicate disbursement for that period
        if (isSalaryPaid(employeeType, empId, year, month)) {
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            throw new IllegalArgumentException(String.format("Salary for %s (%s) has already been disbursed for %s %d.",
                    receiverName, empId, monthName, year));
        }

        // Format datetime matching the selected period
        int day = 5;
        if (paymentDate != null && paymentDate.trim().length() >= 10) {
            try {
                LocalDate parsed = LocalDate.parse(paymentDate.trim().substring(0, 10));
                day = parsed.getDayOfMonth();
            } catch (Exception ignored) {}
        }
        int maxDays = YearMonth.of(year, month).lengthOfMonth();
        if (day > maxDays) day = maxDays;
        String formattedDateTime = String.format("%04d-%02d-%02d 10:00:00", year, month, day);

        String mode = (paymentMode != null && !paymentMode.trim().isEmpty()) ? paymentMode.trim() : "Bank Transfer";

        // 1. Generate Transaction ID
        String transactionId = paymentRepository.generateNextTransactionId();

        // 2. Insert into Payments
        paymentRepository.savePayment(transactionId, formattedDateTime, "Salary", receiverName, "School", mode, payAmount);

        // 3. Link in Teacher_Salary or Staff_Salary
        if ("Teacher".equalsIgnoreCase(employeeType)) {
            paymentRepository.saveTeacherSalaryLink(transactionId, empId);
        } else {
            paymentRepository.saveStaffSalaryLink(transactionId, empId);
        }

        return transactionId;
    }

    @Transactional
    public Map<String, Object> bulkDisburseSalaries(String targetGroup, int year, int month, String paymentMode, String paymentDate) {
        String group = (targetGroup != null) ? targetGroup.trim().toLowerCase() : "all";
        List<String> createdTxns = new ArrayList<>();
        int paidCount = 0;
        int skippedCount = 0;
        double totalAmount = 0.0;

        // Process Teachers if target is 'all' or 'teacher'
        if ("all".equals(group) || "teacher".equals(group) || "teachers".equals(group)) {
            List<Teacher> teachers = teacherRepository.findAll();
            for (Teacher t : teachers) {
                if (isSalaryPaid("Teacher", t.getEmployeeId(), year, month)) {
                    skippedCount++;
                } else {
                    String txnId = recordSalaryPayment("Teacher", t.getEmployeeId(), t.getSalary(), year, month, paymentDate, paymentMode);
                    createdTxns.add(txnId);
                    totalAmount += t.getSalary();
                    paidCount++;
                }
            }
        }

        // Process Staff if target is 'all' or 'staff'
        if ("all".equals(group) || "staff".equals(group)) {
            List<Staff> staffMembers = staffRepository.findAll();
            for (Staff s : staffMembers) {
                if (isSalaryPaid("Staff", s.getEmployeeId(), year, month)) {
                    skippedCount++;
                } else {
                    String txnId = recordSalaryPayment("Staff", s.getEmployeeId(), s.getSalary(), year, month, paymentDate, paymentMode);
                    createdTxns.add(txnId);
                    totalAmount += s.getSalary();
                    paidCount++;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paidCount", paidCount);
        result.put("skippedCount", skippedCount);
        result.put("totalAmount", totalAmount);
        result.put("transactionIds", createdTxns);
        return result;
    }

    public SalarySlip getSalarySlip(String transactionId) {
        return paymentRepository.findSalarySlip(transactionId);
    }

    public Map<String, Object> getFinancialSummary(String startDate, String endDate) {
        Map<String, Object> summary = new HashMap<>(paymentRepository.getFinancialSummary(startDate, endDate));
        double fees = ((Number) summary.getOrDefault("totalFees", 0.0)).doubleValue();
        double salaries = ((Number) summary.getOrDefault("totalSalaries", 0.0)).doubleValue();
        summary.put("netBalance", fees - salaries);
        return summary;
    }

    public List<Map<String, Object>> getMonthlyFinancialBreakdown(String startDate, String endDate) {
        return paymentRepository.getMonthlyFinancialBreakdown(startDate, endDate);
    }

    public List<Payment> getAllFilteredTransactions(String type, String mode, String startDate, String endDate, String search) {
        return paymentRepository.findAllFilteredTransactions(type, mode, startDate, endDate, search);
    }
}
