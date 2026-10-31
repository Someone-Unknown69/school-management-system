package com.example.SchoolWebsite.service;

import com.example.SchoolWebsite.model.Enrollment;
import com.example.SchoolWebsite.model.Subject;
import com.example.SchoolWebsite.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class EnrollmentService {

    public static final Set<String> ALLOWED_GRADES = Set.of(
            "A+", "A", "B+", "B", "C", "D", "F", ""
    );

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Enrollment> getEnrolledSubjects(String studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public void validateGrade(String grade) {
        if (grade != null && !grade.trim().isEmpty()) {
            String upper = grade.trim().toUpperCase();
            if (!ALLOWED_GRADES.contains(upper)) {
                throw new IllegalArgumentException("Invalid grade '" + grade + "'. Allowed grades are: A+, A, B+, B, C, D, F or empty.");
            }
        }
    }

    @Transactional
    public void enrollStudent(String studentId, String subjectCode, String grade) {
        if (studentId == null || subjectCode == null || studentId.trim().isEmpty() || subjectCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID and Subject Code are required.");
        }
        String sId = studentId.trim();
        String subCode = subjectCode.trim();

        if (enrollmentRepository.isStudentEnrolled(sId, subCode)) {
            throw new IllegalArgumentException("Student is already enrolled in subject " + subCode + ".");
        }

        validateGrade(grade);
        enrollmentRepository.enrollStudent(sId, subCode, grade);
    }

    @Transactional
    public void unenrollStudent(String studentId, String subjectCode) {
        if (studentId == null || subjectCode == null || studentId.trim().isEmpty() || subjectCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID and Subject Code are required.");
        }
        enrollmentRepository.unenrollStudent(studentId.trim(), subjectCode.trim());
    }

    @Transactional
    public void updateGrade(String studentId, String subjectCode, String grade) {
        if (studentId == null || subjectCode == null || studentId.trim().isEmpty() || subjectCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID and Subject Code are required.");
        }
        validateGrade(grade);
        enrollmentRepository.updateGrade(studentId.trim(), subjectCode.trim(), grade);
    }

    public List<Subject> getAvailableSubjectsForStudent(String studentId, String standard) {
        if (studentId == null || standard == null) return List.of();
        return enrollmentRepository.findAvailableSubjectsForStudent(studentId.trim(), standard.trim());
    }

    @Transactional
    public int bulkEnrollSection(String standard, String division) {
        if (standard == null || division == null || standard.trim().isEmpty() || division.trim().isEmpty()) {
            throw new IllegalArgumentException("Standard and Division are required for bulk enrollment.");
        }

        List<String> studentIds = enrollmentRepository.findStudentsInSection(standard.trim(), division.trim());
        List<String> subjectCodes = enrollmentRepository.findSubjectCodesForClass(standard.trim());

        int count = 0;
        for (String stId : studentIds) {
            for (String subCode : subjectCodes) {
                if (!enrollmentRepository.isStudentEnrolled(stId, subCode)) {
                    enrollmentRepository.enrollStudent(stId, subCode, null);
                    count++;
                }
            }
        }
        return count;
    }
}
