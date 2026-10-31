package com.example.SchoolWebsite.repository;

import com.example.SchoolWebsite.model.SectionInfo;
import com.example.SchoolWebsite.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SectionRepository {

    private final JdbcTemplate jdbcTemplate;

    public SectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SectionInfo> findAllSections() {
        String sql = "SELECT sec.Standard, sec.Division, sec.Room_No, sec.Class_Teacher, " +
                     "CONCAT(t.First_Name, ' ', COALESCE(t.Last_Name, '')) AS teacherName, " +
                     "(SELECT COUNT(*) FROM Student s WHERE s.Standard = sec.Standard AND s.Division = sec.Division) AS studentCount " +
                     "FROM Section sec " +
                     "LEFT JOIN Teacher t ON sec.Class_Teacher = t.Employee_ID " +
                     "ORDER BY CAST(sec.Standard AS UNSIGNED), sec.Division ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SectionInfo info = new SectionInfo();
            info.setStandard(rs.getString("Standard"));
            info.setDivision(rs.getString("Division"));
            info.setRoomNo(rs.getString("Room_No"));
            info.setClassTeacherId(rs.getString("Class_Teacher"));
            String tName = rs.getString("teacherName");
            info.setClassTeacherName(tName != null && !tName.trim().isEmpty() ? tName.trim() : "Unassigned");
            info.setStudentCount(rs.getInt("studentCount"));
            return info;
        });
    }

    public SectionInfo findSection(String standard, String division) {
        String sql = "SELECT sec.Standard, sec.Division, sec.Room_No, sec.Class_Teacher, " +
                     "CONCAT(t.First_Name, ' ', COALESCE(t.Last_Name, '')) AS teacherName, " +
                     "(SELECT COUNT(*) FROM Student s WHERE s.Standard = sec.Standard AND s.Division = sec.Division) AS studentCount " +
                     "FROM Section sec " +
                     "LEFT JOIN Teacher t ON sec.Class_Teacher = t.Employee_ID " +
                     "WHERE sec.Standard = ? AND sec.Division = ?";

        List<SectionInfo> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            SectionInfo info = new SectionInfo();
            info.setStandard(rs.getString("Standard"));
            info.setDivision(rs.getString("Division"));
            info.setRoomNo(rs.getString("Room_No"));
            info.setClassTeacherId(rs.getString("Class_Teacher"));
            String tName = rs.getString("teacherName");
            info.setClassTeacherName(tName != null && !tName.trim().isEmpty() ? tName.trim() : "Unassigned");
            info.setStudentCount(rs.getInt("studentCount"));
            return info;
        }, standard, division);

        return list.isEmpty() ? null : list.get(0);
    }

    public SectionInfo findSectionByClassTeacher(String teacherId) {
        String sql = "SELECT sec.Standard, sec.Division, sec.Room_No, sec.Class_Teacher, " +
                     "CONCAT(t.First_Name, ' ', COALESCE(t.Last_Name, '')) AS teacherName, " +
                     "(SELECT COUNT(*) FROM Student s WHERE s.Standard = sec.Standard AND s.Division = sec.Division) AS studentCount " +
                     "FROM Section sec " +
                     "LEFT JOIN Teacher t ON sec.Class_Teacher = t.Employee_ID " +
                     "WHERE sec.Class_Teacher = ?";

        List<SectionInfo> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            SectionInfo info = new SectionInfo();
            info.setStandard(rs.getString("Standard"));
            info.setDivision(rs.getString("Division"));
            info.setRoomNo(rs.getString("Room_No"));
            info.setClassTeacherId(rs.getString("Class_Teacher"));
            String tName = rs.getString("teacherName");
            info.setClassTeacherName(tName != null && !tName.trim().isEmpty() ? tName.trim() : "Unassigned");
            info.setStudentCount(rs.getInt("studentCount"));
            return info;
        }, teacherId);

        return list.isEmpty() ? null : list.get(0);
    }

    public boolean sectionExists(String standard, String division) {
        String sql = "SELECT COUNT(*) FROM Section WHERE Standard = ? AND Division = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, standard, division);
        return count != null && count > 0;
    }

    public List<String> findSectionsMentoredByTeacher(String teacherId, String exceptStandard, String exceptDivision) {
        String sql = "SELECT CONCAT(Standard, '-', Division) FROM Section " +
                     "WHERE Class_Teacher = ? AND NOT (Standard = ? AND Division = ?)";
        return jdbcTemplate.queryForList(sql, String.class, teacherId, exceptStandard, exceptDivision);
    }

    public void updateClassTeacher(String standard, String division, String teacherId) {
        String sql = "UPDATE Section SET Class_Teacher = ? WHERE Standard = ? AND Division = ?";
        jdbcTemplate.update(sql, (teacherId != null && !teacherId.trim().isEmpty()) ? teacherId.trim() : null, standard, division);
    }

    public List<Student> findStudentsBySection(String standard, String division) {
        String sql = "SELECT Admission_No, Roll_No, First_Name, Middle_Name, Last_Name, " +
                     "Blood_Group, Standard, Division, House, Remaining_Fees " +
                     "FROM Student WHERE Standard = ? AND Division = ? " +
                     "ORDER BY Roll_No ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Student s = new Student();
            s.setAdmissionNo(rs.getString("Admission_No"));
            s.setRollNo(rs.getInt("Roll_No"));
            s.setFirstName(rs.getString("First_Name"));
            s.setMiddleName(rs.getString("Middle_Name"));
            s.setLastName(rs.getString("Last_Name"));
            s.setBloodGroup(rs.getString("Blood_Group"));
            s.setStandard(rs.getString("Standard"));
            s.setDivision(rs.getString("Division"));
            s.setHouse(rs.getString("House"));
            s.setRemainingFees(rs.getDouble("Remaining_Fees"));
            return s;
        }, standard, division);
    }

    public void updateStudentSection(String admissionNo, String newStandard, String newDivision) {
        String sql = "UPDATE Student SET Standard = ?, Division = ? WHERE Admission_No = ?";
        jdbcTemplate.update(sql, newStandard, newDivision, admissionNo);
    }
}

