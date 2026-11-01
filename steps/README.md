# Project Development Steps & Milestones

This folder contains the complete, step-by-step documentation of all development phases implemented for the **School Management System**:

| Milestone | Document | Summary | Test Status |
| :--- | :--- | :--- | :--- |
| **Step 1** | [step1-frontend-foundation.md](step1-frontend-foundation.md) | Bootstrap 5 + B&W design system, master Thymeleaf layout, UI fragments, and central `RoleInterceptor` security. | ✅ 17/17 tests passing |
| **Step 2** | [step2-template-restyling.md](step2-template-restyling.md) | Full restyling of all 22 Thymeleaf templates (tables, grid forms, delete confirmation modals, flash messages). | ✅ 17/17 tests passing |
| **Step 3** | [step3-schema-and-migration.md](step3-schema-and-migration.md) | Schema gap resolution, non-destructive SQL migration script, seed data generator updates, models/repos/services, form validations, and tests. | ✅ 21/21 tests passing |
| **Step 4** | [step4-fee-collection.md](step4-fee-collection.md) | Admin fee collection workflow, atomic `@Transactional` payment recording, printable receipt page with print stylesheet, fee dues dashboard, and student receipt links. | ✅ 31/31 tests passing |
| **Step 5** | [step5-salary-disbursement.md](step5-salary-disbursement.md) | Admin salary disbursement, atomic `@Transactional` payments, duplicate payment prevention for same month/year, bulk monthly payroll execution, printable salary slips, and teacher/staff self-service links. | ✅ 44/44 tests passing |
| **Step 6** | [step6-subject-management.md](step6-subject-management.md) | Subject catalog with class filtering, add/edit forms, textbook bibliography (`Reference_Books`), faculty assignment (`Teacher_Subject`), and dependency-protected safe deletion. | ✅ 62/62 tests passing |
| **Step 7** | [step7-classes-and-sections.md](step7-classes-and-sections.md) | Classes & sections dashboard with student headcounts, class teacher appointment with multi-section conflict prevention, and batch student grade promotions in one atomic transaction. | ✅ 62/62 tests passing |
| **Step 8** | [step8-student-subject-enrollment.md](step8-student-subject-enrollment.md) | Student course enrollment management, enrolled subjects ledger, course addition/dropping, academic grading (A+ to F), and bulk section-wide course enrollment. | ✅ 62/62 tests passing |
| **Step 9** | [step9-houses-module.md](step9-houses-module.md) | Houses dashboard with four B&W cards, tournament standings, house captain appointment (with strict house-membership validation), and faculty mentorship. | ✅ 72/72 tests passing |
| **Step 10** | [step10-student-overview.md](step10-student-overview.md) | Consolidated 360° student profile dossier linking Personal/Guardians, Academics, House, Transport, and Fees, complete with printable audit view. | ✅ 72/72 tests passing |
| **Step 11** | [step11-financial-statements.md](step11-financial-statements.md) | Financial performance statements over date ranges, monthly operating breakdown, transaction CSV export, and print-friendly institutional audit certificate. | ✅ 72/72 tests passing |
| **Step 12** | [step12-dashboards.md](step12-dashboards.md) | Role-based post-login dashboards (Admin, Teacher, Student, Staff/Driver) with KPI cards, pure monochrome Chart.js charts, timetable schedules, fee/salary summaries, and login redirects. | ✅ 81/81 tests passing |

---

## Quick Reference Commands

- **Run all automated tests:**
  ```bash
  ./mvnw clean test
  ```
- **Initialize database schema:**
  ```bash
  mysql -u root -p school_dbms < database/schema.sql
  ```
- **Regenerate seed data:**
  ```bash
  python3 database/generate_seed.py
  mysql -u root -p school_dbms < database/seed.sql
  ```
- **Run the Spring Boot application:**
  ```bash
  ./mvnw spring-boot:run
  ```

