package com.example.SchoolWebsite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class LoginAuthTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testLoginPageRenders() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testAdminLoginSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .param("userId", "admin")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    void testAdminLoginInvalidPassword() throws Exception {
        mockMvc.perform(post("/login")
                        .param("userId", "admin")
                        .param("password", "wrongpass"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testStudentLoginSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .param("userId", "A001")
                        .param("password", "pass_A001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    void testStudentLoginInvalidPassword() throws Exception {
        mockMvc.perform(post("/login")
                        .param("userId", "A001")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testTeacherLoginSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .param("userId", "T001")
                        .param("password", "pass_T001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));
    }

    @Test
    void testTeacherLoginByEmailSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .param("userId", "anita.verma@school.edu")
                        .param("password", "pass_T001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));
    }

    @Test
    void testStaffLoginSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .param("userId", "S001")
                        .param("password", "pass_S001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/dashboard"));
    }
}
