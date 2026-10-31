package com.example.SchoolWebsite.model;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String subjectCode;
    private String subjectName;
    private int periodsPerWeek;
    private String className; // maps to Subject.Class in DB
    private List<String> referenceBooks = new ArrayList<>();
    private List<Teacher> teachers = new ArrayList<>();
    private int enrollmentCount;
    private int scheduleCount;

    public Subject() {}

    public Subject(String subjectCode, String subjectName, int periodsPerWeek, String className) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.periodsPerWeek = periodsPerWeek;
        this.className = className;
    }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getPeriodsPerWeek() { return periodsPerWeek; }
    public void setPeriodsPerWeek(int periodsPerWeek) { this.periodsPerWeek = periodsPerWeek; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public List<String> getReferenceBooks() { return referenceBooks; }
    public void setReferenceBooks(List<String> referenceBooks) { this.referenceBooks = referenceBooks; }

    public List<Teacher> getTeachers() { return teachers; }
    public void setTeachers(List<Teacher> teachers) { this.teachers = teachers; }

    public int getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(int enrollmentCount) { this.enrollmentCount = enrollmentCount; }

    public int getScheduleCount() { return scheduleCount; }
    public void setScheduleCount(int scheduleCount) { this.scheduleCount = scheduleCount; }
}

