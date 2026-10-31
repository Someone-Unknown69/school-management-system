package com.example.SchoolWebsite.model;

public class SectionInfo {
    private String standard;
    private String division;
    private String roomNo;
    private String classTeacherId;
    private String classTeacherName;
    private int studentCount;

    public SectionInfo() {}

    public SectionInfo(String standard, String division, String roomNo, String classTeacherId, String classTeacherName, int studentCount) {
        this.standard = standard;
        this.division = division;
        this.roomNo = roomNo;
        this.classTeacherId = classTeacherId;
        this.classTeacherName = classTeacherName;
        this.studentCount = studentCount;
    }

    public String getStandard() { return standard; }
    public void setStandard(String standard) { this.standard = standard; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public String getClassTeacherId() { return classTeacherId; }
    public void setClassTeacherId(String classTeacherId) { this.classTeacherId = classTeacherId; }

    public String getClassTeacherName() { return classTeacherName; }
    public void setClassTeacherName(String classTeacherName) { this.classTeacherName = classTeacherName; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) { this.studentCount = studentCount; }

    public String getSectionLabel() {
        return standard + "-" + division;
    }
}

