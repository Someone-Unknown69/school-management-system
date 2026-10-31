# Step 12: Role-Based Dashboards & Monochrome Visualizations

## 1. Overview
Step 12 introduces role-tailored dashboards for all four system roles (**Admin**, **Teacher**, **Student**, and **Staff / Driver**) immediately upon login. Each dashboard is designed following the strict **black-and-white design system** (`#000`, `#fff`, `#f5f5f5`, `#e0e0e0`, `#666`) and incorporates responsive layouts, KPI stat cards, fast quick action launchers, and pure monochrome Chart.js visualizations.

---

## 2. Requirements Implemented

### 1. Role-Based Login Redirection
- **Unified Auth Routing**:
  - `POST /login` and `GET /login` redirect authenticated users directly to their respective home dashboard:
    - `admin` &rarr; `/admin/dashboard`
    - `teacher` &rarr; `/teacher/dashboard`
    - `student` &rarr; `/student/dashboard`
    - `staff` &rarr; `/staff/dashboard`
  - Root route `GET /` redirects authenticated sessions to their specific role dashboard while presenting the public institutional landing page to unauthenticated visitors.

### 2. Admin Dashboard (`/admin/dashboard` and `/admin`)
- **6 Stat Cards**:
  1. **Total Students**: Active student enrollment across classes 9–12.
  2. **Faculty & Staff**: Combined employee count with teacher and staff breakdown.
  3. **Fees Collected**: Total institutional fee revenue collected to date (₹).
  4. **Outstanding Fee Dues**: Total remaining fee balances across all enrolled students (₹).
  5. **Transport Fleet**: Count of operational school buses and total passenger capacity.
  6. **Capacity Utilization**: Current percentage and passenger ratio (`assigned / capacity`) with a black-and-white visual progress bar.
- **2 Monochrome Chart.js Visualizations**:
  1. **Students Per Class**: Bar chart depicting student counts across Class 9, 10, 11, and 12 using solid black bars (`#000000`), white background, and light gray gridlines (`#e0e0e0`). Zero chromatic colors.
  2. **Monthly Fee Collections**: Revenue flow bar chart tracking month-by-month fee receipts using charcoal gray fills (`#666666`), black borders, custom currency tooltip formatters, and gray gridlines.
- **Recent Transactions Ledger Table**:
  - Displays the last 6 payments across fee receipts and employee salary disbursements with direct links to view or print payment receipts and salary slips.
- **Administrative Quick Action Launchers**:
  - Direct tiles for: Record Fee, Pay Salary, Fee Dues, New Student, Add Subject, Sections, Promote Students, Houses, Financial Statements, and Export CSV.

### 3. Teacher Dashboard (`/teacher/dashboard`)
- **Active Timetable & Schedule**:
  - Displays today's scheduled classes and weekly timetable: Standard, Division, Subject Code, Subject Name, Classroom Number, and enrolled student strength.
  - One-click link to view each course's detailed class roster (`/teacher/course-students`).
- **Class Teacher Role Overview**:
  - Identifies if the teacher is appointed as Class Teacher for a section, room number, and total class strength. Shows a clear unassigned state if none.
  - Direct link to Section Student Directory (`/teacher/students`).
- **Students Taught Metric**:
  - Accurate aggregate count of distinct students taught across all assigned sections.

### 4. Student Dashboard (`/student/dashboard`)
- **Academic Placement**:
  - Standard, Division, Roll Number, Classroom Room Number, and assigned Class Teacher.
- **Fee Balance Status**:
  - Displays remaining fee amount (₹) with status badges: `[PAID IN FULL]` or `[PAYMENT DUE]`.
- **Course Enrollment & Latest Academic Grades**:
  - Tabulated overview of enrolled courses, periods per week, and academic grades (`A+`, `A`, `B+`, `B`, `C`, `D`, `F`, or `Not Graded`).
- **House Affiliation & Transport Details**:
  - House name, monochrome color representation, tournament win count, and `[HOUSE CAPTAIN]` honor badge if the student serves as Captain.
  - Commute mode: Assigned bus registration number, route name, driver name, and emergency contact (or Self Commute).

### 5. Staff / Driver Dashboard (`/staff/dashboard`)
- **Profile Summary**:
  - Employee ID, Full Name, Designated Role (e.g. *Driver*, *Accountant*, *Cleaning Staff*), Contact Number, and Date of Joining.
- **Last Salary Payout**:
  - Most recent salary disbursement amount (₹), payment date, payment mode, transaction reference, and a button to view/print the official salary slip.
