# School Management System (DBMS Project)

An enterprise-grade School Management System developed as part of **Assignment - I: School Database Management System Client Requirements**.

Built with **Spring Boot 4**, **Java 17**, **MySQL 8**, **Thymeleaf**, and a modern, high-contrast **Black & White Design System**.

---

## 1. Tech Stack & Architecture

- **Backend Framework**: Spring Boot (v4.1.1)
- **Language**: Java 17
- **Database**: MySQL 8.0+ via Spring `JdbcTemplate` with connection pooling (`HikariCP`)
- **Transaction Management**: `@Transactional` boundaries enforcing multi-table ACID consistency
- **Frontend / Templating**: Thymeleaf with reusable layout fragments (`layout.html`, `ui.html`)
- **Styling**: Bootstrap 5 + Custom Pure Monochrome CSS (strictly `#000000`, `#ffffff`, `#f5f5f5`, `#e0e0e0`, `#666666`, zero color accents)
- **Visualizations**: Chart.js configured with monochrome hatched and dot patterns
- **Test Suite**: JUnit 5 + Spring MockMvc (82 comprehensive integration test cases)

---

## 2. Quick Setup & Installation

### Prerequisites
- **JDK 17** or higher
- **MySQL Server 8.0+**
- **Git**

### Step 1: Clone the Repository
```bash
git clone https://github.com/HarshitV08/school-management-system.git
cd "SCHOOL MANAGEMENT SYSTEM"
```

### Step 2: Setup MySQL Database
Open your MySQL terminal or MySQL Workbench:
```sql
CREATE DATABASE IF NOT EXISTS school_dbms;
USE school_dbms;
```
Import the schema and initial seed data:
```bash
mysql -u root -p school_dbms < database/schema.sql
mysql -u root -p school_dbms < database/seed.sql
```

### Step 3: Configure Database Credentials
Edit [`src/main/resources/application.properties`](src/main/resources/application.properties):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/school_dbms?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 4: Run the Application
```bash
./mvnw clean spring-boot:run
```
The application will start on `http://localhost:8080`.

### Step 5: Run Automated Tests
```bash
./mvnw test
```
*Expected output: `Tests run: 82, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS`.*

---

## 3. Test Accounts & Login Credentials

All test accounts use passwords hashed or prefixed with `pass_<ID>`:

| Role | Username / ID | Password | Portal Description |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Full access to all academic, transport, staff, and financial modules |
| **Student** | `A001` | `pass_A001` | Student dashboard, grades, enrollments, house info, bus route, fee balance & receipts |
| **Teacher** | `T001` | `pass_T001` | Teacher schedule, today's classes, students taught, salary slip history |
| **Staff (Driver)** | `S001` | `pass_S001` | Driver dashboard, bus roster, route & vehicle details, salary slips |
| **Staff (Support)** | `S002` | `pass_S002` | Staff dashboard, employment profile, salary history |

> **Tip**: The login page (`/login`) includes **One-Click Credential Chips** to auto-fill any of the 4 roles instantly.

---

## 4. Requirements Audit Checklist

### Part A: Data Model Requirements

