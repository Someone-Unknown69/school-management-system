# Step 4: Admin Fee Collection & Receipts

> **Test Suite Status:** `Tests run: 31, Failures: 0, Errors: 0` ✅ **BUILD SUCCESS**

---

## 1. Overview & Objective
Implement an end-to-end fee collection workflow for administrators with atomic database transactions, printable receipts, outstanding dues tracking, and student receipt access.

Key Deliverables:
1. **Record Fee Payment Page (`/admin/fees/record`)**:
   - Interactive student search and selection dropdown listing students with pending balances.
   - Live display of student details: Full Name, Class, Section, Roll Number, and Current Outstanding Fees.
   - Form inputs: Payment Amount, Payment Mode (`UPI`, `Cash`, `NetBanking`, `Cheque`, `Card`), Payment Date, and Remitter / Payer Name.
   - Atomic database transaction (`@Transactional`): Inserts into `Payments`, inserts into `Fees`, and reduces `Student.Remaining_Fees`.
   - Strict validation: Rejects amounts `<= 0` or amounts `> Remaining_Fees`.
2. **Printable Fee Receipt Page (`/admin/fees/receipt/{transactionId}`)**:
   - Institutional branding: School name, receipt number, student particulars, amount paid, payment mode, date, and updated remaining balance.
   - Print stylesheet (`@media print`) and dedicated "Print Receipt" button.
   - Direct link to the receipt from the student's own fee history page (`/student/fees`).
3. **Fee Dues Page (`/admin/fees/dues`)**:
   - Lists students with `Remaining_Fees > 0` sorted by highest due.
   - Filterable by Class (Standard) and Section (Division) with real-time text search.
   - Top KPI cards displaying filtered outstanding dues, due student count, and school-wide aggregate dues.
   - Direct "Collect Fee &rarr;" action button pre-filling the payment form.
4. **Sidebar Navigation & Comprehensive MockMvc Tests**:
   - Admin sidebar links for "Fee Dues" and "Record Payment".
   - 10 automated test cases verifying the complete payment flow, rejection cases (zero, negative, excess amounts), receipt rendering, and student access.

---

## 2. Architecture & Data Flow

### A. Atomic Fee Recording (`@Transactional`)
```
Admin Submits Payment Form (/admin/fees/record)
                  │
                  ▼
AdminController.handleRecordFeePayment()
  ├─ Verify student exists
  ├─ Reject amount <= 0
  ├─ Reject amount > student.remainingFees
                  │
                  ▼
PaymentService.recordFeePayment() [@Transactional]
  ├─ 1. Generate next Transaction_ID (e.g. TXN1375)
  ├─ 2. INSERT INTO Payments (Transaction_ID, Date, 'Fee', 'School', Remitter, Mode, Amount)
  ├─ 3. INSERT INTO Fees (Payment_ID, Student_ID)
  └─ 4. UPDATE Student SET Remaining_Fees = GREATEST(0, Remaining_Fees - Amount)
                  │
                  ▼
Redirect to /admin/fees/receipt/{transactionId} with success alert
```

---

## 3. Files Created & Modified

### A. New Model
- [`FeeReceipt.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/model/FeeReceipt.java): Data transfer object encapsulating joined receipt metadata (Transaction ID, Date, Amount, Mode, Student Name, Admission No, Class, Section, and Remaining Fees).

### B. Repositories & Services
- [`PaymentRepository.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/repository/PaymentRepository.java):
  - Added `generateNextTransactionId()`: Generates sequential `TXNxxxx` numbers.
  - Added `savePayment()`: Inserts into `Payments` using parameterized SQL.
  - Added `saveFeeLink()`: Maps payment to student in `Fees`.
  - Added `findFeeReceipt()`: Retrieves joined receipt record with student details.
- [`StudentRepository.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/repository/StudentRepository.java):
  - Added `findStudentsWithDues()`: Query returning students with `Remaining_Fees > 0` sorted by highest due.
  - Added `getTotalOutstandingDues()`: Computes total unpaid fees for given filters.
  - Added `reduceRemainingFees()`: Decrements student balance safely.
- [`PaymentService.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/service/PaymentService.java):
  - Added `@Transactional recordFeePayment(...)` executing atomic insert & balance reduction.
  - Added `getFeeReceipt(transactionId)`.
- [`StudentService.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/service/StudentService.java):
  - Added `getStudentsWithDues()`, `getAllStudentsWithDues()`, and `getTotalOutstandingDues()`.

### C. Controllers & Security
- [`AdminController.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/controller/AdminController.java):
  - `GET /admin/fees` &rarr; redirects to `/admin/fees/dues`.
  - `GET /admin/fees/dues` &rarr; renders fee dues table with filter bar and top KPI stats.
  - `GET /admin/fees/record` &rarr; renders student selection and payment recording form.
  - `POST /admin/fees/record` &rarr; validates and processes payment within transaction.
  - `GET /admin/fees/receipt/{transactionId}` &rarr; renders printable receipt.
- [`StudentController.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/controller/StudentController.java):
  - `GET /student/fees/receipt/{transactionId}` &rarr; redirects students to their own receipt.
- [`RoleInterceptor.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/config/RoleInterceptor.java):
  - Allowed authenticated students to access `/admin/fees/receipt/**` for their own receipts.

### D. Templates & Styling
- [`admin/fee-dues.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/fee-dues.html): Responsive fee dues dashboard with class/division filters, KPI cards, and collection links.
- [`admin/fee-record.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/fee-record.html): Interactive payment entry form with student lookup and validation.
- [`admin/fee-receipt.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/fee-receipt.html): Printable receipt featuring institution header, student dossier, itemized breakdown, and print CSS.
- [`student/fees.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/student/fees.html): Added "Receipt &rarr;" action button in student payment history table.
- [`fragments/layout.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/fragments/layout.html): Added sidebar links for "Fee Dues" and "Record Payment".

---

## 4. How to Verify & Test

1. **Run Automated Test Suite**:
   ```bash
   ./mvnw test
   ```
   *Result:* All 31 tests pass (0 failures, 0 errors).

2. **Interactive Browser Verification**:
   - Start the server: `./mvnw spring-boot:run`
   - Log in as admin (`admin` / `admin123`):
     - Navigate to **Fee Dues** (`/admin/fees/dues`): Filter by Standard 10 or Section A. Verify students are sorted by highest balance with total outstanding dues at top.
     - Click **Collect Fee &rarr;** for student `A001`: Verify student name, class, and pending dues populate automatically.
     - Try submitting `0` or negative amount: Verify error message *"Payment amount must be greater than zero."*
     - Try submitting an amount higher than remaining dues (e.g. `999999`): Verify rejection error.
     - Submit a valid payment (e.g. `₹1,000` via `UPI`): Form atomically records payment, decrements dues, and redirects to the official receipt.
     - Click **Print Receipt**: Verify print preview cleans away the navigation bar and renders an official paper receipt.
   - Log in as student `A001` (`A001` / `pass_A001`):
     - Navigate to **Fee Status** (`/student/fees`): Verify updated remaining dues and click **Receipt &rarr;** on any payment row to view and print the receipt.

