package com.example.SchoolWebsite.repository;

import com.example.SchoolWebsite.model.Subject;
import com.example.SchoolWebsite.model.Teacher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SubjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Subject> findAll(String classFilter) {
        StringBuilder sql = new StringBuilder(
                "SELECT s.Subject_Code, s.Subject_Name, s.Periods_Per_Week, s.Class, " +
                "(SELECT COUNT(*) FROM Reference_Books rb WHERE rb.Subject_Code = s.Subject_Code) AS bookCount, " +
                "(SELECT COUNT(*) FROM Teacher_Subject ts WHERE ts.Subject_ID = s.Subject_Code) AS teacherCount, " +
                "(SELECT COUNT(*) FROM Enrollments e WHERE e.Subject_ID = s.Subject_Code) AS enrollmentCount, " +
                "(SELECT COUNT(*) FROM Class_Schedule cs WHERE cs.Subject_ID = s.Subject_Code) AS scheduleCount " +
                "FROM Subject s "
        );

        List<Object> params = new ArrayList<>();
        if (classFilter != null && !classFilter.trim().isEmpty() && !"all".equalsIgnoreCase(classFilter.trim())) {
            sql.append("WHERE s.Class = ? ");
            params.add(classFilter.trim());
        }
        sql.append("ORDER BY CAST(s.Class AS UNSIGNED), s.Subject_Name ASC");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            Subject s = new Subject();
            s.setSubjectCode(rs.getString("Subject_Code"));
            s.setSubjectName(rs.getString("Subject_Name"));
            s.setPeriodsPerWeek(rs.getInt("Periods_Per_Week"));
            s.setClassName(rs.getString("Class"));
            s.setEnrollmentCount(rs.getInt("enrollmentCount"));
            s.setScheduleCount(rs.getInt("scheduleCount"));
            return s;
        }, params.toArray());
    }

    public Subject findByCode(String code) {
        String sql = "SELECT s.Subject_Code, s.Subject_Name, s.Periods_Per_Week, s.Class, " +
                     "(SELECT COUNT(*) FROM Enrollments e WHERE e.Subject_ID = s.Subject_Code) AS enrollmentCount, " +
                     "(SELECT COUNT(*) FROM Class_Schedule cs WHERE cs.Subject_ID = s.Subject_Code) AS scheduleCount " +
                     "FROM Subject s WHERE s.Subject_Code = ?";

        List<Subject> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Subject s = new Subject();
            s.setSubjectCode(rs.getString("Subject_Code"));
            s.setSubjectName(rs.getString("Subject_Name"));
            s.setPeriodsPerWeek(rs.getInt("Periods_Per_Week"));
            s.setClassName(rs.getString("Class"));
            s.setEnrollmentCount(rs.getInt("enrollmentCount"));
            s.setScheduleCount(rs.getInt("scheduleCount"));
            return s;
        }, code);

        if (list.isEmpty()) {
            return null;
        }

        Subject subject = list.get(0);

        // Fetch reference books
        String booksSql = "SELECT Book_name FROM Reference_Books WHERE Subject_Code = ? ORDER BY Book_name ASC";
        subject.setReferenceBooks(jdbcTemplate.queryForList(booksSql, String.class, code));

        // Fetch assigned teachers
        String teachersSql = "SELECT t.Employee_ID, t.First_Name, t.Middle_Name, t.Last_Name, t.Email, t.Phone_No " +
                             "FROM Teacher_Subject ts " +
                             "JOIN Teacher t ON ts.Teacher_ID = t.Employee_ID " +
                             "WHERE ts.Subject_ID = ? ORDER BY t.Employee_ID ASC";
        List<Teacher> teachers = jdbcTemplate.query(teachersSql, (rs, rowNum) -> {
            Teacher t = new Teacher();
            t.setEmployeeId(rs.getString("Employee_ID"));
            t.setFirstName(rs.getString("First_Name"));
            t.setMiddleName(rs.getString("Middle_Name"));
            t.setLastName(rs.getString("Last_Name"));
            t.setEmail(rs.getString("Email"));
            t.setPhoneNo(rs.getString("Phone_No"));
            return t;
        }, code);
        subject.setTeachers(teachers);

        return subject;
    }

    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM Subject WHERE Subject_Code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code);
        return count != null && count > 0;
    }

    public void save(Subject s, List<String> referenceBooks) {
        String sql = "INSERT INTO Subject (Subject_Code, Subject_Name, Periods_Per_Week, Class) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, s.getSubjectCode(), s.getSubjectName(), s.getPeriodsPerWeek(), s.getClassName());

        if (referenceBooks != null) {
            String bookSql = "INSERT INTO Reference_Books (Book_name, Subject_Code) VALUES (?, ?)";
            for (String book : referenceBooks) {
                if (book != null && !book.trim().isEmpty()) {
                    jdbcTemplate.update(bookSql, book.trim(), s.getSubjectCode());
                }
            }
        }
    }

    public void update(Subject s, List<String> referenceBooks) {
        String sql = "UPDATE Subject SET Subject_Name = ?, Periods_Per_Week = ?, Class = ? WHERE Subject_Code = ?";
        jdbcTemplate.update(sql, s.getSubjectName(), s.getPeriodsPerWeek(), s.getClassName(), s.getSubjectCode());

        // Replace reference books
        jdbcTemplate.update("DELETE FROM Reference_Books WHERE Subject_Code = ?", s.getSubjectCode());
        if (referenceBooks != null) {
            String bookSql = "INSERT INTO Reference_Books (Book_name, Subject_Code) VALUES (?, ?)";
            for (String book : referenceBooks) {
                if (book != null && !book.trim().isEmpty()) {
                    jdbcTemplate.update(bookSql, book.trim(), s.getSubjectCode());
                }
            }
        }
    }

    public void delete(String code) {
        jdbcTemplate.update("DELETE FROM Teacher_Subject WHERE Subject_ID = ?", code);
        jdbcTemplate.update("DELETE FROM Reference_Books WHERE Subject_Code = ?", code);
        jdbcTemplate.update("DELETE FROM Subject WHERE Subject_Code = ?", code);
    }

    public void addTeacher(String subjectCode, String teacherId) {
        String sql = "INSERT INTO Teacher_Subject (Teacher_ID, Subject_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, teacherId, subjectCode);
    }

    public void removeTeacher(String subjectCode, String teacherId) {
        String sql = "DELETE FROM Teacher_Subject WHERE Teacher_ID = ? AND Subject_ID = ?";
        jdbcTemplate.update(sql, teacherId, subjectCode);
    }

    public boolean isTeacherAssigned(String subjectCode, String teacherId) {
        String sql = "SELECT COUNT(*) FROM Teacher_Subject WHERE Teacher_ID = ? AND Subject_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, teacherId, subjectCode);
        return count != null && count > 0;
    }

    public List<Teacher> getUnassignedTeachers(String subjectCode) {
        String sql = "SELECT t.Employee_ID, t.First_Name, t.Middle_Name, t.Last_Name, t.Email, t.Phone_No " +
                     "FROM Teacher t " +
                     "WHERE t.Employee_ID NOT IN (SELECT Teacher_ID FROM Teacher_Subject WHERE Subject_ID = ?) " +
                     "ORDER BY t.Employee_ID ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Teacher t = new Teacher();
            t.setEmployeeId(rs.getString("Employee_ID"));
            t.setFirstName(rs.getString("First_Name"));
            t.setMiddleName(rs.getString("Middle_Name"));
            t.setLastName(rs.getString("Last_Name"));
            t.setEmail(rs.getString("Email"));
            t.setPhoneNo(rs.getString("Phone_No"));
            return t;
        }, subjectCode);
    }

    public List<String> getAvailableClasses() {
        String sql = "SELECT Standard FROM Class ORDER BY CAST(Standard AS UNSIGNED)";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public int countEnrollments(String subjectCode) {
        String sql = "SELECT COUNT(*) FROM Enrollments WHERE Subject_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subjectCode);
        return count != null ? count : 0;
    }

    public int countScheduleEntries(String subjectCode) {
        String sql = "SELECT COUNT(*) FROM Class_Schedule WHERE Subject_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subjectCode);
        return count != null ? count : 0;
    }
}

