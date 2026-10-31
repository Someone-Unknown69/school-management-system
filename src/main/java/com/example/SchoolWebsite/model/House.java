package com.example.SchoolWebsite.model;

import java.util.ArrayList;
import java.util.List;

public class House {
    private String color;
    private String houseName;
    private int noOfWins;
    private String captainId;
    private String captainName;
    private int mentorCount;
    private int studentCount;
    private List<Teacher> mentors = new ArrayList<>();
    private List<Student> students = new ArrayList<>();

    public House() {}

    public House(String color, String houseName, int noOfWins, String captainId) {
        this.color = color;
        this.houseName = houseName;
        this.noOfWins = noOfWins;
        this.captainId = captainId;
    }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getHouseName() { return houseName; }
    public void setHouseName(String houseName) { this.houseName = houseName; }

    public int getNoOfWins() { return noOfWins; }
    public void setNoOfWins(int noOfWins) { this.noOfWins = noOfWins; }

    public String getCaptainId() { return captainId; }
    public void setCaptainId(String captainId) { this.captainId = captainId; }

    public String getCaptainName() { return captainName; }
    public void setCaptainName(String captainName) { this.captainName = captainName; }

    public int getMentorCount() { return mentorCount; }
    public void setMentorCount(int mentorCount) { this.mentorCount = mentorCount; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public List<Teacher> getMentors() { return mentors; }
    public void setMentors(List<Teacher> mentors) { this.mentors = mentors; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
}
