package com.example.SchoolWebsite.repository;

import com.example.SchoolWebsite.model.Enrollment;
import com.example.SchoolWebsite.model.Subject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EnrollmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public EnrollmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Enrollment> findByStudentId(String studentId) {
        String sql = "SELECT e.Subject_ID, s.Subject_Name, s.Periods_Per_Week, e.Grade, " +
                     "CONCAT(t.First_Name, ' ', COALESCE(t.Last_Name, '')) AS teacherName " +
                     "FROM Enrollments e " +
                     "JOIN Subject s ON e.Subject_ID = s.Subject_Code " +
                     "LEFT JOIN Teacher_Subject ts ON s.Subject_Code = ts.Subject_ID " +
                     "LEFT JOIN Teacher t ON ts.Teacher_ID = t.Employee_ID " +
                     "WHERE e.Student_ID = ? " +
                     "ORDER BY s.Subject_Name ASC";

        List<Enrollment> enrollments = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Enrollment e = new Enrollment();
            e.setSubjectCode(rs.getString("Subject_ID"));
            e.setSubjectName(rs.getString("Subject_Name"));
            e.setPeriodsPerWeek(rs.getInt("Periods_Per_Week"));
            e.setGrade(rs.getString("Grade"));
            String teacher = rs.getString("teacherName");
            e.setTeacherName(teacher != null && !teacher.trim().isEmpty() ? teacher : "Not Assigned");
            return e;
        }, studentId);

        // Fetch reference books for each enrolled subject
        for (Enrollment e : enrollments) {
            String bookSql = "SELECT Book_name FROM Reference_Books WHERE Subject_Code = ?";
            List<String> books = jdbcTemplate.queryForList(bookSql, String.class, e.getSubjectCode());
            e.setReferenceBooks(books);
        }

        return enrollments;
    }

    public boolean isStudentEnrolled(String studentId, String subjectCode) {
        String sql = "SELECT COUNT(*) FROM Enrollments WHERE Student_ID = ? AND Subject_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId, subjectCode);
        return count != null && count > 0;
    }

    public void enrollStudent(String studentId, String subjectCode, String grade) {
        String cleanGrade = (grade != null && !grade.trim().isEmpty()) ? grade.trim().toUpperCase() : null;
        String sql = "INSERT INTO Enrollments (Student_ID, Subject_ID, Grade) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, studentId, subjectCode, cleanGrade);
    }

    public void unenrollStudent(String studentId, String subjectCode) {
        String sql = "DELETE FROM Enrollments WHERE Student_ID = ? AND Subject_ID = ?";
        jdbcTemplate.update(sql, studentId, subjectCode);
    }

    public void updateGrade(String studentId, String subjectCode, String grade) {
        String cleanGrade = (grade != null && !grade.trim().isEmpty()) ? grade.trim().toUpperCase() : null;
        String sql = "UPDATE Enrollments SET Grade = ? WHERE Student_ID = ? AND Subject_ID = ?";
        jdbcTemplate.update(sql, cleanGrade, studentId, subjectCode);
    }

    public List<Subject> findAvailableSubjectsForStudent(String studentId, String standard) {
        String sql = "SELECT s.Subject_Code, s.Subject_Name, s.Periods_Per_Week, s.Class " +
                     "FROM Subject s " +
                     "WHERE s.Class = ? AND s.Subject_Code NOT IN (" +
                     "    SELECT Subject_ID FROM Enrollments WHERE Student_ID = ?" +
                     ") ORDER BY s.Subject_Name ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Subject s = new Subject();
            s.setSubjectCode(rs.getString("Subject_Code"));
            s.setSubjectName(rs.getString("Subject_Name"));
            s.setPeriodsPerWeek(rs.getInt("Periods_Per_Week"));
            s.setClassName(rs.getString("Class"));
            return s;
        }, standard, studentId);
    }

    public List<String> findStudentsInSection(String standard, String division) {
        String sql = "SELECT Admission_No FROM Student WHERE Standard = ? AND Division = ? ORDER BY Roll_No ASC";
        return jdbcTemplate.queryForList(sql, String.class, standard, division);
    }

    public List<String> findSubjectCodesForClass(String standard) {
        String sql = "SELECT Subject_Code FROM Subject WHERE Class = ? ORDER BY Subject_Name ASC";
        return jdbcTemplate.queryForList(sql, String.class, standard);
    }
}
