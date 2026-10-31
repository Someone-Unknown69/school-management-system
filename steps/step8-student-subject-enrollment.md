# Step 8: Assign Subjects to Students (Admin)

## 1. Overview
Step 8 introduces student course enrollment management, enabling administrators to view and configure a student's enrolled curriculum subjects, record academic grades, unenroll students from courses, and execute bulk section course enrollments.

---

## 2. Requirements Implemented
1. **Student Detail & Enrolled Subjects View (`/admin/students/{admissionNo}`)**:
   - Comprehensive student dossier (admission number, current section, house, fees balance).
   - "Enrolled Subjects" ledger showing all currently enrolled subjects with subject code, title, periods/week, assigned instructor, and reference textbooks.
2. **Individual Course Enrollment**:
   - Filtered dropdown displaying only subjects belonging to the student's current class (`Subject.Class = Student.Standard`) that the student is NOT already enrolled in.
   - Prevents duplicate enrollments with clear validation messages.
3. **Grade Recording & Editing**:
   - Inline grade selector supporting allowed grades: `A+`, `A`, `B+`, `B`, `C`, `D`, `F`, or empty (null).
   - Validates input and rejects any unauthorized grade strings.
4. **Course Unenrollment**:
   - Allows dropping an enrollment cleanly from the student's curriculum record.
5. **Bulk Section Enrollment Action**:
   - "Enroll Class [Standard]-[Division] in All Subjects of Class [Standard]" button.
   - In ONE database transaction, enrolls every student in that section in all curriculum subjects configured for that standard.
6. **Automated Tests**:
   - MockMvc tests verifying student detail rendering, course enrollment, duplicate rejection, grade editing, grade validation, and bulk enrollment.

---

## 3. Files Created & Modified

### Repository & Service Layer
- `src/main/java/com/example/SchoolWebsite/repository/EnrollmentRepository.java`:
  - Added queries `isStudentEnrolled(...)`, `enrollStudent(...)`, `unenrollStudent(...)`, `updateGrade(...)`, `findAvailableSubjectsForStudent(...)`, `findStudentsInSection(...)`, and `findSubjectCodesForClass(...)`.
- `src/main/java/com/example/SchoolWebsite/service/EnrollmentService.java`:
  - Added `ALLOWED_GRADES` whitelist, grade validation, `@Transactional` single-student enrollment/unenrollment, grade updates, and `@Transactional int bulkEnrollSection(...)`.

### Controller & Views
- `src/main/java/com/example/SchoolWebsite/controller/AdminController.java`:
  - Added endpoints `/admin/students/{admissionNo}`, `/admin/students/{admissionNo}/enroll`, `/admin/students/{admissionNo}/unenroll`, `/admin/students/{admissionNo}/grade`, `/admin/students/{admissionNo}/bulk-enroll-section`.
- `src/main/resources/templates/admin/student-detail.html`:
  - Dedicated student curriculum dashboard with course enrollments table, grade editor, and course enrollment form.
- `src/main/resources/templates/admin/students.html`:
  - Added "Enrollments" action button for every student row.

---

## 4. Verification & Testing

### Automated Tests
Run:
```bash
./mvnw test -Dtest=AdminFeaturesTest#testStudentDetail*,AdminFeaturesTest#test*Grade*,AdminFeaturesTest#test*Enroll*
```
Result: All tests pass.

### Manual Verification Steps
1. Log in as Admin (`admin` / `admin123`).
2. Go to **Manage Students** and click **Enrollments** on any student (e.g. `A001`).
3. View their current enrolled subjects and assigned grades.
4. Select a new grade (e.g., `A+`) and click **Set**: the grade updates immediately.
5. Select an available subject from the dropdown and click **Enroll Subject &rarr;**.
6. Click **Enroll Section in All Class Subjects** to test bulk section enrollment.

