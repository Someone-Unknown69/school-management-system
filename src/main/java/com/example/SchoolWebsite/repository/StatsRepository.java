package com.example.SchoolWebsite.repository;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// Repository for aggregate statistics displayed on the home page
@Repository
public class StatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public StatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long countStudents() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Student", Long.class);
        return count != null ? count : 0;
    }

    public long countTeachers() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Teacher", Long.class);
        return count != null ? count : 0;
    }

    public long countStaff() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Staff", Long.class);
        return count != null ? count : 0;
    }

    public long countHouses() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM House", Long.class);
        return count != null ? count : 0;
    }

    // Used by LoginController to validate login credentials
    public boolean studentExists(String admissionNo) {
        Long count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM Student WHERE Admission_No = ?", Long.class, admissionNo);
        return count != null && count > 0;
    }

    public boolean teacherExists(String employeeId) {
        Long count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM Teacher WHERE Employee_ID = ?", Long.class, employeeId);
        return count != null && count > 0;
    }

    public boolean staffExists(String employeeId) {
        Long count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM Staff WHERE Employee_ID = ?", Long.class, employeeId);
        return count != null && count > 0;
    }

    public List<Map<String, Object>> getStudentCountByStandard() {
        String sql = "SELECT Standard, COUNT(*) AS count " +
                     "FROM Student " +
                     "GROUP BY Standard " +
                     "ORDER BY CAST(Standard AS UNSIGNED), Standard";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getMonthlyFeeCollections() {
        String sql = "SELECT DATE_FORMAT(Date_Of_Payment, '%Y-%m') AS ym, " +
                     "DATE_FORMAT(Date_Of_Payment, '%b %Y') AS monthLabel, " +
                     "COALESCE(SUM(Amount), 0) AS total " +
                     "FROM Payments " +
                     "WHERE Payment_Type = 'Fee' " +
                     "GROUP BY ym, monthLabel " +
                     "ORDER BY ym ASC";
        return jdbcTemplate.queryForList(sql);
    }

    public double getTotalFeeDues() {
        Double dues = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(Remaining_Fees), 0) FROM Student", Double.class);
        return dues != null ? dues : 0.0;
    }
}