| # | Data Model Requirement | Status | Exact Application URL | Test Account |
| :-: | :--- | :-: | :--- | :--- |
| **D1** | **Students**: Unique ID (`Admission_No`), full name, sex, DOB, age, residential address, contact number, guardian name(s), guardian email(s), enrolled in exactly one class/section. | **DONE** | [`/admin/students/A001/overview`](http://localhost:8080/admin/students/A001/overview) | `admin` / `admin123` |
| **D2** | **Classes**: Class ID / standard, class name (e.g. Grade 10), section/division, room number, exactly one appointed Class Teacher. | **DONE** | [`/admin/sections`](http://localhost:8080/admin/sections) | `admin` / `admin123` |
| **D3** | **Subjects**: Unique subject code, subject name, periods per week (> 0), reference books, teacher-subject mapping. | **DONE** | [`/admin/subjects/MATH10`](http://localhost:8080/admin/subjects/MATH10) | `admin` / `admin123` |
| **D4** | **Teachers**: Unique teacher ID, full name, qualification, date of joining, address, contact phone, contact email, assigned subjects. | **DONE** | [`/admin/faculty`](http://localhost:8080/admin/faculty) | `admin` / `admin123` |
| **D5** | **Houses**: Unique name, designated house colour (monochrome textual representation), House Captain (single student), multiple teacher Mentors, tournaments won count. | **DONE** | [`/admin/houses/Red`](http://localhost:8080/admin/houses/Red) | `admin` / `admin123` |
| **D6** | **Transportation & Staff**: Vehicle ID (Bus no. 1, etc.), registration number, route name, seating capacity; Staff: ID, name, designated role, contact info; Drivers assigned to vehicles. | **DONE** | [`/admin/transport`](http://localhost:8080/admin/transport) | `admin` / `admin123` |
| **D7** | **Payments System**: Unique transaction ID, amount, transaction date, payment mode (Cash, UPI, Net Banking, Cheque, Card); Student fees and Teacher/Staff monthly salaries. | **DONE** | [`/admin/transactions`](http://localhost:8080/admin/transactions) | `admin` / `admin123` |

---

### Part B: Basic Functional Requirements (17 Items)

| # | Client Functional Requirement | Status | Exact Application URL | Test Account |
| :-: | :--- | :-: | :--- | :--- |
| **1** | **Enrolling a new student** and recording personal, contact, and guardian details. | **DONE** | [`/admin/students/new`](http://localhost:8080/admin/students/new) | `admin` / `admin123` |
| **2** | **Assigning student to class & section** at admission or promotion. | **DONE** | Admission: [`/admin/students/new`](http://localhost:8080/admin/students/new)<br>Promotion: [`/admin/sections/promote`](http://localhost:8080/admin/sections/promote) | `admin` / `admin123` |
| **3** | **Appointing / reassigning a class teacher** to a class (1 teacher per section enforced). | **DONE** | [`/admin/sections`](http://localhost:8080/admin/sections) | `admin` / `admin123` |
| **4** | **Registering new subjects** & updating details (periods per week, reference books). | **DONE** | List: [`/admin/subjects`](http://localhost:8080/admin/subjects)<br>New: [`/admin/subjects/new`](http://localhost:8080/admin/subjects/new) | `admin` / `admin123` |
| **5** | **Assigning subjects to student's curriculum** (individual & bulk section enrollment). | **DONE** | [`/admin/students/A001/detail`](http://localhost:8080/admin/students/A001/detail) | `admin` / `admin123` |
| **6** | **Appointing teachers to teach specific subjects**. | **DONE** | [`/admin/subjects/MATH10`](http://localhost:8080/admin/subjects/MATH10) | `admin` / `admin123` |
| **7** | **Recording new teacher or staff member details** upon hiring and updating them. | **DONE** | Teacher: [`/admin/teachers/new`](http://localhost:8080/admin/teachers/new)<br>Staff: [`/admin/staff/new`](http://localhost:8080/admin/staff/new) | `admin` / `admin123` |
| **8** | **Assigning a student to a house upon enrollment**. | **DONE** | [`/admin/students/new`](http://localhost:8080/admin/students/new) or edit at [`/admin/students/edit/A001`](http://localhost:8080/admin/students/edit/A001) | `admin` / `admin123` |
| **9** | **Designating a student as House Captain** for a given house. | **DONE** | [`/admin/houses/Red`](http://localhost:8080/admin/houses/Red) | `admin` / `admin123` |
| **10** | **Assigning teachers as mentors to a house**. | **DONE** | [`/admin/houses/Red`](http://localhost:8080/admin/houses/Red) | `admin` / `admin123` |
| **11** | **Enrolling student for transportation** (assigning route and vehicle). | **DONE** | [`/admin/students/edit/A001`](http://localhost:8080/admin/students/edit/A001) | `admin` / `admin123` |
| **12** | **Assigning drivers from the staff pool to specific vehicles**. | **DONE** | [`/admin/transport`](http://localhost:8080/admin/transport) (Edit vehicle) | `admin` / `admin123` |
| **13** | **Recording the collection of fees** from student (amount, date, mode) via `@Transactional`. | **DONE** | [`/admin/fees/record`](http://localhost:8080/admin/fees/record) | `admin` / `admin123` |
| **14** | **Generating fee receipts and tracking outstanding/overdue fees**. | **DONE** | Receipt: [`/admin/fees/receipt/TXN0001`](http://localhost:8080/admin/fees/receipt/TXN0001)<br>Dues list: [`/admin/fees/dues`](http://localhost:8080/admin/fees/dues) | `admin` / `admin123` |
| **15** | **Processing and disbursing monthly salaries** to teachers and staff (individual & bulk). | **DONE** | Pay: [`/admin/salary/pay`](http://localhost:8080/admin/salary/pay)<br>Slip: [`/admin/salary/slip/TXN1057`](http://localhost:8080/admin/salary/slip/TXN1057) | `admin` / `admin123` |
| **16** | **Generating payment records and financial statements** (filtered ledger, CSV export). | **DONE** | Ledger: [`/admin/transactions`](http://localhost:8080/admin/transactions)<br>Statement: [`/admin/transactions/statement`](http://localhost:8080/admin/transactions/statement) | `admin` / `admin123` |
| **17** | **Retrieving a consolidated student profile** (academics, house, transport, payments). | **DONE** | [`/admin/students/A001/overview`](http://localhost:8080/admin/students/A001/overview) | `admin` / `admin123` |

---

## 5. TA Demo Script (Step-by-Step Walkthrough)

Follow this 5-minute click path to demonstrate all requirements in order:

### Stage 1: Public Portal & Login
1. Open `http://localhost:8080/`. Note the **Campus Census & Directory** ledger (480 students, 28 teachers, 25 staff, 4 houses) and architectural micro-grid background.
2. Click **Login** (top right) or go to `/login`.
3. Click the **Administrator** chip (auto-fills `admin` / `admin123`) and click **Sign In**.

### Stage 2: Admin Dashboard & Financial Modules (Req #13, #14, #15, #16)
1. You land on `/admin/dashboard`. Notice the monochrome metric cards and Chart.js distribution charts.
2. **Fee Collection (Req #13 & #14)**:
   - Click **Fees &rarr; Record Fee Payment** on the navbar (`/admin/fees/record`).
   - Select student `A001` (Rohan Mehta). See remaining fees displayed.
   - Enter Amount `500.00`, choose `UPI`, and click **Record Payment &rarr;**.
   - You are redirected to the printable **Fee Receipt** (`/admin/fees/receipt/...`). Click the **Print Receipt** button to inspect print styles.
   - Click **Fees &rarr; Outstanding Dues** (`/admin/fees/dues`). Observe students sorted by highest remaining dues with outstanding total at top.
3. **Salary Disbursement (Req #15)**:
   - Click **Salaries &rarr; Disburse Salary** (`/admin/salary/pay`).
   - Select employee `T001` (Aarav Sharma), month, and payment mode. Click **Disburse Salary &rarr;**.
   - Notice the printable **Salary Slip** generated.
   - Return to `/admin/salary/pay` and attempt to pay the same teacher for the same month: observe the duplicate prevention validation.
4. **Financial Statements & CSV (Req #16)**:
   - Click **Financials &rarr; Ledger &amp; Statements** (`/admin/transactions`).
   - Click **Export Filtered CSV** to download `transactions_ledger.csv`.
   - Click **View Printable Statement** (`/admin/transactions/statement`) to view fees vs salaries net balance.

### Stage 3: Academics, Subjects & Faculty (Req #3, #4, #5, #6, #7)
1. **Classes & Class Teacher Reassignment (Req #3 & #2)**:
   - Click **Academics &rarr; Classes &amp; Sections** (`/admin/sections`).
   - Click **Change Class Teacher** for Class 10 - A and select a new teacher. Submit and observe update.
   - Click **Promote Students** (`/admin/sections/promote`). Select source Class 9-A &rarr; Class 10-A, preview student checkboxes, and confirm.
2. **Subjects & Reference Books (Req #4 & #6)**:
   - Click **Academics &rarr; Subjects Catalog** (`/admin/subjects`).
   - Click on `MATH10` (`/admin/subjects/MATH10`). View assigned teachers and reference books.
   - Click **Add Reference Book** or **Assign Teacher** to test real-time mapping updates.
3. **Student Subject Enrollment & Grades (Req #5)**:
   - Open `/admin/students/A001/detail`.
   - Edit a grade for an enrolled subject (e.g. `A+`), or click **Enroll All Subjects of Class**.
4. **Faculty & Staff Hiring (Req #7)**:
   - Click **Staff &rarr; Faculty &amp; Staff Directory** (`/admin/faculty`).
   - Click **+ Add Teacher** or **+ Add Staff** to review hiring forms.

### Stage 4: Houses & Transportation (Req #8, #9, #10, #11, #12)
1. **Houses Module (Req #8, #9, #10)**:
   - Click **Operations &rarr; Houses &amp; Mentorship** (`/admin/houses`).
   - Click **Manage Red House** (`/admin/houses/Red`).
   - Update Tournaments Won, change House Captain (limited to Red House students), or add/remove a Mentor Teacher.
2. **Transportation Fleet & Drivers (Req #11, #12)**:
   - Click **Operations &rarr; Transport &amp; Vehicles** (`/admin/transport`).
   - Edit vehicle `V01`. Reassign driver from the driver pool or update vehicle capacity.

### Stage 5: Consolidated Student Overview (Req #1, #17)
1. Go to `/admin/students/A001/overview`.
2. Inspect the consolidated view:
   - **Personal & Guardians**: Student contact info, address, guardian name and email.
   - **Academic Status**: Standard, division, room number, assigned class teacher, enrolled subjects and grades.
   - **House**: House name, captain status, assigned mentors.
   - **Transport**: Vehicle ID, registration number, route name, driver name and phone.
   - **Financials**: Lifetime fee transaction history, total amount paid, and remaining balance.
3. Click **Print Profile** to verify clean print preview.

### Stage 6: Role-Based Portals Check
1. Logout from admin (top right).
2. Login as **Student** (`A001` / `pass_A001`): View personalized student dashboard, fee balance, enrolled subjects, and fee receipts.
3. Logout and login as **Teacher** (`T001` / `pass_T001`): View teacher schedule, class teacher duties, and salary slip history.
4. Logout and login as **Staff / Driver** (`S001` / `pass_S001`): View driver bus roster and route details.
