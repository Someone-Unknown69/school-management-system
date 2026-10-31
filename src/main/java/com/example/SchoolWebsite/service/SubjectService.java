package com.example.SchoolWebsite.service;

import com.example.SchoolWebsite.model.Subject;
import com.example.SchoolWebsite.model.Teacher;
import com.example.SchoolWebsite.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> getAllSubjects(String classFilter) {
        return subjectRepository.findAll(classFilter);
    }

    public Subject getSubject(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        return subjectRepository.findByCode(code.trim());
    }

    @Transactional
    public void createSubject(Subject subject, List<String> referenceBooks) {
        if (subject.getSubjectCode() == null || subject.getSubjectCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required.");
        }
        String code = subject.getSubjectCode().trim().toUpperCase();
        subject.setSubjectCode(code);

        if (subjectRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Subject with code '" + code + "' already exists.");
        }
        if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name is required.");
        }
        if (subject.getPeriodsPerWeek() <= 0) {
            throw new IllegalArgumentException("Periods per week must be greater than zero.");
        }
        if (subject.getClassName() == null || subject.getClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("Class level is required.");
        }

        subjectRepository.save(subject, referenceBooks);
    }

    @Transactional
    public void updateSubject(Subject subject, List<String> referenceBooks) {
        if (subject.getSubjectCode() == null || subject.getSubjectCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required.");
        }
        String code = subject.getSubjectCode().trim();
        subject.setSubjectCode(code);

        if (!subjectRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Subject '" + code + "' does not exist.");
        }
        if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name is required.");
        }
        if (subject.getPeriodsPerWeek() <= 0) {
            throw new IllegalArgumentException("Periods per week must be greater than zero.");
        }
        if (subject.getClassName() == null || subject.getClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("Class level is required.");
        }

        subjectRepository.update(subject, referenceBooks);
    }

    @Transactional
    public void deleteSubject(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required.");
        }
        String trimmedCode = code.trim();
        if (!subjectRepository.existsByCode(trimmedCode)) {
            throw new IllegalArgumentException("Subject '" + trimmedCode + "' does not exist.");
        }

        int enrollments = subjectRepository.countEnrollments(trimmedCode);
        if (enrollments > 0) {
            throw new IllegalStateException(String.format(
                    "Cannot delete subject '%s': %d student(s) are currently enrolled in it.", trimmedCode, enrollments));
        }

        int scheduleEntries = subjectRepository.countScheduleEntries(trimmedCode);
        if (scheduleEntries > 0) {
            throw new IllegalStateException(String.format(
                    "Cannot delete subject '%s': It is scheduled in %d active class schedule(s).", trimmedCode, scheduleEntries));
        }

        subjectRepository.delete(trimmedCode);
    }

    public void addTeacherToSubject(String subjectCode, String teacherId) {
        if (subjectCode == null || teacherId == null || subjectCode.trim().isEmpty() || teacherId.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code and Teacher ID are required.");
        }
        String sCode = subjectCode.trim();
        String tId = teacherId.trim();

        if (subjectRepository.isTeacherAssigned(sCode, tId)) {
            throw new IllegalArgumentException("Teacher " + tId + " is already assigned to subject " + sCode + ".");
        }

        subjectRepository.addTeacher(sCode, tId);
    }

    public void removeTeacherFromSubject(String subjectCode, String teacherId) {
        if (subjectCode == null || teacherId == null || subjectCode.trim().isEmpty() || teacherId.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code and Teacher ID are required.");
        }
        subjectRepository.removeTeacher(subjectCode.trim(), teacherId.trim());
    }

    public List<Teacher> getUnassignedTeachers(String subjectCode) {
        return subjectRepository.getUnassignedTeachers(subjectCode);
    }

    public List<String> getAvailableClasses() {
        return subjectRepository.getAvailableClasses();
    }
}

