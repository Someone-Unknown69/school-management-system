package com.example.SchoolWebsite.model;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private String employeeId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dateOfJoining;
    private String phoneNo;
    private String email;
    private String address;
    private double salary;
    private String mentorOf;
    private String houseName;

    // Section where teacher is appointed as Class Teacher
    private String classTeacherStandard;
    private String classTeacherDivision;
    private String classTeacherRoomNo;

    private List<String> qualifications = new ArrayList<>();
    private List<String> specializations = new ArrayList<>();

    public Teacher() {}

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) sb.append(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) sb.append(" ").append(middleName);
        if (lastName != null && !lastName.trim().isEmpty()) sb.append(" ").append(lastName);
        return sb.toString();
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(String dateOfJoining) { this.dateOfJoining = dateOfJoining; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getMentorOf() { return mentorOf; }
    public void setMentorOf(String mentorOf) { this.mentorOf = mentorOf; }

    public String getHouseName() { return houseName; }
    public void setHouseName(String houseName) { this.houseName = houseName; }

    public String getClassTeacherStandard() { return classTeacherStandard; }
    public void setClassTeacherStandard(String classTeacherStandard) { this.classTeacherStandard = classTeacherStandard; }

    public String getClassTeacherDivision() { return classTeacherDivision; }
    public void setClassTeacherDivision(String classTeacherDivision) { this.classTeacherDivision = classTeacherDivision; }

    public String getClassTeacherRoomNo() { return classTeacherRoomNo; }
    public void setClassTeacherRoomNo(String classTeacherRoomNo) { this.classTeacherRoomNo = classTeacherRoomNo; }

    public List<String> getQualifications() { return qualifications; }
    public void setQualifications(List<String> qualifications) { this.qualifications = qualifications; }

    public List<String> getSpecializations() { return specializations; }
    public void setSpecializations(List<String> specializations) { this.specializations = specializations; }
}
