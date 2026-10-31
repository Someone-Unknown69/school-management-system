package com.example.SchoolWebsite.repository;

import com.example.SchoolWebsite.model.ScheduleItem;
import com.example.SchoolWebsite.model.Teacher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeacherRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeacherRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Find full teacher profile including qualifications, specializations, and mentorship
    public Teacher findFullProfile(String employeeId) {
        String sql = "SELECT t.Employee_ID, t.First_Name, t.Middle_Name, t.Last_Name, " +
                     "t.Date_Of_Joining, t.Phone_No, t.Email, t.Address, t.Salary, t.MentorOf, " +
                     "h.House_Name, " +
                     "sec.Standard AS classTeacherStandard, sec.Division AS classTeacherDivision, sec.Room_No AS classTeacherRoomNo " +
                     "FROM Teacher t " +
                     "LEFT JOIN House h ON t.MentorOf = h.Color " +
                     "LEFT JOIN Section sec ON t.Employee_ID = sec.Class_Teacher " +
                     "WHERE t.Employee_ID = ?";

        Teacher teacher = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Teacher t = new Teacher();
            t.setEmployeeId(rs.getString("Employee_ID"));
            t.setFirstName(rs.getString("First_Name"));
            t.setMiddleName(rs.getString("Middle_Name"));
            t.setLastName(rs.getString("Last_Name"));
            t.setDateOfJoining(rs.getString("Date_Of_Joining"));
            t.setPhoneNo(rs.getString("Phone_No"));
            t.setEmail(rs.getString("Email"));
            t.setAddress(rs.getString("Address"));
            t.setSalary(rs.getDouble("Salary"));
            t.setMentorOf(rs.getString("MentorOf"));
            t.setHouseName(rs.getString("House_Name"));
            t.setClassTeacherStandard(rs.getString("classTeacherStandard"));
            t.setClassTeacherDivision(rs.getString("classTeacherDivision"));
            t.setClassTeacherRoomNo(rs.getString("classTeacherRoomNo"));
            return t;
        }, employeeId);

        if (teacher != null) {
            // Load qualifications
            String qualSql = "SELECT Qualification FROM Teacher_Qualification WHERE Teacher_ID = ?";
            List<String> quals = jdbcTemplate.queryForList(qualSql, String.class, employeeId);
            teacher.setQualifications(quals);

            // Load specializations
            String specSql = "SELECT Specialization FROM Teacher_Specialization WHERE Teacher_ID = ?";
            List<String> specs = jdbcTemplate.queryForList(specSql, String.class, employeeId);
            teacher.setSpecializations(specs);
        }

        return teacher;
    }

    // Get schedule entries for teacher
    public List<ScheduleItem> getSchedule(String employeeId) {
        String sql = "SELECT cs.Teacher_ID, cs.Subject_ID, s.Subject_Name, cs.Standard, cs.Division, sec.Room_No, " +
                     "(SELECT COUNT(*) FROM Enrollments e " +
                     " JOIN Student st ON e.Student_ID = st.Admission_No " +
                     " WHERE e.Subject_ID = cs.Subject_ID AND st.Standard = cs.Standard AND st.Division = cs.Division) AS enrolledCount " +
                     "FROM Class_Schedule cs " +
                     "JOIN Subject s ON cs.Subject_ID = s.Subject_Code " +
                     "JOIN Section sec ON cs.Standard = sec.Standard AND cs.Division = sec.Division " +
                     "WHERE cs.Teacher_ID = ? " +
                     "ORDER BY cs.Standard, cs.Division, s.Subject_Name";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ScheduleItem item = new ScheduleItem();
            item.setTeacherId(rs.getString("Teacher_ID"));
            item.setSubjectCode(rs.getString("Subject_ID"));
            item.setSubjectName(rs.getString("Subject_Name"));
            item.setStandard(rs.getString("Standard"));
            item.setDivision(rs.getString("Division"));
            item.setRoomNo(rs.getString("Room_No"));
            item.setEnrolledCount(rs.getInt("enrolledCount"));
            return item;
        }, employeeId);
    }

    // Get distinct class and section pairs associated with teacher (either taught or mentored)
    public List<String> getTeacherSections(String employeeId) {
        String sql = "SELECT DISTINCT CONCAT(Standard, '-', Division) AS sectionPair FROM (" +
                     " SELECT Standard, Division FROM Class_Schedule WHERE Teacher_ID = ? " +
                     " UNION " +
                     " SELECT Standard, Division FROM Section WHERE Class_Teacher = ?" +
                     ") AS combined ORDER BY sectionPair ASC";

        return jdbcTemplate.queryForList(sql, String.class, employeeId, employeeId);
    }

    // Get students enrolled in a specific course/subject for a specific class & section
    public List<com.example.SchoolWebsite.model.Student> getEnrolledStudentsForCourse(String subjectCode, String standard, String division) {
        String sql = "SELECT s.Admission_No AS admissionNo, s.Roll_No AS rollNo, s.First_Name AS firstName, " +
                     "s.Last_Name AS lastName, s.Standard AS standard, s.Division AS division, " +
                     "s.House AS house, e.Grade AS grade " +
                     "FROM Enrollments e " +
                     "JOIN Student s ON e.Student_ID = s.Admission_No " +
                     "WHERE e.Subject_ID = ? AND s.Standard = ? AND s.Division = ? " +
                     "ORDER BY s.Roll_No ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            com.example.SchoolWebsite.model.Student s = new com.example.SchoolWebsite.model.Student();
            s.setAdmissionNo(rs.getString("admissionNo"));
            s.setRollNo(rs.getInt("rollNo"));
            s.setFirstName(rs.getString("firstName"));
            s.setLastName(rs.getString("lastName"));
            s.setStandard(rs.getString("standard"));
            s.setDivision(rs.getString("division"));
            s.setHouse(rs.getString("house"));
            s.setGrade(rs.getString("grade"));
            return s;
        }, subjectCode, standard, division);
    }

    // Get subject name by code
    public String getSubjectName(String subjectCode) {
        String sql = "SELECT Subject_Name FROM Subject WHERE Subject_Code = ?";
        List<String> list = jdbcTemplate.queryForList(sql, String.class, subjectCode);
        return list.isEmpty() ? subjectCode : list.get(0);
    }

    // Get salary payment transactions for teacher
    public List<com.example.SchoolWebsite.model.Payment> getSalaryHistory(String employeeId) {
        String sql = "SELECT p.Transaction_ID, p.Date_Of_Payment, p.Payment_Type, " +
                     "p.Receiver, p.Sender, p.Payment_Mode, p.Amount " +
                     "FROM Teacher_Salary ts " +
                     "JOIN Payments p ON ts.Payment_ID = p.Transaction_ID " +
                     "WHERE ts.Teacher_ID = ? " +
                     "ORDER BY p.Date_Of_Payment DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            com.example.SchoolWebsite.model.Payment p = new com.example.SchoolWebsite.model.Payment();
            p.setTransactionId(rs.getString("Transaction_ID"));
            p.setDateOfPayment(rs.getString("Date_Of_Payment"));
            p.setPaymentType(rs.getString("Payment_Type"));
            p.setReceiver(rs.getString("Receiver"));
            p.setSender(rs.getString("Sender"));
            p.setPaymentMode(rs.getString("Payment_Mode"));
            p.setAmount(rs.getDouble("Amount"));
            return p;
        }, employeeId);
    }

    // Get all teachers for Admin faculty list
    public List<Teacher> findAll() {
        String sql = "SELECT t.Employee_ID, t.First_Name, t.Middle_Name, t.Last_Name, " +
                     "t.Date_Of_Joining, t.Phone_No, t.Email, t.Address, t.Salary, t.MentorOf, " +
                     "h.House_Name, " +
                     "sec.Standard AS classTeacherStandard, sec.Division AS classTeacherDivision " +
                     "FROM Teacher t " +
                     "LEFT JOIN House h ON t.MentorOf = h.Color " +
                     "LEFT JOIN Section sec ON t.Employee_ID = sec.Class_Teacher " +
                     "ORDER BY t.Employee_ID ASC";

        List<Teacher> teachers = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Teacher t = new Teacher();
            t.setEmployeeId(rs.getString("Employee_ID"));
            t.setFirstName(rs.getString("First_Name"));
            t.setMiddleName(rs.getString("Middle_Name"));
            t.setLastName(rs.getString("Last_Name"));
            t.setDateOfJoining(rs.getString("Date_Of_Joining"));
            t.setPhoneNo(rs.getString("Phone_No"));
            t.setEmail(rs.getString("Email"));
            t.setAddress(rs.getString("Address"));
            t.setSalary(rs.getDouble("Salary"));
            t.setMentorOf(rs.getString("MentorOf"));
            t.setHouseName(rs.getString("House_Name"));
            t.setClassTeacherStandard(rs.getString("classTeacherStandard"));
            t.setClassTeacherDivision(rs.getString("classTeacherDivision"));
            return t;
        });

        for (Teacher t : teachers) {
            String qualSql = "SELECT Qualification FROM Teacher_Qualification WHERE Teacher_ID = ?";
            t.setQualifications(jdbcTemplate.queryForList(qualSql, String.class, t.getEmployeeId()));

            String specSql = "SELECT Specialization FROM Teacher_Specialization WHERE Teacher_ID = ?";
            t.setSpecializations(jdbcTemplate.queryForList(specSql, String.class, t.getEmployeeId()));
        }

        return teachers;
    }

    // Save new Teacher
    public void save(Teacher t, List<String> qualifications, List<String> specializations) {
        String sql = "INSERT INTO Teacher (Employee_ID, First_Name, Middle_Name, Last_Name, " +
                     "Date_Of_Joining, Phone_No, Email, Address, Salary, MentorOf) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            t.getEmployeeId(),
            t.getFirstName(),
            t.getMiddleName() != null && t.getMiddleName().trim().isEmpty() ? null : t.getMiddleName(),
            t.getLastName() != null && t.getLastName().trim().isEmpty() ? null : t.getLastName(),
            t.getDateOfJoining(),
            t.getPhoneNo(),
            t.getEmail(),
            t.getAddress() != null && t.getAddress().trim().isEmpty() ? null : t.getAddress(),
            t.getSalary(),
            t.getMentorOf() != null && t.getMentorOf().trim().isEmpty() ? null : t.getMentorOf()
        );

        if (qualifications != null) {
            for (String q : qualifications) {
                if (q != null && !q.trim().isEmpty()) {
                    jdbcTemplate.update("INSERT INTO Teacher_Qualification (Qualification, Teacher_ID) VALUES (?, ?)", q.trim(), t.getEmployeeId());
                }
            }
        }

        if (specializations != null) {
            for (String s : specializations) {
                if (s != null && !s.trim().isEmpty()) {
                    jdbcTemplate.update("INSERT INTO Teacher_Specialization (Specialization, Teacher_ID) VALUES (?, ?)", s.trim(), t.getEmployeeId());
                }
            }
        }
    }

    // Update Teacher
    public void update(Teacher t, List<String> qualifications, List<String> specializations) {
        String sql = "UPDATE Teacher SET First_Name = ?, Middle_Name = ?, Last_Name = ?, " +
                     "Date_Of_Joining = ?, Phone_No = ?, Email = ?, Address = ?, Salary = ?, MentorOf = ? " +
                     "WHERE Employee_ID = ?";

        jdbcTemplate.update(sql,
            t.getFirstName(),
            t.getMiddleName() != null && t.getMiddleName().trim().isEmpty() ? null : t.getMiddleName(),
            t.getLastName() != null && t.getLastName().trim().isEmpty() ? null : t.getLastName(),
            t.getDateOfJoining(),
            t.getPhoneNo(),
            t.getEmail(),
            t.getAddress() != null && t.getAddress().trim().isEmpty() ? null : t.getAddress(),
            t.getSalary(),
            t.getMentorOf() != null && t.getMentorOf().trim().isEmpty() ? null : t.getMentorOf(),
            t.getEmployeeId()
        );

        // Replace qualifications
        jdbcTemplate.update("DELETE FROM Teacher_Qualification WHERE Teacher_ID = ?", t.getEmployeeId());
        if (qualifications != null) {
            for (String q : qualifications) {
                if (q != null && !q.trim().isEmpty()) {
                    jdbcTemplate.update("INSERT INTO Teacher_Qualification (Qualification, Teacher_ID) VALUES (?, ?)", q.trim(), t.getEmployeeId());
                }
            }
        }

        // Replace specializations
        jdbcTemplate.update("DELETE FROM Teacher_Specialization WHERE Teacher_ID = ?", t.getEmployeeId());
        if (specializations != null) {
            for (String s : specializations) {
                if (s != null && !s.trim().isEmpty()) {
                    jdbcTemplate.update("INSERT INTO Teacher_Specialization (Specialization, Teacher_ID) VALUES (?, ?)", s.trim(), t.getEmployeeId());
                }
            }
        }
    }

    // Delete Teacher
    public void delete(String employeeId) {
        jdbcTemplate.update("DELETE FROM Teacher WHERE Employee_ID = ?", employeeId);
    }

    // Total distinct students taught across all scheduled classes
    public int getTotalStudentsTaught(String employeeId) {
        String sql = "SELECT COUNT(DISTINCT e.Student_ID) " +
                     "FROM Enrollments e " +
                     "JOIN Student st ON e.Student_ID = st.Admission_No " +
                     "JOIN Class_Schedule cs ON cs.Subject_ID = e.Subject_ID AND cs.Standard = st.Standard AND cs.Division = st.Division " +
                     "WHERE cs.Teacher_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, employeeId);
        return count != null ? count : 0;
    }
}
