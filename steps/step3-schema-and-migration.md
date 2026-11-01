# Step 3: Schema Alignment, Migration & Validation

> **Test Suite Status:** `Tests run: 21, Failures: 0, Errors: 0` ✅ **BUILD SUCCESS**

---

## 1. Schema Gap Analysis
Before modifying code or database tables, `database/schema.sql` was inspected against client requirements:

| Requirement | Target Table | Target Column | Status in Original Schema | Resolution |
| :--- | :--- | :--- | :--- | :--- |
| **Student residential address** | `Student` | `Address` | ❌ **Missing** | Added `Address VARCHAR(200)` |
| **Student contact number** | `Student` | `Contact_Number` | ❌ **Missing** | Added `Contact_Number VARCHAR(15)` |
| **Guardian email address** | `Student_Guardian` | `Email` | ❌ **Missing** (`Phone_No` existed) | Added `Email VARCHAR(100)` |
| **Teacher address** | `Teacher` | `Address` | ❌ **Missing** (`Phone_No` & `Email` existed) | Added `Address VARCHAR(200)` |
| **House name, color, wins** | `House` | `Color`, `House_Name`, `No_Of_Wins` | ✅ **Already existed** | Retained with exact constraints |
| **House Captain (1 per house)** | `House` | `Captain_ID` | ❌ **Missing** | Added `Captain_ID VARCHAR(10) UNIQUE` + FK to `Student(Admission_No)` |

---

## 2. Migration & Seed Data Updates

### A. Unified Main Schema (`database/schema.sql`)
All entity definitions are consolidated directly into the main `database/schema.sql` file:
- `Address` and `Contact_Number` in `Student`.
- `Email` in `Student_Guardian`.
- `Address` in `Teacher`.
- `House` table without `Captain_ID` (captains removed per specification).

### B. Seed Data Generator (`database/generate_seed.py`)
- Generates realistic residential addresses and valid 10-digit phone numbers for all 480 students.
- Generates realistic notification emails for student guardians (`@gmail.com`).
- Generates realistic addresses for all 28 faculty teachers.
- Dynamically assigns senior Class 12 students in each house as House Captains from their own house members:
  - **Neptune House (Blue)**: Captain `A361` (Akash, Class 12, Blue)
  - **Falcon House (Green)**: Captain `A362` (Shruti, Class 12, Green)
  - **Tigris House (Yellow)**: Captain `A363` (Siddharth, Class 12, Yellow)
  - **Phoenix House (Red)**: Captain `A364` (Myra, Class 12, Red)
- Retains unified authentication records in `User_Account`.
- Successfully regenerated `database/seed.sql` and verified with MySQL `school_dbms`.

---

## 3. Backend & Frontend Code Changes

### A. Models
- [`Student.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/model/Student.java): Added `address`, `contactNumber`, `houseWins`, `houseCaptainId`, and helper `isHouseCaptain()`.
- [`Guardian.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/model/Guardian.java): Added `email` with getter and setter.
- [`Teacher.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/model/Teacher.java): Added `address` with getter and setter.
- [`House.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/model/House.java): Created model for house entity with captain mapping.

### B. Repositories & Services
- [`StudentRepository.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/repository/StudentRepository.java):
  - Updated `findByAdmissionNo`, `findFullProfile`, `searchAndFilter` to SELECT and map `Address` and `Contact_Number`.
  - In `findFullProfile`, joined `House` to fetch `No_Of_Wins` and `Captain_ID`.
  - Updated `save()` and `update()` parameterized queries.
  - Overloaded `saveGuardian()` to accept and store `Email`.
  - Updated `getAvailableHouses()` to select `No_Of_Wins` and `Captain_ID`.
- [`GuardianRepository.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/repository/GuardianRepository.java): Updated `findByStudentId` query to map `Email`.
- [`TeacherRepository.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/repository/TeacherRepository.java): Updated `findFullProfile`, `findAll`, `save`, and `update` to map and persist `Address`.
- [`StudentService.java`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/java/com/example/SchoolWebsite/service/StudentService.java): Updated `saveStudent` and `updateStudent` to pass `guardianEmail`, preserving backwards-compatible overloads.

### C. Validation & Controller (`AdminController.java`)
- Added phone number validation (`^[0-9]{10}$`) for student contact numbers, guardian contact numbers, and teacher contact numbers.
- Added email format validation (`^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$`) for guardian and teacher emails.
- Dispatches informative error flash messages if validation rules fail and safely redirects back to the form.

### D. Thymeleaf Templates
- [`admin/student-form.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/student-form.html): Added inputs for Student Contact Number, Student Residential Address, and Guardian Email with `type="tel"`, `type="email"`, patterns, and helper hints.
- [`admin/teacher-form.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/admin/teacher-form.html): Added Residential Address and telephone validation pattern.
- [`student/profile.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/student/profile.html): Displays Student Address, Contact Number, House Captain badge (when captain), House Tournaments Won, and Guardian Email column.
- [`teacher/profile.html`](file:///Users/harshit/Documents/SCHOOL%20MANAGEMENT%20SYSTEM/src/main/resources/templates/teacher/profile.html): Displays Teacher Residential Address.

---

## 4. How to Initialize Database
Import the consolidated schema directly via terminal:
```bash
mysql -u root -p school_dbms < database/schema.sql
```

### Option 2: Fresh Database (Full Seed)
Regenerate and reload the complete test seed dataset:
```bash
python3 database/generate_seed.py
mysql -u root -p school_dbms < database/seed.sql
```

---

## 5. Verification & Testing

1. Run the test suite:
   ```bash
   ./mvnw clean test
   ```
   *Expected:* All 21 tests pass (including 4 new tests verifying input validations and profile display).

2. Test interactively via browser at `http://localhost:8080/login`:
   - **Student Form Validation**: Attempt creating a student with invalid contact digits or bad guardian email; verify flash error rejection.
   - **Teacher Form**: Verify residential address input and phone formatting.
   - **Student Profile**: Log in as `A001` or `A364` to verify address, contact, house captain badge, and guardian email rendering.

