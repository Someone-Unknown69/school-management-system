package com.example.SchoolWebsite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AdminFeaturesTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up test salary disbursements & payments created after seed cutoff
        jdbcTemplate.update("DELETE FROM Teacher_Salary WHERE Payment_ID IN (SELECT Transaction_ID FROM Payments WHERE Date_Of_Payment >= '2026-10-01')");
        jdbcTemplate.update("DELETE FROM Staff_Salary WHERE Payment_ID IN (SELECT Transaction_ID FROM Payments WHERE Date_Of_Payment >= '2026-10-01')");
        jdbcTemplate.update("DELETE FROM Fees WHERE Payment_ID IN (SELECT Transaction_ID FROM Payments WHERE Date_Of_Payment >= '2026-10-01')");
        jdbcTemplate.update("DELETE FROM Payments WHERE Date_Of_Payment >= '2026-10-01'");
        jdbcTemplate.update("UPDATE Student SET Remaining_Fees = 5000.00 WHERE Admission_No = 'A001' AND Remaining_Fees < 1000.00");

        // Clean up test subjects
        jdbcTemplate.update("DELETE FROM Teacher_Subject WHERE Subject_ID IN ('ASTR11', 'TEMP9', 'TEST10')");
        jdbcTemplate.update("DELETE FROM Reference_Books WHERE Subject_Code IN ('ASTR11', 'TEMP9', 'TEST10')");
        jdbcTemplate.update("DELETE FROM Subject WHERE Subject_Code IN ('ASTR11', 'TEMP9', 'TEST10')");

        // Reset section test mutations
        jdbcTemplate.update("UPDATE Section SET Class_Teacher = 'T001' WHERE Standard = '10' AND Division = 'A'");
        jdbcTemplate.update("UPDATE Section SET Class_Teacher = NULL WHERE Standard = '9' AND Division = 'A'");

        // Reset house test mutations
        jdbcTemplate.update("UPDATE Teacher SET MentorOf = 'Red' WHERE Employee_ID = 'T001'");
        jdbcTemplate.update("UPDATE Teacher SET MentorOf = NULL WHERE Employee_ID = 'T010'");
    }

    private MockHttpSession createAdminSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", "admin");
        session.setAttribute("role", "admin");
        return session;
    }

    @Test
    void testAdminNewStudentPageRendersSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/students/new").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/student-form"));
    }

    @Test
    void testAdminNewTeacherPageRendersSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/faculty/teacher/new").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/teacher-form"));
    }

    @Test
    void testAdminFacultyPagesRenderSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/faculty?tab=teachers").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/faculty"))
                .andExpect(model().attributeExists("teachers", "activeTab"));

        mockMvc.perform(get("/admin/faculty?tab=staff").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/faculty"))
                .andExpect(model().attributeExists("staffList", "activeTab"));
    }

    @Test
    void testAdminDeleteTeacherSuccess() throws Exception {
        // Insert a temporary test teacher
        jdbcTemplate.update("INSERT IGNORE INTO Teacher (Employee_ID, First_Name, Last_Name, Date_Of_Joining, Phone_No, Email, Salary) " +
                            "VALUES ('T999', 'Test', 'Teacher', '2026-01-01', '9999999999', 'test.t@school.edu', 30000.00)");
        jdbcTemplate.update("INSERT IGNORE INTO User_Account (Username, Password, Role) VALUES ('T999', 'pass_T999', 'teacher')");

        mockMvc.perform(post("/admin/faculty/teacher/delete/T999").session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/faculty?tab=teachers"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testAdminDeleteStaffSuccess() throws Exception {
        // Insert a temporary test staff
        jdbcTemplate.update("INSERT IGNORE INTO Staff (Employee_ID, First_Name, Last_Name, Date_Of_Joining, Contact_Number, Designated_Role, Salary) " +
                            "VALUES ('S999', 'Test', 'Staff', '2026-01-01', '8888888888', 'Peon', 20000.00)");
        jdbcTemplate.update("INSERT IGNORE INTO User_Account (Username, Password, Role) VALUES ('S999', 'pass_S999', 'staff')");

        mockMvc.perform(post("/admin/faculty/staff/delete/S999").session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/faculty?tab=staff"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testAdminTransactionsPageRendersSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/transactions").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/transactions"))
                .andExpect(model().attributeExists("transactions", "stats", "availableTypes", "availableModes"));
    }

    @Test
    void testAdminTransactionsWithFilters() throws Exception {
        mockMvc.perform(get("/admin/transactions")
                        .param("type", "Fee")
                        .param("mode", "UPI")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30")
                        .param("search", "A001")
                        .param("page", "1")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/transactions"))
                .andExpect(model().attributeExists("transactions", "startDate", "endDate"))
                .andExpect(model().attribute("startDate", "2026-04-01"))
                .andExpect(model().attribute("endDate", "2026-04-30"));
    }

    @Test
    void testStudentFeesWithDateRangeFilter() throws Exception {
        MockHttpSession studentSession = new MockHttpSession();
        studentSession.setAttribute("userId", "A001");
        studentSession.setAttribute("role", "student");

        mockMvc.perform(get("/student/fees")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30")
                        .session(studentSession))
                .andExpect(status().isOk())
                .andExpect(view().name("student/fees"))
                .andExpect(model().attributeExists("student", "payments", "startDate", "endDate"));
    }

    @Test
    void testAdminTransportPageRendersSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/transport").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/transport"))
                .andExpect(model().attributeExists("vehicles", "stats", "passengers"));
    }

    @Test
    void testAdminTransportVehicleWithSpecificSelection() throws Exception {
        mockMvc.perform(get("/admin/transport")
                        .param("selectedVehicle", "V01")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/transport"))
                .andExpect(model().attributeExists("vehicles", "activeVehicle", "passengers"));
    }

    @Test
    void testAdminVehicleNewFormRenders() throws Exception {
        mockMvc.perform(get("/admin/transport/vehicle/new").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/vehicle-form"))
                .andExpect(model().attributeExists("vehicle", "drivers", "mode"));
    }

    @Test
    void testAdminVehicleEditFormRenders() throws Exception {
        mockMvc.perform(get("/admin/transport/vehicle/edit/V01").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/vehicle-form"))
                .andExpect(model().attributeExists("vehicle", "drivers", "mode"));
    }

    @Test
    void testAdminAddAndRemovePassengerFlow() throws Exception {
        // Add student to vehicle V01
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/transport/add-passenger")
                        .param("vehicleId", "V01")
                        .param("admissionNo", "A001")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/transport?selectedVehicle=V01#passengers"))
                .andExpect(flash().attributeExists("successMessage"));

        // Remove student from vehicle V01
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/transport/remove-passenger")
                        .param("vehicleId", "V01")
                        .param("admissionNo", "A001")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/transport?selectedVehicle=V01"))
                .andExpect(flash().attributeExists("successMessage"));

        // Re-assign A001 back so database state remains pristine
        mockMvc.perform(post("/admin/transport/add-passenger")
                        .param("vehicleId", "V01")
                        .param("admissionNo", "A001")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testStudentValidationWithInvalidPhone() throws Exception {
        mockMvc.perform(post("/admin/students/new")
                        .param("admissionNo", "TEST999")
                        .param("firstName", "Test")
                        .param("standard", "10")
                        .param("division", "A")
                        .param("house", "Red")
                        .param("dateOfAdmission", "2026-04-01")
                        .param("dateOfBirth", "2010-01-01")
                        .param("age", "16")
                        .param("gender", "Male")
                        .param("bloodGroup", "O+")
                        .param("contactNumber", "123") // Invalid phone
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students/new"))
                .andExpect(flash().attribute("errorMessage", "Student contact number must be a valid 10-digit number."));
    }

    @Test
    void testStudentValidationWithInvalidGuardianEmail() throws Exception {
        mockMvc.perform(post("/admin/students/new")
                        .param("admissionNo", "TEST999")
                        .param("firstName", "Test")
                        .param("standard", "10")
                        .param("division", "A")
                        .param("house", "Red")
                        .param("dateOfAdmission", "2026-04-01")
                        .param("dateOfBirth", "2010-01-01")
                        .param("age", "16")
                        .param("gender", "Male")
                        .param("bloodGroup", "O+")
                        .param("contactNumber", "9876543210")
                        .param("guardianEmail", "invalid-email") // Invalid email
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students/new"))
                .andExpect(flash().attribute("errorMessage", "Guardian email address is invalid."));
    }

    @Test
    void testTeacherValidationWithInvalidPhone() throws Exception {
        mockMvc.perform(post("/admin/faculty/teacher/new")
                        .param("employeeId", "TESTT99")
                        .param("firstName", "Teacher")
                        .param("dateOfJoining", "2026-01-01")
                        .param("salary", "50000")
                        .param("email", "teacher@school.edu")
                        .param("phoneNo", "not-a-phone")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/faculty/teacher/new"))
                .andExpect(flash().attribute("errorMessage", "Teacher phone number must be a valid 10-digit number."));
    }

    @Test
    void testStudentProfileDisplaysNewAttributes() throws Exception {
        MockHttpSession studentSession = new MockHttpSession();
        studentSession.setAttribute("userId", "A001");
        studentSession.setAttribute("role", "student");

        mockMvc.perform(get("/student/profile").session(studentSession))
                .andExpect(status().isOk())
                .andExpect(view().name("student/profile"))
                .andExpect(model().attributeExists("student", "guardians"));
    }

    // ==========================================
    // STEP 4: FEE COLLECTION TESTS
    // ==========================================

    @Test
    void testFeeDuesPageRendersForAdmin() throws Exception {
        mockMvc.perform(get("/admin/fees/dues").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fee-dues"))
                .andExpect(model().attributeExists("students", "totalOutstanding", "dueStudentsCount", "totalSchoolDues"));
    }

    @Test
    void testFeeDuesPageWithFilters() throws Exception {
        mockMvc.perform(get("/admin/fees/dues")
                        .param("standard", "10")
                        .param("division", "A")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fee-dues"))
                .andExpect(model().attributeExists("students", "totalOutstanding"));
    }

    @Test
    void testRecordFeePaymentPageRenders() throws Exception {
        mockMvc.perform(get("/admin/fees/record").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fee-record"))
                .andExpect(model().attributeExists("studentsWithDues", "paymentModes"));
    }

    @Test
    void testRecordFeePaymentPageWithStudentSelected() throws Exception {
        mockMvc.perform(get("/admin/fees/record")
                        .param("admissionNo", "A001")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fee-record"))
                .andExpect(model().attributeExists("selectedStudent", "studentsWithDues"));
    }

    @Test
    void testRecordFeePaymentPageHandlesMultipleAdmissionNoParameters() throws Exception {
        mockMvc.perform(get("/admin/fees/record")
                        .param("admissionNo", "A001")
                        .param("admissionNo", "")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fee-record"))
                .andExpect(model().attributeExists("selectedStudent"))
                .andExpect(model().attribute("admissionNo", "A001"));
    }

    @Test
    void testRecordFeePaymentSuccessFlow() throws Exception {
        mockMvc.perform(post("/admin/fees/record")
                        .param("admissionNo", "A001")
                        .param("amount", "500.00")
                        .param("paymentMode", "UPI")
                        .param("paymentDate", "2026-10-30")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/fees/receipt/TXN*"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testRecordFeePaymentRejectedWhenAmountZeroOrNegative() throws Exception {
        // Zero amount
        mockMvc.perform(post("/admin/fees/record")
                        .param("admissionNo", "A001")
                        .param("amount", "0.00")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/fees/record?admissionNo=A001"))
                .andExpect(flash().attribute("errorMessage", "Payment amount must be greater than zero."));

        // Negative amount
        mockMvc.perform(post("/admin/fees/record")
                        .param("admissionNo", "A001")
                        .param("amount", "-100.00")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/fees/record?admissionNo=A001"))
                .andExpect(flash().attribute("errorMessage", "Payment amount must be greater than zero."));
    }

    @Test
    void testRecordFeePaymentRejectedWhenAmountExceedsRemainingFees() throws Exception {
        mockMvc.perform(post("/admin/fees/record")
                        .param("admissionNo", "A001")
                        .param("amount", "9999999.00")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/fees/record?admissionNo=A001"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testPrintableReceiptRendersForAdmin() throws Exception {
        mockMvc.perform(get("/admin/fees/receipt/TXN0001").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fee-receipt"))
                .andExpect(model().attributeExists("receipt"));
    }

    @Test
    void testPrintableReceiptAccessibleByStudent() throws Exception {
        MockHttpSession studentSession = new MockHttpSession();
        studentSession.setAttribute("userId", "A001");
        studentSession.setAttribute("role", "student");

        mockMvc.perform(get("/admin/fees/receipt/TXN0001").session(studentSession))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/fee-receipt"))
                .andExpect(model().attributeExists("receipt"));
    }

    @Test
    void testStudentFeesPageLinksToReceipt() throws Exception {
        MockHttpSession studentSession = new MockHttpSession();
        studentSession.setAttribute("userId", "A001");
        studentSession.setAttribute("role", "student");

        mockMvc.perform(get("/student/fees").session(studentSession))
                .andExpect(status().isOk())
                .andExpect(view().name("student/fees"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/admin/fees/receipt/")));
    }

    // ==========================================
    // STEP 5: SALARY DISBURSEMENT TESTS
    // ==========================================

    @Test
    void testPaySalaryPageRendersSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/salary/pay").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/salary-pay"))
                .andExpect(model().attributeExists("employeeType", "teachers", "staffList", "months", "years", "paymentModes"));
    }

    @Test
    void testPaySalaryPageWithTeacherSelected() throws Exception {
        mockMvc.perform(get("/admin/salary/pay")
                        .param("employeeType", "Teacher")
                        .param("employeeId", "T001")
                        .param("month", "4")
                        .param("year", "2026")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/salary-pay"))
                .andExpect(model().attributeExists("selectedEmployee", "employeeName", "defaultSalary", "isAlreadyPaid"))
                .andExpect(model().attribute("isAlreadyPaid", true)); // T001 is already paid in April 2026 in seed data
    }

    @Test
    void testPaySalaryPageWithStaffSelected() throws Exception {
        mockMvc.perform(get("/admin/salary/pay")
                        .param("employeeType", "Staff")
                        .param("employeeId", "S001")
                        .param("month", "4")
                        .param("year", "2026")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/salary-pay"))
                .andExpect(model().attributeExists("selectedEmployee", "employeeName", "defaultSalary", "isAlreadyPaid"))
                .andExpect(model().attribute("isAlreadyPaid", true)); // S001 is already paid in April 2026 in seed data
    }

    @Test
    void testDisburseTeacherSalarySuccessFlow() throws Exception {
        // Disburse salary for November 2026 (unpaid in seed)
        mockMvc.perform(post("/admin/salary/pay")
                        .param("employeeType", "Teacher")
                        .param("employeeId", "T001")
                        .param("amount", "52000.00")
                        .param("month", "11")
                        .param("year", "2026")
                        .param("paymentMode", "Bank Transfer")
                        .param("paymentDate", "2026-11-05")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/salary/slip/TXN*"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testDuplicateSalaryPaymentRejected() throws Exception {
        // First disbursement for month 10 / year 2027 (so it's clean)
        mockMvc.perform(post("/admin/salary/pay")
                        .param("employeeType", "Teacher")
                        .param("employeeId", "T002")
                        .param("amount", "48000.00")
                        .param("month", "10")
                        .param("year", "2027")
                        .param("paymentMode", "Bank Transfer")
                        .param("paymentDate", "2027-10-05")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection());

        // Attempt second disbursement for same month/year
        mockMvc.perform(post("/admin/salary/pay")
                        .param("employeeType", "Teacher")
                        .param("employeeId", "T002")
                        .param("amount", "48000.00")
                        .param("month", "10")
                        .param("year", "2027")
                        .param("paymentMode", "Bank Transfer")
                        .param("paymentDate", "2027-10-05")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/salary/pay?employeeType=Teacher&employeeId=T002&month=10&year=2027"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testDisburseStaffSalarySuccessFlow() throws Exception {
        // Disburse salary for November 2026 (unpaid in seed)
        mockMvc.perform(post("/admin/salary/pay")
                        .param("employeeType", "Staff")
                        .param("employeeId", "S001")
                        .param("amount", "22000.00")
                        .param("month", "11")
                        .param("year", "2026")
                        .param("paymentMode", "Bank Transfer")
                        .param("paymentDate", "2026-11-05")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/salary/slip/TXN*"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testBulkSalaryDisbursementFlow() throws Exception {
        // Bulk disburse for a future month (e.g. Month 12, Year 2027)
        mockMvc.perform(post("/admin/salary/bulk-pay")
                        .param("targetGroup", "staff")
                        .param("month", "12")
                        .param("year", "2027")
                        .param("paymentMode", "Bank Transfer")
                        .param("paymentDate", "2027-12-05")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/transactions?type=Salary"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testPrintableSalarySlipRendersForAdmin() throws Exception {
        mockMvc.perform(get("/admin/salary/slip/TXN1057").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/salary-slip"))
                .andExpect(model().attributeExists("slip"));
    }

    @Test
    void testPrintableSalarySlipAccessibleByTeacherOwner() throws Exception {
        MockHttpSession teacherSession = new MockHttpSession();
        teacherSession.setAttribute("userId", "T001");
        teacherSession.setAttribute("role", "teacher");

        mockMvc.perform(get("/admin/salary/slip/TXN1057").session(teacherSession))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/salary-slip"))
                .andExpect(model().attributeExists("slip"));
    }

    @Test
    void testSalarySlipUnauthorizedForDifferentTeacher() throws Exception {
        MockHttpSession otherTeacherSession = new MockHttpSession();
        otherTeacherSession.setAttribute("userId", "T002");
        otherTeacherSession.setAttribute("role", "teacher");

        // TXN1057 belongs to T001
        mockMvc.perform(get("/admin/salary/slip/TXN1057").session(otherTeacherSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/salary"))
                .andExpect(flash().attribute("errorMessage", "Unauthorized access to salary slip."));
    }

    @Test
    void testPrintableSalarySlipAccessibleByStaffOwner() throws Exception {
        MockHttpSession staffSession = new MockHttpSession();
        staffSession.setAttribute("userId", "S001");
        staffSession.setAttribute("role", "staff");

        // TXN1225 belongs to S001
        mockMvc.perform(get("/admin/salary/slip/TXN1225").session(staffSession))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/salary-slip"))
                .andExpect(model().attributeExists("slip"));
    }

    @Test
    void testTeacherSalaryPageLinksToSlip() throws Exception {
        MockHttpSession teacherSession = new MockHttpSession();
        teacherSession.setAttribute("userId", "T001");
        teacherSession.setAttribute("role", "teacher");

        mockMvc.perform(get("/teacher/salary").session(teacherSession))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/salary"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Salary Slip")));
    }

    @Test
    void testStaffSalaryPageLinksToSlip() throws Exception {
        MockHttpSession staffSession = new MockHttpSession();
        staffSession.setAttribute("userId", "S001");
        staffSession.setAttribute("role", "staff");

        mockMvc.perform(get("/staff/salary").session(staffSession))
                .andExpect(status().isOk())
                .andExpect(view().name("staff/salary"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Salary Slip")));
    }

    // ==========================================
    // STEP 6: SUBJECT MANAGEMENT TESTS
    // ==========================================

    @Test
    void testListSubjectsPageRendersSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/subjects").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subjects"))
                .andExpect(model().attributeExists("subjects", "availableClasses"));
    }

    @Test
    void testListSubjectsWithClassFilter() throws Exception {
        mockMvc.perform(get("/admin/subjects")
                        .param("classFilter", "10")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subjects"))
                .andExpect(model().attributeExists("subjects", "classFilter"))
                .andExpect(model().attribute("classFilter", "10"));
    }

    @Test
    void testNewSubjectFormRenders() throws Exception {
        mockMvc.perform(get("/admin/subjects/new").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subject-form"))
                .andExpect(model().attribute("mode", "add"))
                .andExpect(model().attributeExists("subject", "availableClasses"));
    }

    @Test
    void testCreateSubjectSuccessFlow() throws Exception {
        mockMvc.perform(post("/admin/subjects/new")
                        .param("subjectCode", "ASTR11")
                        .param("subjectName", "Astronomy")
                        .param("className", "11")
                        .param("periodsPerWeek", "4")
                        .param("referenceBooksText", "Cosmos by Carl Sagan\nAstrophysics for Young People")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subjects"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testCreateSubjectDuplicateCodeRejected() throws Exception {
        mockMvc.perform(post("/admin/subjects/new")
                        .param("subjectCode", "MATH10")
                        .param("subjectName", "Duplicate Math")
                        .param("className", "10")
                        .param("periodsPerWeek", "5")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subjects/new"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testSubjectDetailView() throws Exception {
        mockMvc.perform(get("/admin/subjects/MATH10").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/subject-detail"))
                .andExpect(model().attributeExists("subject", "unassignedTeachers"));
    }

    @Test
    void testAddAndRemoveTeacherFromSubject() throws Exception {
        // Assign teacher T012 to subject MATH10
        mockMvc.perform(post("/admin/subjects/MATH10/teachers/add")
                        .param("teacherId", "T012")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subjects/MATH10"))
                .andExpect(flash().attributeExists("successMessage"));

        // Remove teacher T012 from subject MATH10
        mockMvc.perform(post("/admin/subjects/MATH10/teachers/remove")
                        .param("teacherId", "T012")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subjects/MATH10"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testDeleteSubjectBlockedWhenEnrollmentsExist() throws Exception {
        // MATH10 has active enrollments in seed data
        mockMvc.perform(post("/admin/subjects/delete/MATH10")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subjects"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testDeleteSubjectSuccessWhenNoEnrollments() throws Exception {
        // Create an un-enrolled test subject first
        mockMvc.perform(post("/admin/subjects/new")
                        .param("subjectCode", "TEMP9")
                        .param("subjectName", "Temporary Subject")
                        .param("className", "9")
                        .param("periodsPerWeek", "2")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection());

        // Now delete it
        mockMvc.perform(post("/admin/subjects/delete/TEMP9")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/subjects"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    // ==========================================
    // STEP 7: CLASSES & SECTIONS TESTS
    // ==========================================

    @Test
    void testSectionsPageRendersSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/sections").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/sections"))
                .andExpect(model().attributeExists("sections", "teachers"));
    }

    @Test
    void testAppointClassTeacherConflictRejected() throws Exception {
        // In seed data, Section 9-A has a class teacher or Section 10-A has a class teacher
        // Let's appoint T001 to 9-A first
        mockMvc.perform(post("/admin/sections/9/A/teacher")
                        .param("teacherId", "T001")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sections"));

        // Now attempting to appoint T001 to 9-B must be rejected
        mockMvc.perform(post("/admin/sections/9/B/teacher")
                        .param("teacherId", "T001")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/sections"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testPromoteStudentsPageRenders() throws Exception {
        mockMvc.perform(get("/admin/sections/promote")
                        .param("sourceStandard", "9")
                        .param("sourceDivision", "A")
                        .param("targetStandard", "10")
                        .param("targetDivision", "A")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/promote-students"))
                .andExpect(model().attributeExists("allSections", "students"));
    }

    @Test
    void testPromoteStudentsBeyondClass12Blocked() throws Exception {
        mockMvc.perform(post("/admin/sections/promote")
                        .param("sourceStandard", "12")
                        .param("sourceDivision", "A")
                        .param("targetStandard", "13")
                        .param("targetDivision", "A")
                        .param("studentIds", "A050")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Target section Class 13-A does not exist in the school registry."));
    }

    @Test
    void testPromoteStudentsTargetSectionNotExistsBlocked() throws Exception {
        mockMvc.perform(post("/admin/sections/promote")
                        .param("sourceStandard", "9")
                        .param("sourceDivision", "A")
                        .param("targetStandard", "10")
                        .param("targetDivision", "Z")
                        .param("studentIds", "A001")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ==========================================
    // STEP 8: STUDENT ENROLLED SUBJECTS TESTS
    // ==========================================

    @Test
    void testStudentDetailEnrollmentsPageRenders() throws Exception {
        mockMvc.perform(get("/admin/students/A001").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/student-detail"))
                .andExpect(model().attributeExists("student", "enrolledSubjects", "availableSubjects", "allowedGrades"));
    }

    @Test
    void testUpdateStudentGradeSuccessFlow() throws Exception {
        mockMvc.perform(post("/admin/students/A001/grade")
                        .param("subjectCode", "MATH10")
                        .param("grade", "A+")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students/A001"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testInvalidGradeRejected() throws Exception {
        mockMvc.perform(post("/admin/students/A001/grade")
                        .param("subjectCode", "MATH10")
                        .param("grade", "INVALID_GRADE")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students/A001"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testDuplicateEnrollmentRejected() throws Exception {
        // A001 is already enrolled in MATH10
        mockMvc.perform(post("/admin/students/A001/enroll")
                        .param("subjectCode", "MATH10")
                        .param("grade", "A")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students/A001"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ==========================================
    // STEP 9: HOUSES MODULE TESTS
    // ==========================================

    @Test
    void testHousesPageRendersSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/houses").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/houses"))
                .andExpect(model().attributeExists("houses"));
    }

    @Test
    void testHouseDetailView() throws Exception {
        mockMvc.perform(get("/admin/houses/Red").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/house-detail"))
                .andExpect(model().attributeExists("house", "availableMentors"));
    }

    @Test
    void testUpdateTournamentsWon() throws Exception {
        mockMvc.perform(post("/admin/houses/Red/tournaments")
                        .param("noOfWins", "7")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/houses/Red"))
                .andExpect(flash().attributeExists("successMessage"));
    }


    @Test
    void testAddAndRemoveHouseMentor() throws Exception {
        // Add teacher T010 as mentor to Red house
        mockMvc.perform(post("/admin/houses/Red/mentors/add")
                        .param("teacherId", "T010")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/houses/Red"))
                .andExpect(flash().attributeExists("successMessage"));

        // Remove teacher T010 from Red house
        mockMvc.perform(post("/admin/houses/Red/mentors/remove")
                        .param("teacherId", "T010")
                        .session(createAdminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/houses/Red"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    // ==========================================
    // STEP 10: CONSOLIDATED STUDENT PROFILE TESTS
    // ==========================================

    @Test
    void testStudentOverviewPageRendersSuccessfully() throws Exception {
        mockMvc.perform(get("/admin/students/A001/overview").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/student-overview"))
                .andExpect(model().attributeExists("student", "guardians", "section", "enrolledSubjects", "house", "totalPaid"));
    }

    // ==========================================
    // STEP 11: FINANCIAL STATEMENTS TESTS
    // ==========================================

    @Test
    void testTransactionsFinancialSummaryCalculations() throws Exception {
        mockMvc.perform(get("/admin/transactions").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/transactions"))
                .andExpect(model().attributeExists("financialSummary", "monthlyBreakdown"));
    }

    @Test
    void testExportTransactionsCsv() throws Exception {
        mockMvc.perform(get("/admin/transactions/export")
                        .param("type", "Fee")
                        .session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"transactions_ledger.csv\""))
                .andExpect(content().contentType("text/csv; charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Transaction ID,Date,Type")));
    }

    @Test
    void testFinancialStatementPrintViewRenders() throws Exception {
        mockMvc.perform(get("/admin/transactions/statement").session(createAdminSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/financial-statement"))
                .andExpect(model().attributeExists("financialSummary", "monthlyBreakdown"));
    }
}
