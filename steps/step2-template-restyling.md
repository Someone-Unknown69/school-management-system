# Step 2: Full Template Restyling

> **Test Suite Status:** `Tests run: 17, Failures: 0, Errors: 0` ✅ **BUILD SUCCESS**

---

## 1. Overview & Objective
Refactor and restyle all 22 existing server-side Thymeleaf templates to adopt the Step 1 layout, fragments, and black-and-white design system while keeping all controller logic, models, and tests intact.

Key requirements:
1. **Strictly Black and White**: Grayscale palette only (`#000`, `#fff`, `#f5f5f5`, `#e0e0e0`, `#666`). Status indicators use text, borders, or filled-versus-outlined badges instead of colors.
2. **Responsive Tables**: Every table wrapped in `.table-responsive` with uppercase 11px light-gray headers (`#f5f5f5`) and 1px borders.
3. **Responsive Forms**: Converted HTML `<table>` forms into Bootstrap grid systems (`.row.g-3`, `.col-md-4`) with labels, required asterisks, and inline `.form-text` validation messages.
4. **Flash Messages Fragment**: Standardized alert delivery across all templates via `<div th:replace="~{fragments/ui :: flash-messages}"></div>`.
5. **Delete Confirmation via Modal**: Replaced intrusive browser alert popups with styled monochrome Bootstrap modals (`confirm-modal`).
6. **Zero Backend Logic Drift**: Preserved existing controller endpoints, model attribute names, and database interaction intact.

---

## 2. Inventory of Restyled Templates

### A. Shared & Root Templates
- [`templates/students.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/students.html) — General class directory with badge indicators and responsive table.
- [`templates/index.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/index.html) — Home portal with KPI cards, role quick links, and authentication banners.
- [`templates/login.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/login.html) — Monochrome centered login box with test credential hints.

### B. Student Portal (`templates/student/`)
- [`student/profile.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/student/profile.html) — Comprehensive profile with personal, academic, guardian, and bus route panels.
- [`student/subjects.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/student/subjects.html) — Enrolled courses, teacher allocations, reference books, and letter grades.
- [`student/fees.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/student/fees.html) — Outstanding balance card, date range filter bar, and payment receipt ledger.

### C. Teacher Portal (`templates/teacher/`)
- [`teacher/profile.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/teacher/profile.html) — Employment details, house mentorship, class teacher designation, and credentials.
- [`teacher/schedule.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/teacher/schedule.html) — Course timetable with classroom locations, student counts, and direct roster links.
- [`teacher/students.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/teacher/students.html) — Homeroom student directory with fee status chips; handles non-class-teacher state gracefully.
- [`teacher/course-students.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/teacher/course-students.html) — Section student roster for a specific subject course.
- [`teacher/salary.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/teacher/salary.html) — Contracted wage KPI card and historical disbursed salary bank transactions.

### D. Staff & Driver Portal (`templates/staff/`)
- [`staff/profile.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/staff/profile.html) — Staff employment profile and driver vehicle route card.
- [`staff/salary.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/staff/salary.html) — Staff salary transaction ledger.
- [`staff/transport.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/staff/transport.html) — Driver bus passenger roster with vehicle capacity and occupancy metrics.

### E. Admin Portal (`templates/admin/`)
- [`admin/students.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/students.html) — Student search/filter bar, responsive table, and confirm delete modal.
- [`admin/student-form.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/student-form.html) — 2-column responsive form for student enrollment and guardian details.
- [`admin/faculty.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/faculty.html) — Monochrome tabs switching between Teachers and Operational Staff with delete modals.
- [`admin/teacher-form.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/teacher-form.html) — Teacher employment form with qualifications and specializations.
- [`admin/staff-form.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/staff-form.html) — Staff registration form with role selection dropdown.
- [`admin/transactions.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/transactions.html) — Multi-criteria filter bar, revenue/payout KPI cards, and paginated ledger.
- [`admin/transport.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/transport.html) — Bus fleet table, occupancy tracking, and interactive passenger assignment/removal.
- [`admin/vehicle-form.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/vehicle-form.html) — School bus vehicle and route configuration form.

---

## 3. Verification & Testing

1. Run the test suite:
   ```bash
   ./mvnw test
   ```
   *Expected:* 17 tests run, 0 failures, 0 errors.

2. Launch the application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Verification checkpoints:
   - **Admin Portal**: Check `/admin/students`, `/admin/faculty`, `/admin/transactions`, `/admin/transport`. Test the delete confirm modal on table rows.
   - **Student Portal**: Check `/student/profile`, `/student/subjects`, `/student/fees`. Check fee badge states (`badge-filled` for settled, `badge-outlined` for dues).
   - **Teacher Portal**: Check `/teacher/profile`, `/teacher/schedule`, `/teacher/students`, `/teacher/salary`.
   - **Staff Portal**: Check `/staff/profile`, `/staff/transport`, `/staff/salary`.
   - **Mobile Responsiveness**: Resize viewport below 992px; check sidebar collapse and table horizontal scroll containers.

