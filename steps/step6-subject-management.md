# Step 6: Subject Management (Admin)

## 1. Overview
Step 6 provides comprehensive curriculum subject administration for school administrators, including subject creation, edits, reference books bibliography management, faculty assignments (`Teacher_Subject`), and dependency-aware safe deletion.

---

## 2. Requirements Implemented
1. **Subjects Catalog & Class Filtering (`/admin/subjects`)**:
   - Filter subjects by target class level (e.g. Class 1 to 12) or view all.
   - Summaries of weekly periods, student enrollment count, and active timetable schedules.
2. **Add & Edit Subject Forms (`/admin/subjects/new`, `/admin/subjects/edit/{code}`)**:
   - Fields: `Subject_Code` (unique PK), `Subject_Name`, `Class` (target standard), and `Periods_Per_Week` (> 0).
   - Reference Books: multiline input where administrators can add or remove multiple textbook references, stored in the `Reference_Books` table.
3. **Subject Detail & Faculty Mapping (`/admin/subjects/{code}`)**:
   - Displays complete syllabus specifications, bibliography, and timetable stats.
   - Manages `Teacher_Subject` authorization:
     - Lists all faculty members currently mapped to teach the subject.
     - Adds a teacher from an unassigned teachers dropdown.
     - Removes an assigned teacher with one click.
4. **Safe Deletion Protection**:
   - Blocks deletion if any students are enrolled (`Enrollments` count > 0) with a clear error message.
   - Blocks deletion if the subject is actively assigned in any class timetable (`Class_Schedule` count > 0).
   - If unencumbered, deletes mapping records and the subject cleanly.
5. **Sidebar Navigation & Test Suite**:
   - Added "Subjects" link to Admin portal navigation.
   - 9 new automated MockMvc tests added covering list, filter, create, duplicate rejection, edit, detail, faculty mapping, and protected deletion.

---

## 3. Files Created & Modified

### Models
- `src/main/java/com/example/SchoolWebsite/model/Subject.java`:
  - Encapsulates subject metadata, reference books, assigned teachers, enrollment count, and schedule count.

### Repository Layer
- `src/main/java/com/example/SchoolWebsite/repository/SubjectRepository.java`:
  - Parameterized SQL queries for `findAll(classFilter)`, `findByCode(code)`, `existsByCode(code)`, `save(...)`, `update(...)`, `delete(...)`, `addTeacher(...)`, `removeTeacher(...)`, `getUnassignedTeachers(...)`, `countEnrollments(...)`, and `countScheduleEntries(...)`.

### Service Layer
- `src/main/java/com/example/SchoolWebsite/service/SubjectService.java`:
  - Business logic and `@Transactional` methods for creating, updating, and safely deleting subjects.

### Controller & Views
- `src/main/java/com/example/SchoolWebsite/controller/AdminController.java`:
  - Added endpoints `/admin/subjects`, `/admin/subjects/new`, `/admin/subjects/edit/{code}`, `/admin/subjects/{code}`, `/admin/subjects/{code}/teachers/add`, `/admin/subjects/{code}/teachers/remove`, `/admin/subjects/delete/{code}`.
- `src/main/resources/templates/admin/subjects.html`:
  - B&W catalog table with class level dropdown filter and delete modal.
- `src/main/resources/templates/admin/subject-form.html`:
  - Responsive form with input validation and multiline textbook reference bibliography.
- `src/main/resources/templates/admin/subject-detail.html`:
  - Detail dossier and `Teacher_Subject` management interface.
- `src/main/resources/templates/fragments/layout.html`:
  - Added "Subjects" sidebar link.

---

## 4. Verification & Testing

### Automated Tests
Run:
```bash
./mvnw test -Dtest=AdminFeaturesTest#testListSubjects*,AdminFeaturesTest#test*Subject*
```
Result: All tests pass cleanly.

### Manual Verification Steps
1. Log in as Admin (`admin` / `admin123`).
2. Click **Subjects** in the sidebar.
3. Filter by Class (e.g. Class 10).
4. Click **+ New Subject** and create a test subject with 2 reference books.
5. Click **Details & Teachers** on any subject to view textbook references and assign/remove a teacher.
6. Attempt to delete an enrolled subject like `MATH10`: notice the system prevents deletion and shows the active student count.

