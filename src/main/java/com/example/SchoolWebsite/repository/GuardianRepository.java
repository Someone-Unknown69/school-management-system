package com.example.SchoolWebsite.repository;

import com.example.SchoolWebsite.model.Guardian;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GuardianRepository {

    private final JdbcTemplate jdbcTemplate;

    public GuardianRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get all guardians for a specific student
    public List<Guardian> findByStudentId(String studentId) {
        String sql = "SELECT Student_ID, Relationship, First_Name, Middle_Name, Last_Name, " +
                     "Occupation, Phone_No, Email FROM Student_Guardian WHERE Student_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Guardian g = new Guardian();
            g.setStudentId(rs.getString("Student_ID"));
            g.setRelationship(rs.getString("Relationship"));
            g.setFirstName(rs.getString("First_Name"));
            g.setMiddleName(rs.getString("Middle_Name"));
            g.setLastName(rs.getString("Last_Name"));
            g.setOccupation(rs.getString("Occupation"));
            g.setPhoneNo(rs.getString("Phone_No"));
            g.setEmail(rs.getString("Email"));
            return g;
        }, studentId);
    }
}
