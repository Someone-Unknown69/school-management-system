package com.example.SchoolWebsite.service;

import com.example.SchoolWebsite.model.Student;
import com.example.SchoolWebsite.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final com.example.SchoolWebsite.repository.UserRepository userRepository;

    public StudentService(StudentRepository studentRepository, com.example.SchoolWebsite.repository.UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student findByAdmissionNo(String admissionNo) {
        return studentRepository.findByAdmissionNo(admissionNo);
    }

    public List<Student> findByStandardAndDivision(String standard, String division) {
        return studentRepository.findByStandardAndDivision(standard, division);
    }

    // Full profile with all JOINed data (for the student profile page)
    public Student getFullProfile(String admissionNo) {
        return studentRepository.findFullProfile(admissionNo);
    }

    public List<Student> searchAndFilter(String standard, String division, String search) {
        return studentRepository.searchAndFilter(standard, division, search);
    }

    public void saveStudent(Student s, String guardianRel, String gFirst, String gMiddle, String gLast, String gOcc, String gPhone, String gEmail) {
        studentRepository.save(s);
        userRepository.createOrUpdate(s.getAdmissionNo(), "pass_" + s.getAdmissionNo(), "student");
        if (guardianRel != null && !guardianRel.trim().isEmpty() && gFirst != null && !gFirst.trim().isEmpty()) {
            studentRepository.saveGuardian(s.getAdmissionNo(), guardianRel.trim(), gFirst.trim(), gMiddle, gLast, gOcc != null ? gOcc.trim() : "", gPhone != null ? gPhone.trim() : "", gEmail != null ? gEmail.trim() : "");
        }
    }

    public void saveStudent(Student s, String guardianRel, String gFirst, String gMiddle, String gLast, String gOcc, String gPhone) {
        saveStudent(s, guardianRel, gFirst, gMiddle, gLast, gOcc, gPhone, null);
    }

    public void updateStudent(Student s, String guardianRel, String gFirst, String gMiddle, String gLast, String gOcc, String gPhone, String gEmail) {
        studentRepository.update(s);
        if (guardianRel != null && !guardianRel.trim().isEmpty() && gFirst != null && !gFirst.trim().isEmpty()) {
            studentRepository.saveGuardian(s.getAdmissionNo(), guardianRel.trim(), gFirst.trim(), gMiddle, gLast, gOcc != null ? gOcc.trim() : "", gPhone != null ? gPhone.trim() : "", gEmail != null ? gEmail.trim() : "");
        }
    }

    public void updateStudent(Student s, String guardianRel, String gFirst, String gMiddle, String gLast, String gOcc, String gPhone) {
        updateStudent(s, guardianRel, gFirst, gMiddle, gLast, gOcc, gPhone, null);
    }

    public void deleteStudent(String admissionNo) {
        userRepository.delete(admissionNo);
        studentRepository.delete(admissionNo);
    }

    public List<String> getAvailableClasses() {
        return studentRepository.getAvailableClasses();
    }

    public List<String> getAvailableDivisions() {
        return studentRepository.getAvailableDivisions();
    }

    public List<java.util.Map<String, Object>> getAvailableSections() {
        return studentRepository.getAvailableSections();
    }

    public List<java.util.Map<String, Object>> getAvailableHouses() {
        return studentRepository.getAvailableHouses();
    }

    public List<java.util.Map<String, Object>> getAvailableVehicles() {
        return studentRepository.getAvailableVehicles();
    }

    public List<Student> getStudentsWithDues(String standard, String division, String search) {
        return studentRepository.findStudentsWithDues(standard, division, search);
    }

    public List<Student> getAllStudentsWithDues() {
        return studentRepository.findAllStudentsWithDues();
    }

    public double getTotalOutstandingDues(String standard, String division, String search) {
        return studentRepository.getTotalOutstandingDues(standard, division, search);
    }
}
