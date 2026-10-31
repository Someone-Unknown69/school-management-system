package com.example.SchoolWebsite.service;

import com.example.SchoolWebsite.model.SectionInfo;
import com.example.SchoolWebsite.model.Student;
import com.example.SchoolWebsite.model.Teacher;
import com.example.SchoolWebsite.repository.SectionRepository;
import com.example.SchoolWebsite.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final TeacherRepository teacherRepository;

    public SectionService(SectionRepository sectionRepository, TeacherRepository teacherRepository) {
        this.sectionRepository = sectionRepository;
        this.teacherRepository = teacherRepository;
    }

    public List<SectionInfo> getAllSections() {
        return sectionRepository.findAllSections();
    }

    public SectionInfo getSection(String standard, String division) {
        return sectionRepository.findSection(standard, division);
    }

    public SectionInfo getSectionByClassTeacher(String teacherId) {
        return sectionRepository.findSectionByClassTeacher(teacherId);
    }

    @Transactional
    public void appointClassTeacher(String standard, String division, String teacherId) {
        if (!sectionRepository.sectionExists(standard, division)) {
            throw new IllegalArgumentException("Section " + standard + "-" + division + " does not exist.");
        }

        if (teacherId != null && !teacherId.trim().isEmpty()) {
            String trimmedTeacherId = teacherId.trim();
            Teacher teacher = teacherRepository.findFullProfile(trimmedTeacherId);
            if (teacher == null) {
                throw new IllegalArgumentException("Teacher not found with Employee ID: " + trimmedTeacherId);
            }

            // Prevent one teacher being class teacher of two sections
            List<String> conflicts = sectionRepository.findSectionsMentoredByTeacher(trimmedTeacherId, standard, division);
            if (!conflicts.isEmpty()) {
                throw new IllegalArgumentException(String.format(
                        "Teacher %s (%s) is already appointed as the class teacher for Section %s. A teacher cannot lead multiple sections.",
                        teacher.getFullName(), trimmedTeacherId, conflicts.get(0)));
            }

            sectionRepository.updateClassTeacher(standard, division, trimmedTeacherId);
        } else {
            // Unassign class teacher
            sectionRepository.updateClassTeacher(standard, division, null);
        }
    }

    public List<Student> getStudentsInSection(String standard, String division) {
        return sectionRepository.findStudentsBySection(standard, division);
    }

    @Transactional
    public int promoteStudents(String sourceStandard, String sourceDivision,
                               String targetStandard, String targetDivision,
                               List<String> studentIds) {

        if (studentIds == null || studentIds.isEmpty()) {
            throw new IllegalArgumentException("No students were selected for promotion.");
        }

        // Validate target section existence
        if (!sectionRepository.sectionExists(targetStandard, targetDivision)) {
            throw new IllegalArgumentException(String.format(
                    "Target section Class %s-%s does not exist in the school registry.", targetStandard, targetDivision));
        }

        // Validate not promoting beyond Class 12
        try {
            int srcStd = Integer.parseInt(sourceStandard.trim());
            int tgtStd = Integer.parseInt(targetStandard.trim());
            if (tgtStd > 12) {
                throw new IllegalArgumentException("Promotion beyond Class 12 is not permitted (students graduate after Class 12).");
            }
            if (tgtStd < srcStd) {
                throw new IllegalArgumentException("Target class (" + tgtStd + ") cannot be lower than current class (" + srcStd + ").");
            }
        } catch (NumberFormatException ignored) {}

        for (String admNo : studentIds) {
            if (admNo != null && !admNo.trim().isEmpty()) {
                sectionRepository.updateStudentSection(admNo.trim(), targetStandard.trim(), targetDivision.trim());
            }
        }

        return studentIds.size();
    }
}

