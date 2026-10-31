# Step 5: Salary Disbursement (Admin)

## 1. Overview
Step 5 introduces full-lifecycle employee salary disbursement for administration, complete with duplicate disbursement prevention, bulk payroll execution, printable pay advice slips, and employee self-service access.

---

## 2. Requirements Implemented
1. **Pay Salary Page (`/admin/salary/pay`)**:
   - Filter by Teacher or Staff cadre.
   - Dynamic employee dropdown displaying full name, employee ID, and contracted monthly salary.
   - Month and Year selector to disburse salary for any billing period.
   - Pre-fills contracted salary from database record as default disbursement amount.
   - Payment Mode choices: `Bank Transfer`, `Cheque`, `Cash`, `UPI`, `Card`.
2. **Atomic Database Transactions (`@Transactional`)**:
   - In a single atomic transaction:
     - Generates next sequential transaction ID (e.g., `TXN1376`).
     - Inserts into `Payments` table (`Transaction_ID`, `Date_Of_Payment`, `Payment_Type='Salary'`, `Receiver=FullName`, `Sender='School'`, `Payment_Mode`, `Amount`).
     - Inserts link record into `Teacher_Salary (Payment_ID, Teacher_ID)` or `Staff_Salary (Payment_ID, Staff_ID)`.
3. **Duplicate Payment Prevention**:
   - Before recording any salary payment, the system queries whether a salary payment already exists for that employee for the chosen month and year:
     - Teacher: `SELECT COUNT(*) FROM Payments p JOIN Teacher_Salary ts ON p.Transaction_ID = ts.Payment_ID WHERE ts.Teacher_ID = ? AND YEAR(p.Date_Of_Payment) = ? AND MONTH(p.Date_Of_Payment) = ?`
     - Staff: `SELECT COUNT(*) FROM Payments p JOIN Staff_Salary ss ON p.Transaction_ID = ss.Payment_ID WHERE ss.Staff_ID = ? AND YEAR(p.Date_Of_Payment) = ? AND MONTH(p.Date_Of_Payment) = ?`
   - If already disbursed, UI displays a bold B&W warning banner and disables the submit button. Any backend attempt throws a descriptive error and redirects with a flash message.
4. **Bulk Monthly Payroll Disburse (`/admin/salary/bulk-pay`)**:
   - "Bulk Pay All for Month" modal with target group selection (`All Employees`, `Teachers Only`, `Support Staff Only`), Month, Year, and Payment Mode.
   - In an atomic transaction, processes all unpaid employees in the selected cadre, automatically skipping any employee who was already paid for that period to prevent duplicate disbursements.
   - Returns a flash summary of processed employees, total payout (₹), and count of skipped employees.
5. **Ledger Integration**:
   - Disbursed payments immediately appear in `/admin/transactions?type=Salary` through existing `LEFT JOIN Teacher_Salary` and `LEFT JOIN Staff_Salary` queries.
   - Disbursed payments immediately appear in the employee's personal salary history (`/teacher/salary` and `/staff/salary`).
6. **Printable Salary Slip (`/admin/salary/slip/{transactionId}`)**:
   - Clean, formal institutional pay advice slip with St. Xavier's High School branding.
   - Includes Transaction ID, Disbursement Date, Pay Period (e.g., October 2026), Payment Mode, Employee ID, Cadre, Designation, Contact Info, Remuneration Breakdown, and Authorized Signatures.
   - Print stylesheet (`@media print`) hides navigation bars and headers, centering the clean black-and-white slip for physical printing or PDF generation.
   - Includes a "Print Salary Slip" button (`window.print()`).
   - Self-service links added directly to the "Action" column in `/teacher/salary` and `/staff/salary`.
7. **Access Control**:
   - `RoleInterceptor` permits `admin`, as well as `teacher` and `staff` for salary slips.
   - Controllers enforce strict ownership: teachers and staff can view only their own salary slip, while admins can view all.

---

## 3. Files Created & Modified

### New Model
- `src/main/java/com/example/SchoolWebsite/model/SalarySlip.java`:
  - Data transfer object holding transaction metadata, employee profile info, pay period (month & year), and payment breakdown.

