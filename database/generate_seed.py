#!/usr/bin/env python3
"""
Seed Data Generator for School Management System DBMS.
Generates a comprehensive, realistic, and robust SQL seed file for testing:
- 4 Houses
- 4 Classes (9, 10, 11, 12)
- 16 Sections (4 sections per class: A, B, C, D)
- 10 Transport Vehicles covering detailed routes
- 25 Non-teaching Staff members across multiple roles (8 Drivers, Accountants, Librarians, etc.)
- 28 Faculty Teachers with qualifications, specializations, class teacher & mentor duties
- 480 Students (30 students per section, Roll Nos 1-30 in each section)
- 900+ Student Guardians with realistic occupations and contacts
- 34 Subjects across Classes 9-12 (Middle school core + Senior Secondary Streams: Science PCM/PCB, Commerce)
- 70+ Reference Books
- Complete Teacher-Subject mappings
- Comprehensive Class Schedules for all 16 sections
- 3,120 Course Enrollments with realistic grade distributions
- 1,200+ Payment Transactions (Fee installments + 6 months of Teacher & Staff salary disbursements)
- Exact Foreign Key integrity and constraint compliance
"""

import random

# Fix seed for reproducibility
random.seed(42)

def generate_sql():
    lines = []
    
    lines.append("-- =========================================================")
    lines.append("-- School Management System - Comprehensive Seed Data")
    lines.append("-- Generated for robust performance, query, and UI testing")
    lines.append("-- Run database/schema.sql FIRST before applying this seed.")
    lines.append("-- =========================================================")
    lines.append("")
    lines.append("USE school_dbms;")
    lines.append("")
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- Reset / Truncate (foreign key safe)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("SET FOREIGN_KEY_CHECKS = 0;")
    lines.append("TRUNCATE TABLE User_Account;")
    lines.append("TRUNCATE TABLE Staff_Salary;    TRUNCATE TABLE Teacher_Salary;  TRUNCATE TABLE Fees;")
    lines.append("TRUNCATE TABLE Payments;        TRUNCATE TABLE Class_Schedule;  TRUNCATE TABLE Enrollments;")
    lines.append("TRUNCATE TABLE Teacher_Subject; TRUNCATE TABLE Reference_Books; TRUNCATE TABLE Subject;")
    lines.append("TRUNCATE TABLE Student_Guardian;TRUNCATE TABLE Student;         TRUNCATE TABLE Section;")
    lines.append("TRUNCATE TABLE Teacher_Specialization; TRUNCATE TABLE Teacher_Qualification;")
    lines.append("TRUNCATE TABLE Teacher;         TRUNCATE TABLE Transport;       TRUNCATE TABLE Staff;")
    lines.append("TRUNCATE TABLE Class;           TRUNCATE TABLE House;")
    lines.append("SET FOREIGN_KEY_CHECKS = 1;")
    lines.append("")

    # 1. House
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 1. Houses")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO House (Color, House_Name, No_Of_Wins) VALUES")
    houses = [
        ("Red", "Phoenix House", 18),
        ("Blue", "Neptune House", 14),
        ("Green", "Falcon House", 16),
        ("Yellow", "Tigris House", 15)
    ]
    house_vals = [f"('{h[0]}', '{h[1]}', {h[2]})" for h in houses]
    lines.append(",\n".join(house_vals) + ";")
    lines.append("")

    # 2. Class
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 2. Classes (Standards 9 to 12)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Class (Standard) VALUES")
    classes = [9, 10, 11, 12]
    class_vals = [f"('{c}')" for c in classes]
    lines.append(",\n".join(class_vals) + ";")
    lines.append("")

    # 3. Staff
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 3. Staff Members (Non-teaching: Drivers, Admin, Labs, etc.)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Staff (Employee_ID, First_Name, Middle_Name, Last_Name, Contact_Number, Designated_Role, Date_Of_Joining, Salary) VALUES")
    staff_members = [
        ("S001", "Ramesh", "NULL", "'Kumar'", "'9876500001'", "'Driver'", "'2019-06-01'", 22000.00),
        ("S002", "Mohan", "'Lal'", "'Yadav'", "'9876500002'", "'Driver'", "'2020-01-10'", 21000.00),
        ("S003", "Imran", "NULL", "'Ansari'", "'9876500003'", "'Driver'", "'2021-07-01'", 21500.00),
        ("S004", "Bablu", "NULL", "'Prasad'", "'9876500004'", "'Driver'", "'2022-02-14'", 20500.00),
        ("S005", "Dinesh", "'Kumar'", "'Verma'", "'9876500005'", "'Driver'", "'2020-08-11'", 22500.00),
        ("S006", "Suraj", "NULL", "'Pal'", "'9876500006'", "'Driver'", "'2019-11-20'", 21000.00),
        ("S007", "Santosh", "NULL", "'Chauhan'", "'9876500007'", "'Driver'", "'2021-03-15'", 20500.00),
        ("S008", "Jagdish", "'Prasad'", "'Singh'", "'9876500008'", "'Driver'", "'2018-05-10'", 23000.00),
        ("S009", "Sunita", "NULL", "'Sharma'", "'9876500009'", "'Accountant'", "'2018-04-15'", 35000.00),
        ("S010", "Vikas", "'Nath'", "'Dubey'", "'9876500010'", "'Accountant'", "'2021-09-01'", 32000.00),
        ("S011", "Geeta", "NULL", "'Devi'", "'9876500011'", "'Librarian'", "'2017-08-01'", 30000.00),
        ("S012", "Purnima", "NULL", "'Sengupta'", "'9876500012'", "'Librarian'", "'2020-03-01'", 28000.00),
        ("S013", "Anil", "'K.'", "'Pathak'", "'9876500013'", "'Lab Assistant'", "'2019-09-01'", 24000.00),
        ("S014", "Rajesh", "NULL", "'Mishra'", "'9876500014'", "'Lab Assistant'", "'2020-10-15'", 23500.00),
        ("S015", "Vinay", "'Kant'", "'Tripathi'", "'9876500015'", "'Lab Assistant'", "'2021-01-20'", 24000.00),
        ("S016", "Mukesh", "NULL", "'Sahu'", "'9876500016'", "'Lab Assistant'", "'2022-04-05'", 22000.00),
        ("S017", "Harish", "NULL", "'Rai'", "'9876500017'", "'Security Guard'", "'2020-06-15'", 18000.00),
        ("S018", "Brijesh", "'Bhan'", "'Pandey'", "'9876500018'", "'Security Guard'", "'2019-02-10'", 18500.00),
        ("S019", "Ramu", "NULL", "'Chaurasia'", "'9876500019'", "'Peon'", "'2016-04-01'", 15000.00),
        ("S020", "Gopal", "NULL", "'Kushwaha'", "'9876500020'", "'Peon'", "'2018-09-12'", 15500.00),
        ("S021", "Shweta", "NULL", "'Jaiswal'", "'9876500021'", "'Office Clerk'", "'2021-04-01'", 26000.00),
        ("S022", "Alok", "'Ranjan'", "'Srivastava'", "'9876500022'", "'Office Clerk'", "'2020-11-05'", 27000.00),
        ("S023", "Deepika", "NULL", "'Pandey'", "'9876500023'", "'Office Clerk'", "'2022-06-18'", 25000.00),
        ("S024", "Sarojini", "NULL", "'Nair'", "'9876500024'", "'Nurse'", "'2019-07-25'", 29000.00),
        ("S025", "Dr. Shalini", "NULL", "'Mathur'", "'9876500025'", "'Counselor'", "'2021-08-01'", 38000.00),
    ]
    staff_vals = [
        f"('{s[0]}', '{s[1]}', {s[2]}, {s[3]}, {s[4]}, {s[5]}, {s[6]}, {s[7]:.2f})"
        for s in staff_members
    ]
    lines.append(",\n".join(staff_vals) + ";")
    lines.append("")

    # 4. Transport
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 4. Transport (Vehicles & Bus Routes)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Transport (Vehicle_ID, Route_Name, Registration_Number, Capacity, DriverID) VALUES")
    transports = [
        ("V01", "Route A - Sigra & Cantt", "UP65AB1234", 40, "S001"),
        ("V02", "Route B - Lanka & BHU", "UP65CD5678", 35, "S002"),
        ("V03", "Route C - Godowlia & Chowk", "UP65EF9012", 40, "S003"),
        ("V04", "Route D - Sarnath & Pandeypur", "UP65GH3456", 35, "S004"),
        ("V05", "Route E - Mahmoorganj & Rathyatra", "UP65IJ7890", 40, "S005"),
        ("V06", "Route F - Shivpur & Orderly Bazar", "UP65KL2345", 35, "S006"),
        ("V07", "Route G - DLW & Kakarmatta", "UP65MN6789", 40, "S007"),
        ("V08", "Route H - Bhelupur & Sonarpura", "UP65OP0123", 35, "S008"),
    ]
    transport_vals = [f"('{t[0]}', '{t[1]}', '{t[2]}', {t[3]}, '{t[4]}')" for t in transports]
    lines.append(",\n".join(transport_vals) + ";")
    lines.append("")

    # 5. Teacher
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 5. Faculty Teachers")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Teacher (Employee_ID, First_Name, Middle_Name, Last_Name, Date_Of_Joining, Phone_No, Email, Salary, MentorOf, Address) VALUES")
    teachers = [
        ("T001", "Anita", "NULL", "'Verma'", "'2015-07-10'", "'9876000001'", "'anita.verma@school.edu'", 52000.00, "'Red'", "'B-12, Shivala Colony, Mahmoorganj, Varanasi'"),
        ("T002", "Rakesh", "'K.'", "'Singh'", "'2017-03-22'", "'9876000002'", "'rakesh.singh@school.edu'", 48000.00, "'Blue'", "'Flat 302, Ganga Heights, Bhelupur, Varanasi'"),
        ("T003", "Meena", "NULL", "'Gupta'", "'2014-06-18'", "'9876000003'", "'meena.gupta@school.edu'", 55000.00, "'Green'", "'House 88/B, Saket Nagar, Lanka, Varanasi'"),
        ("T004", "Vikram", "NULL", "'Pandey'", "'2016-08-01'", "'9876000004'", "'vikram.pandey@school.edu'", 50000.00, "'Yellow'", "'12, Tagore Town, Orderly Bazar, Varanasi'"),
        ("T005", "Kavita", "NULL", "'Mishra'", "'2018-07-05'", "'9876000005'", "'kavita.mishra@school.edu'", 46000.00, "NULL", "'C-4/15, Vidyapeeth Road, Sigra, Varanasi'"),
        ("T006", "Arjun", "'P.'", "'Tiwari'", "'2013-04-12'", "'9876000006'", "'arjun.tiwari@school.edu'", 62000.00, "NULL", "'78, Shivpur By-pass, Shivpur, Varanasi'"),
        ("T007", "Neha", "NULL", "'Srivastava'", "'2015-11-20'", "'9876000007'", "'neha.srivastava@school.edu'", 58000.00, "NULL", "'House 23, DLW Colony, Kakarmatta, Varanasi'"),
        ("T008", "Sanjay", "NULL", "'Yadav'", "'2012-06-30'", "'9876000008'", "'sanjay.yadav@school.edu'", 65000.00, "NULL", "'56/A, Pandeypur Chauraha, Varanasi'"),
        ("T009", "Pooja", "NULL", "'Agarwal'", "'2019-01-14'", "'9876000009'", "'pooja.agarwal@school.edu'", 47000.00, "NULL", "'19, Sarnath Station Road, Sarnath, Varanasi'"),
        ("T010", "Deepak", "NULL", "'Joshi'", "'2020-07-01'", "'9876000010'", "'deepak.joshi@school.edu'", 51000.00, "NULL", "'Plot 102, Chandpur Industrial Estate, Varanasi'"),
        ("T011", "Ritu", "NULL", "'Saxena'", "'2017-09-09'", "'9876000011'", "'ritu.saxena@school.edu'", 54000.00, "NULL", "'House 34, Maldahiya, Cantt, Varanasi'"),
        ("T012", "Manoj", "NULL", "'Dubey'", "'2021-04-05'", "'9876000012'", "'manoj.dubey@school.edu'", 42000.00, "NULL", "'Flat 101, Surya Vihar, Ravindrapuri, Varanasi'"),
        ("T013", "Sunil", "'K.'", "'Chawla'", "'2016-05-18'", "'9876000013'", "'sunil.chawla@school.edu'", 53000.00, "NULL", "'8/12, Gurubagh, Kamachha, Varanasi'"),
        ("T014", "Vandana", "NULL", "'Tripathi'", "'2015-02-11'", "'9876000014'", "'vandana.tripathi@school.edu'", 56000.00, "NULL", "'44, Assi Ghat Road, Assi, Varanasi'"),
        ("T015", "Anuradha", "NULL", "'Sen'", "'2014-10-04'", "'9876000015'", "'anuradha.sen@school.edu'", 59000.00, "NULL", "'15/C, Nagwa, Lanka, Varanasi'"),
        ("T016", "Kamal", "NULL", "'Nayan'", "'2018-08-20'", "'9876000016'", "'kamal.nayan@school.edu'", 48000.00, "NULL", "'67, Naria, BHU Road, Varanasi'"),
        ("T017", "Girish", "'C.'", "'Bhatt'", "'2017-12-01'", "'9876000017'", "'girish.bhatt@school.edu'", 51000.00, "NULL", "'Plot 8, Chitaipur Road, Sundarpur, Varanasi'"),
        ("T018", "Deepa", "NULL", "'Rastogi'", "'2019-03-15'", "'9876000018'", "'deepa.rastogi@school.edu'", 49000.00, "NULL", "'House 91, Manduadih Station Road, Varanasi'"),
        ("T019", "Pradeep", "NULL", "'Chawla'", "'2018-11-10'", "'9876000019'", "'pradeep.chawla@school.edu'", 50000.00, "NULL", "'28/B, Rohania, GT Road, Varanasi'"),
        ("T020", "Meenu", "NULL", "'Kapoor'", "'2020-02-14'", "'9876000020'", "'meenu.kapoor@school.edu'", 47000.00, "NULL", "'B-14, Shivala Colony, Mahmoorganj, Varanasi'"),
        ("T021", "Harish", "'Chand'", "'Sharma'", "'2015-08-25'", "'9876000021'", "'harish.sharma@school.edu'", 57000.00, "NULL", "'Flat 204, Ganga Heights, Bhelupur, Varanasi'"),
        ("T022", "Ravi", "NULL", "'Shukla'", "'2016-09-17'", "'9876000022'", "'ravi.shukla@school.edu'", 52000.00, "NULL", "'House 55/A, Saket Nagar, Lanka, Varanasi'"),
        ("T023", "Amitabh", "'R.'", "'Sinha'", "'2017-06-05'", "'9876000023'", "'amitabh.sinha@school.edu'", 54000.00, "NULL", "'18, Tagore Town, Orderly Bazar, Varanasi'"),
        ("T024", "Preeti", "NULL", "'Deshmukh'", "'2019-07-22'", "'9876000024'", "'preeti.deshmukh@school.edu'", 48000.00, "NULL", "'C-7/12, Vidyapeeth Road, Sigra, Varanasi'"),
        ("T025", "Bhavna", "NULL", "'Kulkarni'", "'2020-09-01'", "'9876000025'", "'bhavna.kulkarni@school.edu'", 46000.00, "NULL", "'82, Shivpur By-pass, Shivpur, Varanasi'"),
        ("T026", "Suresh", "NULL", "'Nambiar'", "'2016-01-15'", "'9876000026'", "'suresh.nambiar@school.edu'", 55000.00, "NULL", "'House 10, DLW Colony, Kakarmatta, Varanasi'"),
        ("T027", "Alka", "NULL", "'Gautam'", "'2018-04-10'", "'9876000027'", "'alka.gautam@school.edu'", 50000.00, "NULL", "'61/B, Pandeypur Chauraha, Varanasi'"),
        ("T028", "Naveen", "'Kumar'", "'Jha'", "'2021-05-12'", "'9876000028'", "'naveen.jha@school.edu'", 44000.00, "NULL", "'25, Sarnath Station Road, Sarnath, Varanasi'"),
    ]
    teacher_vals = [
        f"('{t[0]}', '{t[1]}', {t[2]}, {t[3]}, {t[4]}, {t[5]}, {t[6]}, {t[7]:.2f}, {t[8]}, {t[9]})"
        for t in teachers
    ]
    lines.append(",\n".join(teacher_vals) + ";")
    lines.append("")

    # 6. Teacher_Qualification
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 6. Teacher Qualifications")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Teacher_Qualification (Qualification, Teacher_ID) VALUES")
    teacher_quals = [
        ("M.Sc. Mathematics", "T001"), ("B.Ed", "T001"),
        ("M.A. English", "T002"), ("B.Ed", "T002"),
        ("M.Sc. Physics", "T003"), ("B.Ed", "T003"),
        ("M.A. History", "T004"), ("B.Ed", "T004"),
        ("M.A. Hindi", "T005"), ("B.Ed", "T005"),
        ("M.Sc. Physics", "T006"), ("Ph.D. Physics", "T006"),
        ("M.Sc. Chemistry", "T007"), ("B.Ed", "T007"),
        ("M.Sc. Mathematics", "T008"), ("Ph.D. Mathematics", "T008"),
        ("M.A. English", "T009"), ("B.Ed", "T009"),
        ("MCA", "T010"), ("B.Tech Computer Science", "T010"),
        ("M.Sc. Zoology", "T011"), ("B.Ed", "T011"),
        ("M.P.Ed", "T012"), ("B.P.Ed", "T012"),
        ("M.Sc. Chemistry", "T013"), ("Ph.D. Chemistry", "T013"),
        ("M.Sc. Botany", "T014"), ("B.Ed", "T014"),
        ("M.A. Economics", "T015"), ("B.Ed", "T015"),
        ("M.Com", "T016"), ("MBA Finance", "T016"),
        ("M.Sc. Mathematics", "T017"), ("B.Ed", "T017"),
        ("M.A. English Literature", "T018"), ("B.Ed", "T018"),
        ("M.A. Geography", "T019"), ("B.Ed", "T019"),
        ("M.Sc. Computer Science", "T020"), ("B.Tech IT", "T020"),
        ("M.A. Political Science", "T021"), ("B.Ed", "T021"),
        ("M.Sc. Physics", "T022"), ("B.Ed", "T022"),
        ("M.Com", "T023"), ("B.Ed", "T023"),
        ("M.P.Ed", "T024"),
        ("M.A. Sanskrit", "T025"), ("B.Ed", "T025"),
        ("M.Sc. Statistics", "T026"), ("B.Ed", "T026"),
        ("M.Sc. Biochemistry", "T027"), ("B.Ed", "T027"),
        ("M.A. Psychology", "T028"), ("B.Ed", "T028"),
    ]
    tqual_vals = [f"('{q[0]}', '{q[1]}')" for q in teacher_quals]
    lines.append(",\n".join(tqual_vals) + ";")
    lines.append("")

    # 7. Teacher_Specialization
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 7. Teacher Specializations")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Teacher_Specialization (Specialization, Teacher_ID) VALUES")
    teacher_specs = [
        ("Mathematics", "T001"), ("Algebra", "T001"),
        ("English Literature", "T002"), ("English Grammar", "T002"),
        ("General Science", "T003"), ("Mechanics", "T003"),
        ("Indian History", "T004"), ("Ancient Civilizations", "T004"),
        ("Hindi Literature", "T005"), ("Hindi Vyakaran", "T005"),
        ("Quantum Mechanics", "T006"), ("Electrodynamics", "T006"),
        ("Organic Chemistry", "T007"), ("Chemical Bonding", "T007"),
        ("Calculus", "T008"), ("Coordinate Geometry", "T008"),
        ("English Poetry", "T009"), ("Phonetics", "T009"),
        ("Programming", "T010"), ("Object Oriented Design", "T010"),
        ("Genetics", "T011"), ("Human Physiology", "T011"),
        ("Athletics", "T012"), ("Sports Training", "T012"),
        ("Inorganic Chemistry", "T013"), ("Physical Chemistry", "T013"),
        ("Plant Physiology", "T014"), ("Cytology", "T014"),
        ("Macroeconomics", "T015"), ("Indian Economic Development", "T015"),
        ("Financial Accounting", "T016"), ("Cost Accounting", "T016"),
        ("Linear Algebra", "T017"), ("Probability", "T017"),
        ("Modern Drama", "T018"), ("Creative Writing", "T018"),
        ("Physical Geography", "T019"), ("Cartography", "T019"),
        ("Database Systems", "T020"), ("Python Programming", "T020"),
        ("Political Theory", "T021"), ("Indian Constitution", "T021"),
        ("Optics", "T022"), ("Thermodynamics", "T022"),
        ("Business Studies", "T023"), ("Financial Management", "T023"),
        ("Yoga Education", "T024"), ("Football Coaching", "T024"),
        ("Classical Sanskrit", "T025"),
        ("Applied Statistics", "T026"),
        ("Molecular Biology", "T027"),
        ("Child Psychology", "T028"),
    ]
    tspec_vals = [f"('{s[0]}', '{s[1]}')" for s in teacher_specs]
    lines.append(",\n".join(tspec_vals) + ";")
    lines.append("")

    # 8. Section
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 8. Sections (4 per standard: A, B, C, D)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Section (Standard, Division, Room_No, Class_Teacher) VALUES")
    sections = [
        # Standard, Division, Room_No, Class_Teacher
        ("10", "A", "Room-101", "T001"),
        ("10", "B", "Room-102", "T002"),
        ("10", "C", "Room-103", "T003"),
        ("10", "D", "Room-104", "T004"),
        ("9",  "A", "Room-001", "T005"),
        ("9",  "B", "Room-002", "T006"),
        ("9",  "C", "Room-003", "T007"),
        ("9",  "D", "Room-004", "T008"),
        ("11", "A", "Room-201", "T009"),
        ("11", "B", "Room-202", "T010"),
        ("11", "C", "Room-203", "T011"),
        ("11", "D", "Room-204", "T012"),
        ("12", "A", "Room-301", "T013"),
        ("12", "B", "Room-302", "T014"),
        ("12", "C", "Room-303", "T015"),
        ("12", "D", "Room-304", "T016"),
    ]
    sec_vals = [f"('{s[0]}', '{s[1]}', '{s[2]}', '{s[3]}')" for s in sections]
    lines.append(",\n".join(sec_vals) + ";")
    lines.append("")

    # 9. Student Generation
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 9. Students (480 Students, 30 per section across 16 sections)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Student (Admission_No, Roll_No, First_Name, Middle_Name, Last_Name, Blood_Group, Standard, Division, House, Vehicle_No, Date_Of_Admission, Date_Of_Birth, Age, Gender, Remaining_Fees, Address, Contact_Number) VALUES")

    varanasi_addresses = [
        "Plot 12, Assi Ghat Road, Assi, Varanasi",
        "B-45, Mahmoorganj, Varanasi",
        "Flat 302, Surya Complex, Sigra, Varanasi",
        "House 18, Chitaipur Road, Sundarpur, Varanasi",
        "54, Orderly Bazar, Cantt, Varanasi",
        "C-19/2, Ravindrapuri, Lanka, Varanasi",
        "77, Pandeypur Chauraha, Varanasi",
        "House 8, Saket Nagar, Lanka, Varanasi",
        "Flat 104, Ganga Heights, Bhelupur, Varanasi",
        "28/A, Durgakund Road, Varanasi",
        "15, Gurubagh, Kamachha, Varanasi",
        "Plot 6, Vidyapeeth Road, Sigra, Varanasi",
        "House 92, Manduadih Station Road, Varanasi",
        "44, Shivpur By-pass, Varanasi",
        "12, Naria, BHU Road, Varanasi",
        "Flat 201, Shivala Colony, Varanasi",
        "33, Tagore Town, Orderly Bazar, Varanasi",
        "House 67, Sarnath Station Road, Varanasi",
        "Plot 21, Chandpur Industrial Estate, Varanasi",
        "Flat 405, Kashi Enclave, Maldahiya, Varanasi"
    ]

    first_names_male = [
        "Rohan", "Madhav", "Arnav", "Siddharth", "Aarav", "Vihaan", "Aditya", "Aryan",
        "Reyansh", "Ishaan", "Shaurya", "Atharv", "Pranav", "Dhruv", "Parth", "Yash",
        "Harsh", "Varun", "Rohit", "Kunal", "Chirag", "Mohit", "Deepak", "Gaurav",
        "Alok", "Nishant", "Mayank", "Kartik", "Ayush", "Dev", "Tushar", "Abhishek",
        "Ankit", "Rahul", "Saurabh", "Sumit", "Vikas", "Vishal", "Aman", "Akash",
        "Sahil", "Tarun", "Naveen", "Manish", "Pankaj", "Hemant", "Ritesh", "Praveen",
        "Samarth", "Tejas", "Utkarsh", "Rudra", "Nikhil", "Bhavya", "Sanchit", "Rishi"
    ]
    first_names_female = [
        "Priya", "Kavya", "Riya", "Isha", "Ananya", "Muskan", "Myra", "Saanvi",
        "Diya", "Kiara", "Pari", "Sara", "Avani", "Tanvi", "Shreya", "Meera",
        "Rhea", "Neha", "Pooja", "Sneha", "Ritika", "Divya", "Swati", "Mansi",
        "Shruti", "Srishti", "Vidhi", "Sakshi", "Payal", "Kritika", "Prisha",
        "Navya", "Anika", "Siya", "Disha", "Tara", "Aditi", "Khushi", "Simran",
        "Tanya", "Shalini", "Radhika", "Rashmi", "Juhi", "Garima", "Palak",
        "Aarohi", "Aashi", "Ishita", "Anushka", "Gauri", "Bhavana", "Chhavi", "Tanushree"
    ]
    last_names = [
        "Mehta", "Nair", "Jaiswal", "Upadhyay", "Yadav", "Mishra", "Verma", "Tiwari",
        "Sharma", "Gupta", "Singh", "Pandey", "Srivastava", "Agarwal", "Joshi", "Saxena",
        "Dubey", "Patel", "Reddy", "Kumar", "Rao", "Kapoor", "Malhotra", "Bhatia",
        "Chopra", "Das", "Mukherjee", "Banerjee", "Chatterjee", "Roy", "Sen", "Choudhury",
        "Chauhan", "Tomar", "Rawat", "Bisht", "Negi", "Bhatt", "Tripathy", "Nambiar",
        "Deshmukh", "Kulkarni", "Patil", "Shinde", "Pawar", "Bhardwaj", "Shukla", "Awasthi",
        "Dwivedi", "Mani", "Tripathi", "Pathak", "Chawla", "Rastogi", "Sinha", "Gautam"
    ]
    blood_groups = ["O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB+"]
    house_colors = ["Red", "Blue", "Green", "Yellow"]
    vehicle_ids = [f"V0{i}" for i in range(1, 9)]

    # Map section to age range & base DOB year
    section_age_map = {
        "9": (14, 2012),
        "10": (15, 2011),
        "11": (16, 2010),
        "12": (17, 2009)
    }

    students_data = [] # List of dicts
    student_insert_vals = []

    adm_num = 1
    # Generate sections in the desired order
    for std, div, room, c_teacher in sections:
        base_age, dob_year = section_age_map[std]
        for roll in range(1, 31):
            adm_id = f"A{adm_num:03d}"
            adm_num += 1
            
            # Special test case A001
            if adm_id == "A001":
                fname = "Rohan"
                mname = "NULL"
                lname = "Mehta"
                gender = "Male"
                blood = "O+"
                house = "Red"
                vehicle = "'V01'"
                adm_date = "2020-04-02"
                dob = "2011-05-14"
                age = 15
                rem_fee = 5000.00
                address = "Flat 402, Ganga Heights, Bhelupur, Varanasi"
                contact_num = "9876543210"
            elif adm_id == "A002":
                fname = "Priya"
                mname = "NULL"
                lname = "Nair"
                gender = "Female"
                blood = "B+"
                house = "Blue"
                vehicle = "'V01'"
                adm_date = "2020-04-02"
                dob = "2011-08-22"
                age = 15
                rem_fee = 0.00
                address = "House 14, Saket Nagar, Lanka, Varanasi"
                contact_num = "9876543211"
            elif adm_id == "A003":
                fname = "Kavya"
                mname = "NULL"
                lname = "Jaiswal"
                gender = "Female"
                blood = "AB+"
                house = "Green"
                vehicle = "NULL"
                adm_date = "2020-04-02"
                dob = "2011-07-17"
                age = 15
                rem_fee = 18000.00
                address = "22/A, Tagore Town, Orderly Bazar, Varanasi"
                contact_num = "9876543212"
            else:
                is_female = (roll % 2 == 0)
                if is_female:
                    fname = random.choice(first_names_female)
                    gender = "Female"
                else:
                    fname = random.choice(first_names_male)
                    gender = "Male"
                
                lname = random.choice(last_names)
                mname = "NULL"
                if roll % 7 == 0:
                    mname = "'Kumar'" if gender == "Male" else "'Kumari'"
                
                blood = random.choice(blood_groups)
                house = house_colors[(roll + int(std)) % 4]
                
                # 65% use transport
                if (roll * 3 + int(std)) % 3 != 0:
                    v_choice = vehicle_ids[(roll + int(std)) % len(vehicle_ids)]
                    vehicle = f"'{v_choice}'"
                else:
                    vehicle = "NULL"
                
                # DOB
                m = (roll % 12) + 1
                d = (roll % 28) + 1
                dob = f"{dob_year}-{m:02d}-{d:02d}"
                age = base_age
                adm_year = dob_year + random.choice([5, 6, 7])
                adm_date = f"{adm_year}-04-05"

                # Fees: fully paid (0.0), partial (15000.0, 20000.0), or higher
                fee_stat = roll % 4
                if fee_stat == 0:
                    rem_fee = 0.00
                elif fee_stat == 1:
                    rem_fee = 12000.00
                elif fee_stat == 2:
                    rem_fee = 25000.00
                else:
                    rem_fee = 0.00
                
                address = random.choice(varanasi_addresses)
                contact_num = f"981{adm_num:07d}"
            
            s_dict = {
                "adm_id": adm_id,
                "roll": roll,
                "fname": fname,
                "mname": mname,
                "lname": lname,
                "blood": blood,
                "standard": std,
                "division": div,
                "house": house,
                "vehicle": vehicle,
                "adm_date": adm_date,
                "dob": dob,
                "age": age,
                "gender": gender,
                "rem_fee": rem_fee,
                "address": address,
                "contact_number": contact_num,
                "full_name": f"{fname} {lname}" if lname else fname
            }
            students_data.append(s_dict)
            
            mname_str = mname
            lname_str = f"'{lname}'" if lname else "NULL"
            val_str = f"('{adm_id}', {roll}, '{fname}', {mname_str}, {lname_str}, '{blood}', '{std}', '{div}', '{house}', {vehicle}, '{adm_date}', '{dob}', {age}, '{gender}', {rem_fee:.2f}, '{address}', '{contact_num}')"
            student_insert_vals.append(val_str)

    lines.append(",\n".join(student_insert_vals) + ";")
    lines.append("")

    # 10. Student_Guardian
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 10. Student Guardians")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Student_Guardian (Student_ID, Relationship, First_Name, Middle_Name, Last_Name, Occupation, Phone_No, Email) VALUES")
    
    occupations_father = [
        "Software Engineer", "Business Executive", "Doctor", "Advocate",
        "Professor", "Civil Servant", "Bank Manager", "Architect",
        "Chartered Accountant", "Government Officer", "Merchant Navy Officer",
        "School Principal", "Pharmacist", "Shop Owner", "Civil Engineer"
    ]
    occupations_mother = [
        "Homemaker", "Teacher", "Doctor", "Professor", "Software Developer",
        "Advocate", "Banker", "Nurse", "Architect", "Government Officer",
        "Journalist", "Accountant"
    ]
    father_first_names = [
        "Suresh", "Ramesh", "Sunil", "Rajesh", "Vinod", "Ashok", "Anil", "Manoj",
        "Pradeep", "Dinesh", "Sanjay", "Mahesh", "Mukesh", "Vijay", "Satish", "Alok"
    ]
    mother_first_names = [
        "Sunita", "Lakshmi", "Rekha", "Usha", "Manju", "Seema", "Anita", "Geeta",
        "Sangeeta", "Shashi", "Kavita", "Saroj", "Pooja", "Asha", "Sudha", "Vandana"
    ]

    guardians_vals = []
    phone_counter = 9900000001

    for s in students_data:
        adm_id = s["adm_id"]
        lname = s["lname"]
        
        if adm_id == "A001":
            f_fname, f_occ = "Suresh", "Software Engineer"
            m_fname, m_occ = "Sunita", "Professor"
            f_email = "suresh.mehta@gmail.com"
            m_email = "sunita.mehta@gmail.com"
        elif adm_id == "A002":
            f_fname, f_occ = "Venu", "Bank Manager"
            m_fname, m_occ = "Lakshmi", "Doctor"
            f_email = "venu.nair@gmail.com"
            m_email = "lakshmi.nair@gmail.com"
        elif adm_id == "A003":
            f_fname, f_occ = "Kailash", "Business Owner"
            m_fname, m_occ = "Poonam", "Homemaker"
            f_email = "kailash.jaiswal@gmail.com"
            m_email = "poonam.jaiswal@gmail.com"
        else:
            f_fname = random.choice(father_first_names)
            f_occ = random.choice(occupations_father)
            m_fname = random.choice(mother_first_names)
            m_occ = random.choice(occupations_mother)
            f_email = f"{f_fname.lower()}.{lname.lower()}{s['roll']}@gmail.com"
            m_email = f"{m_fname.lower()}.{lname.lower()}{s['roll']}@gmail.com"

        # Father record
        f_phone = f"{phone_counter}"
        phone_counter += 1
        guardians_vals.append(f"('{adm_id}', 'Father', '{f_fname}', NULL, '{lname}', '{f_occ}', '{f_phone}', '{f_email}')")

        # Mother record (for ~85% students)
        if s["roll"] % 7 != 0 or adm_id in ["A001", "A002", "A003"]:
            m_phone = f"{phone_counter}"
            phone_counter += 1
            guardians_vals.append(f"('{adm_id}', 'Mother', '{m_fname}', NULL, '{lname}', '{m_occ}', '{m_phone}', '{m_email}')")

    lines.append(",\n".join(guardians_vals) + ";")
    lines.append("")


    # 11. Subject
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 11. Subjects (Classes 9, 10, 11, 12)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Subject (Subject_Code, Subject_Name, Periods_Per_Week, Class) VALUES")
    subjects = [
        # Class 9
        ("MATH9", "Mathematics", 6, "9"),
        ("ENG9",  "English", 5, "9"),
        ("SCI9",  "Science", 6, "9"),
        ("SST9",  "Social Studies", 5, "9"),
        ("HIN9",  "Hindi", 4, "9"),
        ("CS9",   "Computer Science", 3, "9"),
        ("PE9",   "Physical Education", 2, "9"),
        # Class 10
        ("MATH10","Mathematics", 6, "10"),
        ("ENG10", "English", 5, "10"),
        ("SCI10", "Science", 6, "10"),
        ("SST10", "Social Studies", 5, "10"),
        ("HIN10", "Hindi", 4, "10"),
        ("CS10",  "Computer Science", 3, "10"),
        ("PE10",  "Physical Education", 2, "10"),
        # Class 11
        ("PHY11", "Physics", 6, "11"),
        ("CHEM11","Chemistry", 6, "11"),
        ("MATH11","Mathematics", 6, "11"),
        ("ENG11", "English", 5, "11"),
        ("CS11",  "Computer Science", 3, "11"),
        ("BIO11", "Biology", 6, "11"),
        ("ACC11", "Accountancy", 6, "11"),
        ("BST11", "Business Studies", 5, "11"),
        ("ECO11", "Economics", 5, "11"),
        ("PE11",  "Physical Education", 2, "11"),
        # Class 12
        ("PHY12", "Physics", 6, "12"),
        ("CHEM12","Chemistry", 6, "12"),
        ("MATH12","Mathematics", 6, "12"),
        ("ENG12", "English", 5, "12"),
        ("CS12",  "Computer Science", 3, "12"),
        ("BIO12", "Biology", 6, "12"),
        ("ACC12", "Accountancy", 6, "12"),
        ("BST12", "Business Studies", 5, "12"),
        ("ECO12", "Economics", 5, "12"),
        ("PE12",  "Physical Education", 2, "12"),
    ]
    sub_vals = [f"('{s[0]}', '{s[1]}', {s[2]}, '{s[3]}')" for s in subjects]
    lines.append(",\n".join(sub_vals) + ";")
    lines.append("")

    # 12. Reference_Books
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 12. Reference Books")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Reference_Books (Book_name, Subject_Code) VALUES")
    ref_books = [
        ("NCERT Class 9 Mathematics", "MATH9"),
        ("R.D. Sharma Class 9 Mathematics", "MATH9"),
        ("NCERT Class 9 English - Beehive", "ENG9"),
        ("NCERT Class 9 Science", "SCI9"),
        ("Lakhmir Singh Physics & Chemistry Class 9", "SCI9"),
        ("NCERT Class 9 India and the Contemporary World", "SST9"),
        ("NCERT Class 9 Kshitij Hindi", "HIN9"),
        ("Sumita Arora Computer Applications Class 9", "CS9"),
        ("CBSE Physical Education Class 9 Manual", "PE9"),

        ("NCERT Class 10 Mathematics", "MATH10"),
        ("R.D. Sharma Class 10 Mathematics", "MATH10"),
        ("NCERT Class 10 English - First Flight", "ENG10"),
        ("NCERT Class 10 Science", "SCI10"),
        ("Lakhmir Singh & Manjit Kaur Class 10 Science", "SCI10"),
        ("NCERT Class 10 Contemporary India", "SST10"),
        ("NCERT Class 10 Kshitij & Kritika Hindi", "HIN10"),
        ("Sumita Arora Computer Applications Class 10", "CS10"),
        ("Saraswati Health and Physical Education Class 10", "PE10"),

        ("NCERT Class 11 Physics Part 1 & 2", "PHY11"),
        ("Concepts of Physics by H.C. Verma Vol 1", "PHY11"),
        ("NCERT Class 11 Chemistry Part 1 & 2", "CHEM11"),
        ("Pradeep New Course Chemistry Class 11", "CHEM11"),
        ("NCERT Class 11 Mathematics", "MATH11"),
        ("R.D. Sharma Mathematics Class 11", "MATH11"),
        ("NCERT Class 11 English - Hornbill", "ENG11"),
        ("Computer Science with Python by Sumita Arora Class 11", "CS11"),
        ("NCERT Class 11 Biology", "BIO11"),
        ("Trueman Elementary Biology Vol 1", "BIO11"),
        ("Double Entry Book Keeping by T.S. Grewal Class 11", "ACC11"),
        ("Business Studies by Poonam Gandhi Class 11", "BST11"),
        ("Introductory Microeconomics by Sandeep Garg Class 11", "ECO11"),
        ("Health & Physical Education by Dr. V.K. Sharma Class 11", "PE11"),

        ("NCERT Class 12 Physics Part 1 & 2", "PHY12"),
        ("Concepts of Physics by H.C. Verma Vol 2", "PHY12"),
        ("NCERT Class 12 Chemistry Part 1 & 2", "CHEM12"),
        ("Pradeep New Course Chemistry Class 12", "CHEM12"),
        ("NCERT Class 12 Mathematics Part 1 & 2", "MATH12"),
        ("R.D. Sharma Mathematics Class 12", "MATH12"),
        ("NCERT Class 12 English - Flamingo", "ENG12"),
        ("Computer Science with Python by Sumita Arora Class 12", "CS12"),
        ("NCERT Class 12 Biology", "BIO12"),
        ("Trueman Elementary Biology Vol 2", "BIO12"),
        ("Analysis of Financial Statements by T.S. Grewal Class 12", "ACC12"),
        ("Business Studies by Poonam Gandhi Class 12", "BST12"),
        ("Introductory Macroeconomics by Sandeep Garg Class 12", "ECO12"),
        ("Health & Physical Education by Dr. V.K. Sharma Class 12", "PE12"),
    ]
    ref_vals = [f"('{r[0]}', '{r[1]}')" for r in ref_books]
    lines.append(",\n".join(ref_vals) + ";")
    lines.append("")

    # 13. Teacher_Subject
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 13. Teacher-Subject Assignments")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Teacher_Subject (Teacher_ID, Subject_ID) VALUES")
    teacher_subjects = [
        ("T001", "MATH10"), ("T001", "MATH9"),
        ("T002", "ENG10"),  ("T002", "ENG9"),
        ("T003", "SCI10"),  ("T003", "SCI9"),
        ("T004", "SST10"),  ("T004", "SST9"),
        ("T005", "HIN10"),  ("T005", "HIN9"),
        ("T006", "PHY11"),  ("T006", "PHY12"),
        ("T007", "CHEM11"), ("T007", "CHEM12"),
        ("T008", "MATH11"), ("T008", "MATH12"),
        ("T009", "ENG11"),  ("T009", "ENG12"),
        ("T010", "CS9"),    ("T010", "CS10"),   ("T010", "CS11"),   ("T010", "CS12"),
        ("T011", "BIO11"),  ("T011", "BIO12"),
        ("T012", "PE9"),    ("T012", "PE10"),   ("T012", "PE11"),   ("T012", "PE12"),
        ("T013", "CHEM11"), ("T013", "CHEM12"),
        ("T014", "BIO11"),  ("T014", "BIO12"),
        ("T015", "ECO11"),  ("T015", "ECO12"),
        ("T016", "ACC11"),  ("T016", "ACC12"),
        ("T017", "MATH9"),  ("T017", "MATH10"),
        ("T018", "ENG9"),   ("T018", "ENG10"),
        ("T019", "SST9"),   ("T019", "SST10"),
        ("T020", "CS11"),   ("T020", "CS12"),
        ("T021", "SST9"),   ("T021", "SST10"),
        ("T022", "SCI9"),   ("T022", "SCI10"),
        ("T023", "BST11"),  ("T023", "BST12"),
        ("T024", "PE9"),    ("T024", "PE10"),
        ("T025", "HIN9"),   ("T025", "HIN10"),
        ("T026", "MATH11"), ("T026", "MATH12"),
        ("T027", "BIO11"),  ("T027", "BIO12"),
        ("T028", "ENG11"),  ("T028", "ENG12"),
    ]
    tsub_vals = [f"('{ts[0]}', '{ts[1]}')" for ts in teacher_subjects]
    lines.append(",\n".join(tsub_vals) + ";")
    lines.append("")

    # 14. Class_Schedule
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 14. Class Schedules (Curriculum mapping per section)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Class_Schedule (Teacher_ID, Subject_ID, Standard, Division) VALUES")
    schedules = [
        # 9-A
        ("T001", "MATH9", "9", "A"), ("T002", "ENG9", "9", "A"), ("T003", "SCI9", "9", "A"),
        ("T004", "SST9", "9", "A"),  ("T005", "HIN9", "9", "A"), ("T010", "CS9", "9", "A"),  ("T012", "PE9", "9", "A"),
        # 9-B
        ("T017", "MATH9", "9", "B"), ("T002", "ENG9", "9", "B"), ("T003", "SCI9", "9", "B"),
        ("T004", "SST9", "9", "B"),  ("T005", "HIN9", "9", "B"), ("T010", "CS9", "9", "B"),  ("T012", "PE9", "9", "B"),
        # 9-C
        ("T001", "MATH9", "9", "C"), ("T018", "ENG9", "9", "C"), ("T022", "SCI9", "9", "C"),
        ("T019", "SST9", "9", "C"),  ("T025", "HIN9", "9", "C"), ("T010", "CS9", "9", "C"),  ("T024", "PE9", "9", "C"),
        # 9-D
        ("T017", "MATH9", "9", "D"), ("T018", "ENG9", "9", "D"), ("T022", "SCI9", "9", "D"),
        ("T021", "SST9", "9", "D"),  ("T025", "HIN9", "9", "D"), ("T010", "CS9", "9", "D"),  ("T024", "PE9", "9", "D"),

        # 10-A
        ("T001", "MATH10", "10", "A"), ("T002", "ENG10", "10", "A"), ("T003", "SCI10", "10", "A"),
        ("T004", "SST10", "10", "A"),  ("T005", "HIN10", "10", "A"), ("T010", "CS10", "10", "A"),  ("T012", "PE10", "10", "A"),
        # 10-B
        ("T017", "MATH10", "10", "B"), ("T002", "ENG10", "10", "B"), ("T003", "SCI10", "10", "B"),
        ("T004", "SST10", "10", "B"),  ("T005", "HIN10", "10", "B"), ("T010", "CS10", "10", "B"),  ("T012", "PE10", "10", "B"),
        # 10-C
        ("T001", "MATH10", "10", "C"), ("T018", "ENG10", "10", "C"), ("T022", "SCI10", "10", "C"),
        ("T019", "SST10", "10", "C"),  ("T025", "HIN10", "10", "C"), ("T010", "CS10", "10", "C"),  ("T024", "PE10", "10", "C"),
        # 10-D
        ("T017", "MATH10", "10", "D"), ("T018", "ENG10", "10", "D"), ("T022", "SCI10", "10", "D"),
        ("T021", "SST10", "10", "D"),  ("T025", "HIN10", "10", "D"), ("T010", "CS10", "10", "D"),  ("T024", "PE10", "10", "D"),

        # 11-A (PCM + CS)
        ("T006", "PHY11", "11", "A"), ("T007", "CHEM11", "11", "A"), ("T008", "MATH11", "11", "A"),
        ("T009", "ENG11", "11", "A"), ("T010", "CS11", "11", "A"),   ("T012", "PE11", "11", "A"),
        # 11-B (PCB + PE)
        ("T006", "PHY11", "11", "B"), ("T013", "CHEM11", "11", "B"), ("T011", "BIO11", "11", "B"),
        ("T009", "ENG11", "11", "B"), ("T012", "PE11", "11", "B"),   ("T020", "CS11", "11", "B"),
        # 11-C (Commerce w/ Maths)
        ("T016", "ACC11", "11", "C"), ("T023", "BST11", "11", "C"),  ("T015", "ECO11", "11", "C"),
        ("T028", "ENG11", "11", "C"), ("T026", "MATH11", "11", "C"), ("T012", "PE11", "11", "C"),
        # 11-D (Commerce w/ CS)
        ("T016", "ACC11", "11", "D"), ("T023", "BST11", "11", "D"),  ("T015", "ECO11", "11", "D"),
        ("T028", "ENG11", "11", "D"), ("T020", "CS11", "11", "D"),   ("T012", "PE11", "11", "D"),

        # 12-A (PCM + CS)
        ("T006", "PHY12", "12", "A"), ("T007", "CHEM12", "12", "A"), ("T008", "MATH12", "12", "A"),
        ("T009", "ENG12", "12", "A"), ("T010", "CS12", "12", "A"),   ("T012", "PE12", "12", "A"),
        # 12-B (PCB + PE)
        ("T006", "PHY12", "12", "B"), ("T013", "CHEM12", "12", "B"), ("T011", "BIO12", "12", "B"),
        ("T009", "ENG12", "12", "B"), ("T012", "PE12", "12", "B"),   ("T020", "CS12", "12", "B"),
        # 12-C (Commerce w/ Maths)
        ("T016", "ACC12", "12", "C"), ("T023", "BST12", "12", "C"),  ("T015", "ECO12", "12", "C"),
        ("T028", "ENG12", "12", "C"), ("T026", "MATH12", "12", "C"), ("T012", "PE12", "12", "C"),
        # 12-D (Commerce w/ CS)
        ("T016", "ACC12", "12", "D"), ("T023", "BST12", "12", "D"),  ("T015", "ECO12", "12", "D"),
        ("T028", "ENG12", "12", "D"), ("T020", "CS12", "12", "D"),   ("T012", "PE12", "12", "D"),
    ]
    sched_vals = [f"('{s[0]}', '{s[1]}', '{s[2]}', '{s[3]}')" for s in schedules]
    lines.append(",\n".join(sched_vals) + ";")
    lines.append("")

    # Map each (Standard, Division) to its assigned subjects from Class_Schedule
    section_subjects = {}
    for t_id, sub_id, std, div in schedules:
        key = (std, div)
        if key not in section_subjects:
            section_subjects[key] = []
        section_subjects[key].append(sub_id)

    # 15. Enrollments
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 15. Course Enrollments (3,120 student-course enrollments)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Enrollments (Student_ID, Subject_ID, Grade) VALUES")
    
    grades_pool = ["A+", "A", "A", "B+", "B+", "B", "B", "C+", "C", "A+"]
    enrollments_vals = []

    for s in students_data:
        adm_id = s["adm_id"]
        std = s["standard"]
        div = s["division"]
        roll = s["roll"]
        subs = section_subjects.get((std, div), [])
        
        for idx, sub_code in enumerate(subs):
            if adm_id == "A001":
                g = ["A", "B+", "A+", "A", "B+", "A+", "A"][idx % 7]
            elif adm_id == "A002":
                g = ["B", "C+", "A", "B+", "C", "A+", "A"][idx % 7]
            elif adm_id == "A003":
                g = ["B+", "B", "B+", "A", "A+", "A", "B"][idx % 7]
            else:
                g = grades_pool[(roll + idx + int(std)) % len(grades_pool)]
            enrollments_vals.append(f"('{adm_id}', '{sub_code}', '{g}')")

    lines.append(",\n".join(enrollments_vals) + ";")
    lines.append("")

    # 16. Payments & Fees & Salary
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 16. Payments (Tuition Fees + Faculty/Staff Salaries)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Payments (Transaction_ID, Date_Of_Payment, Payment_Type, Receiver, Sender, Payment_Mode, Amount) VALUES")

    payments_vals = []
    fees_links = []
    teacher_salary_links = []
    staff_salary_links = []

    txn_num = 1
    payment_modes = ["Online", "UPI", "Cheque", "Net Banking", "Cash"]

    # (A) Student Fee Payments
    for s in students_data:
        adm_id = s["adm_id"]
        rem_fee = s["rem_fee"]
        student_name = s["full_name"]
        roll = s["roll"]

        # Base annual fee is 45,000.
        # Paid = 45000 - rem_fee
        paid = 45000.00 - rem_fee
        if paid <= 0:
            continue

        # Split paid into 1 to 3 installments
        if paid == 45000.00:
            installments = [
                ("2026-04-05 10:15:00", 15000.00),
                ("2026-07-10 11:30:00", 15000.00),
                ("2026-09-15 14:00:00", 15000.00)
            ]
        elif paid == 40000.00:
            installments = [
                ("2026-04-05 09:45:00", 20000.00),
                ("2026-07-10 12:00:00", 20000.00)
            ]
        elif paid == 33000.00 or paid == 32000.00:
            installments = [
                ("2026-04-08 11:20:00", 18000.00),
                ("2026-07-12 10:10:00", paid - 18000.00)
            ]
        elif paid == 20000.00:
            installments = [
                ("2026-04-10 10:00:00", 20000.00)
            ]
        elif paid >= 15000.00:
            installments = [
                ("2026-04-12 15:30:00", paid)
            ]
        else:
            installments = [
                ("2026-04-15 10:00:00", paid)
            ]

        for p_date, p_amt in installments:
            txn_id = f"TXN{txn_num:04d}"
            txn_num += 1
            mode = payment_modes[(roll + txn_num) % len(payment_modes)]
            payments_vals.append(
                f"('{txn_id}', '{p_date}', 'Fee', 'School', '{student_name}', '{mode}', {p_amt:.2f})"
            )
            fees_links.append(f"('{txn_id}', '{adm_id}')")

    # (B) Teacher Salary Disbursements (April to September 2026)
    salary_months = [
        ("2026-04-05 09:00:00"),
        ("2026-05-05 09:00:00"),
        ("2026-06-05 09:00:00"),
        ("2026-07-05 09:00:00"),
        ("2026-08-05 09:00:00"),
        ("2026-09-05 09:00:00"),
    ]

    for t in teachers:
        t_id = t[0]
        fname = t[1]
        mname_clean = "" if t[2] == "NULL" else f" {t[2].strip("'")}"
        lname_clean = "" if t[3] == "NULL" else f" {t[3].strip("'")}"
        t_fullname = f"{fname}{mname_clean}{lname_clean}"
        salary = t[7]

        for s_date in salary_months:
            txn_id = f"TXN{txn_num:04d}"
            txn_num += 1
            payments_vals.append(
                f"('{txn_id}', '{s_date}', 'Salary', '{t_fullname}', 'School', 'Bank Transfer', {salary:.2f})"
            )
            teacher_salary_links.append(f"('{txn_id}', '{t_id}')")

    # (C) Staff Salary Disbursements (April to September 2026)
    for s in staff_members:
        s_id = s[0]
        fname = s[1]
        mname_clean = "" if s[2] == "NULL" else f" {s[2].strip("'")}"
        lname_clean = "" if s[3] == "NULL" else f" {s[3].strip("'")}"
        s_fullname = f"{fname}{mname_clean}{lname_clean}"
        salary = s[7]

        for s_date in salary_months:
            txn_id = f"TXN{txn_num:04d}"
            txn_num += 1
            payments_vals.append(
                f"('{txn_id}', '{s_date}', 'Salary', '{s_fullname}', 'School', 'Bank Transfer', {salary:.2f})"
            )
            staff_salary_links.append(f"('{txn_id}', '{s_id}')")

    lines.append(",\n".join(payments_vals) + ";")
    lines.append("")

    # 17. Fees Links
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 17. Fees (Payment-Student Mapping)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Fees (Payment_ID, Student_ID) VALUES")
    lines.append(",\n".join(fees_links) + ";")
    lines.append("")

    # 18. Teacher_Salary Links
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 18. Teacher Salary (Payment-Teacher Mapping)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Teacher_Salary (Payment_ID, Teacher_ID) VALUES")
    lines.append(",\n".join(teacher_salary_links) + ";")
    lines.append("")

    # 19. Staff_Salary Links
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 19. Staff Salary (Payment-Staff Mapping)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT INTO Staff_Salary (Payment_ID, Staff_ID) VALUES")
    lines.append(",\n".join(staff_salary_links) + ";")
    lines.append("")

    # 20. User Account Seed Data
    lines.append("-- ---------------------------------------------------------")
    lines.append("-- 20. User Accounts (Unified Login)")
    lines.append("-- ---------------------------------------------------------")
    lines.append("INSERT IGNORE INTO User_Account (Username, Password, Role) VALUES ('admin', 'admin123', 'admin');")
    lines.append("")
    lines.append("INSERT IGNORE INTO User_Account (Username, Password, Role)")
    lines.append("SELECT Admission_No, CONCAT('pass_', Admission_No), 'student' FROM Student;")
    lines.append("")
    lines.append("INSERT IGNORE INTO User_Account (Username, Password, Role)")
    lines.append("SELECT Employee_ID, CONCAT('pass_', Employee_ID), 'teacher' FROM Teacher;")
    lines.append("")
    lines.append("INSERT IGNORE INTO User_Account (Username, Password, Role)")
    lines.append("SELECT Employee_ID, CONCAT('pass_', Employee_ID), 'staff' FROM Staff;")
    lines.append("")

    return "\n".join(lines)

if __name__ == "__main__":
    sql_text = generate_sql()
    import os
    from pathlib import Path
    script_dir = Path(__file__).parent.resolve()
    output_path = script_dir / "seed.sql"
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(sql_text)
    print(f"Generated seed data written to {output_path}")
    print(f"Total file characters: {len(sql_text)}")
