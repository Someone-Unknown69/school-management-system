package com.example.SchoolWebsite.repository;

import com.example.SchoolWebsite.model.House;
import com.example.SchoolWebsite.model.Student;
import com.example.SchoolWebsite.model.Teacher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HouseRepository {

    private final JdbcTemplate jdbcTemplate;

    public HouseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<House> findAll() {
        String sql = "SELECT h.Color, h.House_Name, h.No_Of_Wins, " +
                     "(SELECT COUNT(*) FROM Teacher t WHERE t.MentorOf = h.Color) AS mentorCount, " +
                     "(SELECT COUNT(*) FROM Student s WHERE s.House = h.Color) AS studentCount " +
                     "FROM House h " +
                     "ORDER BY h.House_Name ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            House h = new House();
            h.setColor(rs.getString("Color"));
            h.setHouseName(rs.getString("House_Name"));
            h.setNoOfWins(rs.getInt("No_Of_Wins"));
            h.setMentorCount(rs.getInt("mentorCount"));
            h.setStudentCount(rs.getInt("studentCount"));
            return h;
        });
    }

    public House findByColor(String color) {
        String sql = "SELECT h.Color, h.House_Name, h.No_Of_Wins, " +
                     "(SELECT COUNT(*) FROM Teacher t WHERE t.MentorOf = h.Color) AS mentorCount, " +
                     "(SELECT COUNT(*) FROM Student s WHERE s.House = h.Color) AS studentCount " +
                     "FROM House h " +
                     "WHERE h.Color = ?";

        List<House> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            House h = new House();
            h.setColor(rs.getString("Color"));
            h.setHouseName(rs.getString("House_Name"));
            h.setNoOfWins(rs.getInt("No_Of_Wins"));
            h.setMentorCount(rs.getInt("mentorCount"));
            h.setStudentCount(rs.getInt("studentCount"));
            return h;
        }, color);

        if (list.isEmpty()) {
            return null;
        }

        House house = list.get(0);

        // Fetch house mentors
        String mentorSql = "SELECT t.Employee_ID, t.First_Name, t.Middle_Name, t.Last_Name, t.Email, t.Phone_No " +
                           "FROM Teacher t WHERE t.MentorOf = ? ORDER BY t.First_Name ASC";
        List<Teacher> mentors = jdbcTemplate.query(mentorSql, (rs, rowNum) -> {
            Teacher t = new Teacher();
            t.setEmployeeId(rs.getString("Employee_ID"));
            t.setFirstName(rs.getString("First_Name"));
            t.setMiddleName(rs.getString("Middle_Name"));
            t.setLastName(rs.getString("Last_Name"));
            t.setEmail(rs.getString("Email"));
            t.setPhoneNo(rs.getString("Phone_No"));
            return t;
        }, color);
        house.setMentors(mentors);

        // Fetch house students
        String studentSql = "SELECT s.Admission_No, s.Roll_No, s.First_Name, s.Middle_Name, s.Last_Name, " +
                            "s.Standard, s.Division, s.House, s.Remaining_Fees " +
                            "FROM Student s WHERE s.House = ? " +
                            "ORDER BY CAST(s.Standard AS UNSIGNED), s.Division, s.Roll_No ASC";
        List<Student> students = jdbcTemplate.query(studentSql, (rs, rowNum) -> {
            Student s = new Student();
            s.setAdmissionNo(rs.getString("Admission_No"));
            s.setRollNo(rs.getInt("Roll_No"));
            s.setFirstName(rs.getString("First_Name"));
            s.setMiddleName(rs.getString("Middle_Name"));
            s.setLastName(rs.getString("Last_Name"));
            s.setStandard(rs.getString("Standard"));
            s.setDivision(rs.getString("Division"));
            s.setHouse(rs.getString("House"));
            s.setRemainingFees(rs.getDouble("Remaining_Fees"));
            return s;
        }, color);
        house.setStudents(students);

        return house;
    }

    public List<Teacher> getAvailableMentors(String color) {
        String sql = "SELECT t.Employee_ID, t.First_Name, t.Middle_Name, t.Last_Name, t.Email, t.Phone_No, t.MentorOf " +
                     "FROM Teacher t WHERE t.MentorOf IS NULL OR t.MentorOf != ? " +
                     "ORDER BY t.First_Name ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Teacher t = new Teacher();
            t.setEmployeeId(rs.getString("Employee_ID"));
            t.setFirstName(rs.getString("First_Name"));
            t.setMiddleName(rs.getString("Middle_Name"));
            t.setLastName(rs.getString("Last_Name"));
            t.setEmail(rs.getString("Email"));
            t.setPhoneNo(rs.getString("Phone_No"));
            t.setMentorOf(rs.getString("MentorOf"));
            return t;
        }, color);
    }

    public void updateTournamentsWon(String color, int wins) {
        String sql = "UPDATE House SET No_Of_Wins = ? WHERE Color = ?";
        jdbcTemplate.update(sql, Math.max(0, wins), color);
    }

    public void addMentor(String color, String teacherId) {
        String sql = "UPDATE Teacher SET MentorOf = ? WHERE Employee_ID = ?";
        jdbcTemplate.update(sql, color, teacherId);
    }

    public void removeMentor(String color, String teacherId) {
        String sql = "UPDATE Teacher SET MentorOf = NULL WHERE Employee_ID = ? AND MentorOf = ?";
        jdbcTemplate.update(sql, teacherId, color);
    }
}

