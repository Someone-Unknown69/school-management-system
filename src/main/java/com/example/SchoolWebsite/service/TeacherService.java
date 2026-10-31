package com.example.SchoolWebsite.service;

import com.example.SchoolWebsite.model.ScheduleItem;
import com.example.SchoolWebsite.model.Teacher;
import com.example.SchoolWebsite.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final com.example.SchoolWebsite.repository.UserRepository userRepository;

    public TeacherService(TeacherRepository teacherRepository, com.example.SchoolWebsite.repository.UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
    }

    public Teacher getFullProfile(String employeeId) {
        return teacherRepository.findFullProfile(employeeId);
    }

    public List<ScheduleItem> getSchedule(String employeeId) {
        return teacherRepository.getSchedule(employeeId);
    }

    public List<String> getTeacherSections(String employeeId) {
        return teacherRepository.getTeacherSections(employeeId);
    }

    public List<com.example.SchoolWebsite.model.Student> getEnrolledStudentsForCourse(String subjectCode, String standard, String division) {
        return teacherRepository.getEnrolledStudentsForCourse(subjectCode, standard, division);
    }

    public String getSubjectName(String subjectCode) {
        return teacherRepository.getSubjectName(subjectCode);
    }

    public List<com.example.SchoolWebsite.model.Payment> getSalaryHistory(String employeeId) {
        return teacherRepository.getSalaryHistory(employeeId);
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public void saveTeacher(Teacher t, List<String> qualifications, List<String> specializations) {
        teacherRepository.save(t, qualifications, specializations);
        userRepository.createOrUpdate(t.getEmployeeId(), "pass_" + t.getEmployeeId(), "teacher");
    }

    public void updateTeacher(Teacher t, List<String> qualifications, List<String> specializations) {
        teacherRepository.update(t, qualifications, specializations);
    }

    public void deleteTeacher(String employeeId) {
        userRepository.delete(employeeId);
        teacherRepository.delete(employeeId);
    }

    public int getTotalStudentsTaught(String employeeId) {
        return teacherRepository.getTotalStudentsTaught(employeeId);
    }
}
