# Step 9: Houses Module (Admin)

## 1. Overview
Step 9 implements inter-house league administration, providing a dedicated dashboard for the school's four houses (Red, Blue, Green, Yellow), tournament standings management, faculty mentor assignments, and student roster access. *(Note: House captain assignment was subsequently removed per client specification; faculty mentors and tournament tracking remain active).*

---

## 2. Requirements Implemented
1. **Four House Cards Dashboard (`/admin/houses`)**:
   - Strict black-and-white presentation (colour represented as text: "Colour: Red", not a visual color).
   - Metrics shown on each card: House Name, Colour text, Tournaments Won, Mentor Count, and Student Count.
2. **House Detail & Configuration Page (`/admin/houses/{color}`)**:
   - Edit Tournaments Won: updates `House.No_Of_Wins`.
   - Faculty Mentors: displays current teachers mentoring this house, provides "Add Mentor" (from available unassigned teachers) and "Remove Mentor" (sets `MentorOf = NULL`).
   - Enrolled Students Roster: comprehensive table of all students allocated to this house.
3. **Automated Tests**:
   - MockMvc tests for houses list, house detail, tournament updates, and adding/removing mentors.

---

## 3. Files Created & Modified

### Models & Repositories
- `src/main/java/com/example/SchoolWebsite/model/House.java`:
  - Added `mentorCount`, `studentCount`, `List<Teacher> mentors`, and `List<Student> students`.
- `src/main/java/com/example/SchoolWebsite/repository/HouseRepository.java`:
  - Parameterized SQL queries for house lists, house detail, available mentors, captain membership validation, and mentor assignment.
- `src/main/java/com/example/SchoolWebsite/service/HouseService.java`:
  - Business logic and transactional methods for updating wins, setting captain, and assigning/removing mentors.

### Controller & Views
- `src/main/java/com/example/SchoolWebsite/controller/AdminController.java`:
  - Added endpoints `/admin/houses`, `/admin/houses/{color}`, `/admin/houses/{color}/tournaments`, `/admin/houses/{color}/captain`, `/admin/houses/{color}/mentors/add`, `/admin/houses/{color}/mentors/remove`.
- `src/main/resources/templates/admin/houses.html`:
  - 4 black-and-white house cards with tournament wins and key indicators.
- `src/main/resources/templates/admin/house-detail.html`:
  - Complete house management console.
- `src/main/resources/templates/fragments/layout.html`:
  - Added "Houses" link to the Admin portal sidebar.

---

## 4. Verification & Testing

### Automated Tests
Run:
```bash
./mvnw test -Dtest=AdminFeaturesTest#testHouses*,AdminFeaturesTest#testHouse*,AdminFeaturesTest#test*House*
```
Result: All tests pass.

### Manual Verification Steps
1. Log in as Admin (`admin` / `admin123`).
2. Click **Houses** in the sidebar.
3. Review the four house cards (Red, Blue, Green, Yellow).
4. Click **Manage House &rarr;** on Red House:
   - Update tournament wins (e.g. set to `7`).
   - Assign an available faculty member as a mentor, then remove them.