### Repository Layer
- `src/main/java/com/example/SchoolWebsite/repository/PaymentRepository.java`:
  - Added `saveTeacherSalaryLink(paymentId, teacherId)`.
  - Added `saveStaffSalaryLink(paymentId, staffId)`.
  - Added `isSalaryPaidForMonth(employeeType, employeeId, year, month)`.
  - Added `findSalarySlip(transactionId)` with parameterized SQL queries joining `Payments` to `Teacher_Salary` + `Teacher`, falling back to `Staff_Salary` + `Staff`.

### Service Layer
- `src/main/java/com/example/SchoolWebsite/service/PaymentService.java`:
  - Injected `TeacherRepository` and `StaffRepository`.
  - Added `@Transactional recordSalaryPayment(employeeType, employeeId, amount, year, month, paymentDate, paymentMode)`.
  - Added `@Transactional bulkDisburseSalaries(targetGroup, year, month, paymentMode, paymentDate)`.
  - Added `isSalaryPaid(employeeType, employeeId, year, month)`.
  - Added `getSalarySlip(transactionId)`.

### Security & Controller Layer
- `src/main/java/com/example/SchoolWebsite/config/RoleInterceptor.java`:
  - Allowed `teacher` and `staff` roles to access `/admin/salary/slip/**`.
- `src/main/java/com/example/SchoolWebsite/controller/AdminController.java`:
  - Added `GET /admin/salary` (redirects to `/admin/salary/pay`).
  - Added `GET /admin/salary/pay` (renders salary disbursement view, checks duplicate payment state).
  - Added `POST /admin/salary/pay` (records individual disbursement, redirects to salary slip).
  - Added `POST /admin/salary/bulk-pay` (bulk disbursement with flash summary).
  - Added `GET /admin/salary/slip/{transactionId}` (renders printable slip with role-based access checks).
- `src/main/java/com/example/SchoolWebsite/controller/TeacherController.java`:
  - Added `GET /teacher/salary/slip/{transactionId}` shortcut redirect.
- `src/main/java/com/example/SchoolWebsite/controller/StaffController.java`:
  - Added `GET /staff/salary/slip/{transactionId}` shortcut redirect.

### Templates
- `src/main/resources/templates/admin/salary-pay.html`:
  - Responsive B&W layout, employee selector tabs, month/year selector, duplicate payment warning banner, and bulk payment modal.
- `src/main/resources/templates/admin/salary-slip.html`:
  - Printable salary slip with `@media print` CSS, print button, and back navigation.
- `src/main/resources/templates/fragments/layout.html`:
  - Added "Disburse Salary" link under Admin Portal.
- `src/main/resources/templates/teacher/salary.html`:
  - Added "Action" column and "Salary Slip →" button for each transaction.
- `src/main/resources/templates/staff/salary.html`:
  - Added "Action" column and "Salary Slip →" button for each transaction.

### Automated Testing
- `src/test/java/com/example/SchoolWebsite/AdminFeaturesTest.java`:
  - Added 12 new MockMvc test cases covering GET form, employee selection, successful disbursement, duplicate payment rejection, bulk payroll disbursement, printable slip rendering for admin, teacher owner access, staff owner access, unauthorized teacher rejection, and table links.

---

## 4. Verification & Testing

### Running Tests
Execute the Maven test suite:
```bash
./mvnw clean test
```
Result:
```
Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Manual Verification Steps
1. Log in as Admin (`admin` / `admin123`).
2. Click **Disburse Salary** in the sidebar.
3. Select a Teacher (e.g. `T001 - Anita Verma`) and change month to `November 2026`.
4. Click **Disburse Salary →**. You are redirected to the official printable Salary Slip.
5. Click **Print Salary Slip** to see the clean, unbranded print preview.
6. Try disbursing again for `T001` for `November 2026`: notice the warning banner and that duplicate disbursement is rejected.
7. Log in as Teacher (`T001` / `teach123`), navigate to **My Salary**, and click **Salary Slip →** on any row to view your own slip.

