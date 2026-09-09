-- =========================================================
-- Just for inserting dummy data and testing it out
-- NOTE: Run school_dbms_schema.sql FIRST to create the tables.
-- =========================================================

USE school_dbms;

INSERT INTO House VALUES
('Red', 'Phoenix House', 12),
('Blue', 'Neptune House', 9),
('Green', 'Falcon House', 15);

INSERT INTO Class VALUES (9), (10), (11);

INSERT INTO Staff VALUES
('S001','Ramesh',NULL,'Kumar','9876500001','Driver','2019-06-01',22000.00),
('S002','Sunita',NULL,'Sharma','9876500002','Accountant','2018-04-15',35000.00);

INSERT INTO Transport VALUES
('V01','Route A - Sigra','UP65AB1234',40,'S001');

INSERT INTO Teacher (Employee_ID, First_Name, Middle_Name, Last_Name,
    Date_Of_Joining, Phone_No, Email, Salary)
VALUES
('T001','Anita',NULL,'Verma','2015-07-10','9876000001','anita.verma@school.edu',52000.00),
('T002','Rakesh','K.','Singh','2017-03-22','9876000002','rakesh.singh@school.edu',48000.00);

INSERT INTO Teacher_Qualification VALUES
('M.Sc. Mathematics','T001'),
('B.Ed','T001'),
('M.A. English','T002');

INSERT INTO Teacher_Specialization VALUES
('Mathematics','T001'),
('English Literature','T002');

INSERT INTO Section VALUES
(10,'A','Room-101','T001'),
(10,'B','Room-102','T002');

UPDATE Teacher SET Mentor_Standard=10, Mentor_Division='A' WHERE Employee_ID='T001';
UPDATE Teacher SET Mentor_Standard=10, Mentor_Division='B' WHERE Employee_ID='T002';

INSERT INTO Student (Admission_No, Roll_No, First_Name, Middle_Name, Last_Name,
    Blood_Group, Standard, Division, House, Vehicle_No, Date_Of_Admission,
    Date_Of_Birth, Age, Gender, Remaining_Fees)
VALUES
('A001',1,'Rohan',NULL,'Mehta','O+',10,'A','Red','V01','2021-04-01','2010-05-14',15,'Male',5000.00),
('A002',2,'Priya',NULL,'Nair','B+',10,'A','Blue','V01','2021-04-01','2010-08-22',15,'Female',0.00);

INSERT INTO Student_Guardian VALUES
('A001','Father','Suresh',NULL,'Mehta','Engineer','9998887771'),
('A002','Mother','Lakshmi',NULL,'Nair','Doctor','9998887772');

INSERT INTO Subject VALUES
('MATH10','Mathematics','NCERT Class 10 Maths',6,10),
('ENG10','English','NCERT Class 10 English',5,10);

INSERT INTO Teacher_Subject VALUES
('T001','MATH10'),
('T002','ENG10');

INSERT INTO Enrollments VALUES
('A001','MATH10','A'),
('A001','ENG10','B'),
('A002','MATH10','A+');

INSERT INTO Class_Schedule VALUES
('T001','MATH10',10,'A'),
('T002','ENG10',10,'A');

INSERT INTO Payments VALUES
('TXN001','2025-04-05','Fee','School','Rohan Mehta','Online',15000.00),
('TXN002','2025-04-05','Salary','Anita Verma','School','Bank Transfer',52000.00);

INSERT INTO Fees VALUES ('TXN001','A001');
INSERT INTO Teacher_Salary VALUES ('TXN002','T001');