- **Driver Bus Operations (Drivers Only)**:
  - Conditionally rendered for staff with `isDriver == true`.
  - Bus registration number, vehicle ID, route name, passenger capacity, assigned student count, capacity utilization percentage, progress bar, and a link to the Bus Passenger Roster.

---

## 3. Files Created & Modified

### Repositories & Services
- `src/main/java/com/example/SchoolWebsite/repository/StatsRepository.java`:
  - Added `getStudentCountByStandard()` for class distribution stats.
  - Added `getMonthlyFeeCollections()` for monthly revenue aggregation.
  - Added `getTotalFeeDues()` for remaining fees summation.
- `src/main/java/com/example/SchoolWebsite/repository/TeacherRepository.java`:
  - Added `getTotalStudentsTaught(employeeId)` using distinct enrollments.
- `src/main/java/com/example/SchoolWebsite/service/TeacherService.java`:
  - Exposed `getTotalStudentsTaught(employeeId)`.
- `src/main/java/com/example/SchoolWebsite/repository/SectionRepository.java`:
  - Added `findSectionByClassTeacher(teacherId)` for class teacher lookup.
- `src/main/java/com/example/SchoolWebsite/service/SectionService.java`:
  - Exposed `getSectionByClassTeacher(teacherId)`.

### Controllers & Layout
- `src/main/java/com/example/SchoolWebsite/controller/LoginController.java`:
  - Updated login redirect to send each role to its dashboard.
- `src/main/java/com/example/SchoolWebsite/controller/HomeController.java`:
  - Updated `GET /` to redirect authenticated users to their role dashboard.
- `src/main/java/com/example/SchoolWebsite/controller/AdminController.java`:
  - Injected `StatsRepository`.
  - Added `GET /admin/dashboard` (and alias `/admin`).
- `src/main/java/com/example/SchoolWebsite/controller/TeacherController.java`:
  - Injected `SectionService`.
  - Added `GET /teacher/dashboard`.
- `src/main/java/com/example/SchoolWebsite/controller/StudentController.java`:
  - Injected `SectionService`, `HouseService`, `TransportService`.
  - Added `GET /student/dashboard`.
- `src/main/java/com/example/SchoolWebsite/controller/StaffController.java`:
  - Added `GET /staff/dashboard`.
- `src/main/resources/templates/fragments/layout.html`:
  - Added "Dashboard" as the primary sidebar navigation item for all roles.
- `src/main/resources/static/css/style.css`:
  - Added styles for `.dashboard-actions-grid`, `.action-tile`, `.chart-card`, and `.progress-bw`.

### HTML Templates
- `src/main/resources/templates/admin/dashboard.html`: Admin command dashboard with B&W Chart.js.
- `src/main/resources/templates/teacher/dashboard.html`: Teacher timetable, section duties, and student count.
- `src/main/resources/templates/student/dashboard.html`: Student academic grades, fee status, and house/bus details.
- `src/main/resources/templates/staff/dashboard.html`: Staff employment summary, last salary slip, and driver fleet stats.

### Automated Tests
- `src/test/java/com/example/SchoolWebsite/DashboardTest.java`: Comprehensive tests for all four role dashboards, auth guards, and login redirects.
- `src/test/java/com/example/SchoolWebsite/LoginAuthTest.java`: Updated redirection targets to role dashboards.
- `src/test/java/com/example/SchoolWebsite/AdminFeaturesTest.java`: Ensured student remaining fee restoration in `@BeforeEach`.

---

## 4. Verification & Testing

Run all unit and integration tests with Maven:
```bash
./mvnw clean test
```

To run only the dashboard and auth tests:
```bash
./mvnw test -Dtest=DashboardTest,LoginAuthTest
```

### Manual Testing Guide
1. **Admin Login**:
   - Navigate to `/login`, enter `admin` / `admin123`.
   - Verify redirection to `/admin/dashboard`.
   - Confirm all 6 stat cards, monochrome charts, quick actions, and recent payments render properly.
2. **Teacher Login**:
   - Log in as `T001` / `pass_T001`.
   - Verify redirection to `/teacher/dashboard`.
   - Check teaching schedule, students taught count, and class teacher section details.
3. **Student Login**:
   - Log in as `A001` / `pass_A001`.
   - Verify redirection to `/student/dashboard`.
   - Check enrolled subjects, grades, remaining fee balance badge, and house info.
4. **Driver / Staff Login**:
   - Log in as driver `S001` / `pass_S001` &rarr; verify vehicle bus summary, route, and last salary.
   - Log in as accountant `S009` / `pass_S009` &rarr; verify non-driver view with profile and salary count.

