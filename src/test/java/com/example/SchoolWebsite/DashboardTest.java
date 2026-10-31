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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class DashboardTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private MockHttpSession createSession(String userId, String role) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", userId);
        session.setAttribute("role", role);
        return session;
    }

    @Test
    void testAdminDashboardRendersSuccessfully() throws Exception {
        MockHttpSession session = createSession("admin", "admin");

        mockMvc.perform(get("/admin/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("totalStudents"))
                .andExpect(model().attributeExists("totalTeachers"))
                .andExpect(model().attributeExists("totalStaff"))
                .andExpect(model().attributeExists("totalFees"))
                .andExpect(model().attributeExists("totalDues"))
                .andExpect(model().attributeExists("totalVehicles"))
                .andExpect(model().attributeExists("capacityUtilization"))
                .andExpect(model().attributeExists("classLabels"))
                .andExpect(model().attributeExists("classData"))
                .andExpect(model().attributeExists("feeLabels"))
                .andExpect(model().attributeExists("feeData"))
                .andExpect(model().attributeExists("recentPayments"));
    }

    @Test
    void testAdminRootPathRendersDashboard() throws Exception {
        MockHttpSession session = createSession("admin", "admin");

        mockMvc.perform(get("/admin").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }

    @Test
    void testTeacherDashboardRendersSuccessfully() throws Exception {
        MockHttpSession session = createSession("T001", "teacher");

        mockMvc.perform(get("/teacher/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher/dashboard"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attributeExists("schedule"))
                .andExpect(model().attributeExists("totalStudentsTaught"))
                .andExpect(model().attributeExists("todayDay"))
                .andExpect(model().attributeExists("todayDate"));
    }

    @Test
    void testStudentDashboardRendersSuccessfully() throws Exception {
        MockHttpSession session = createSession("A001", "student");

        mockMvc.perform(get("/student/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("student/dashboard"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("remainingFees"))
                .andExpect(model().attributeExists("enrollments"))
                .andExpect(model().attributeExists("subjectCount"));
    }

    @Test
    void testStaffDriverDashboardRendersSuccessfully() throws Exception {
        MockHttpSession session = createSession("S001", "staff");

        mockMvc.perform(get("/staff/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("staff/dashboard"))
                .andExpect(model().attributeExists("staff"))
                .andExpect(model().attribute("isDriver", true))
                .andExpect(model().attributeExists("vehicle"))
                .andExpect(model().attributeExists("busStudentCount"));
    }

    @Test
    void testStaffNonDriverDashboardRendersSuccessfully() throws Exception {
        MockHttpSession session = createSession("S009", "staff");

        mockMvc.perform(get("/staff/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("staff/dashboard"))
                .andExpect(model().attributeExists("staff"))
                .andExpect(model().attribute("isDriver", false))
                .andExpect(model().attributeExists("totalSalaryPayments"));
    }

    @Test
    void testHomeRedirectsAuthenticatedUserToRoleDashboard() throws Exception {
        MockHttpSession adminSession = createSession("admin", "admin");
        mockMvc.perform(get("/").session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));

        MockHttpSession teacherSession = createSession("T001", "teacher");
        mockMvc.perform(get("/").session(teacherSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));

        MockHttpSession studentSession = createSession("A001", "student");
        mockMvc.perform(get("/").session(studentSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));

        MockHttpSession staffSession = createSession("S001", "staff");
        mockMvc.perform(get("/").session(staffSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));
    }

    @Test
    void testUnauthenticatedAccessRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/teacher/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/staff/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testUnauthorizedRoleAccessIsForbidden() throws Exception {
        MockHttpSession studentSession = createSession("A001", "student");

        // Student attempting to access admin dashboard should receive 403 Forbidden
        mockMvc.perform(get("/admin/dashboard").session(studentSession))
                .andExpect(status().isForbidden());

        // Student attempting to access teacher dashboard should receive 403 Forbidden
        mockMvc.perform(get("/teacher/dashboard").session(studentSession))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthenticatedHomePageRendersCensusAndForm() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("studentCount"))
                .andExpect(model().attributeExists("teacherCount"))
                .andExpect(model().attributeExists("staffCount"))
                .andExpect(model().attributeExists("houseCount"))
                .andExpect(model().attribute("isLoggedIn", false));
    }
}

