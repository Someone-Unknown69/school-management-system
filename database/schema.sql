-- =========================================================
-- School Management System - Database Schema
-- =========================================================

DROP DATABASE IF EXISTS school_dbms;
CREATE DATABASE school_dbms;
USE school_dbms;

-- ---------------------------------------------------------
-- Independent / lookup entities
-- ---------------------------------------------------------

CREATE TABLE House (
    Color        VARCHAR(30)  PRIMARY KEY,
    House_Name   VARCHAR(50)  NOT NULL,
    No_Of_Wins   INT          DEFAULT 0
);

CREATE TABLE Class (
    Standard     INT PRIMARY KEY   -- 1 to 12
);

CREATE TABLE Transport (
    Vehicle_ID          VARCHAR(10)  PRIMARY KEY,
    Route_Name          VARCHAR(50)  NOT NULL,
    Registration_Number VARCHAR(20)  UNIQUE NOT NULL,
    Capacity            INT          NOT NULL,
    DriverID            VARCHAR(10)               -- FK to Staff, added later
);

-- ---------------------------------------------------------
-- Staff / Teacher (Staff is the general employee table;
--    Teacher is a specialised employee with its own attributes)
-- ---------------------------------------------------------

CREATE TABLE Staff (
    Employee_ID       VARCHAR(10)  PRIMARY KEY,
    First_Name        VARCHAR(50)  NOT NULL,
    Middle_Name       VARCHAR(50),
    Last_Name         VARCHAR(50)  NOT NULL,
    Contact_Number    VARCHAR(15),
    Designated_Role   VARCHAR(50),
    Date_Of_Joining   DATE,
    Salary            DECIMAL(10,2)
);

CREATE TABLE Teacher (
    Employee_ID       VARCHAR(10)  PRIMARY KEY,
    First_Name        VARCHAR(50)  NOT NULL,
    Middle_Name       VARCHAR(50),
    Last_Name         VARCHAR(50)  NOT NULL,
    Date_Of_Joining   DATE,
    Phone_No          VARCHAR(15),
    Email             VARCHAR(100) UNIQUE,
    Salary            DECIMAL(10,2),
    MentorOf          VARCHAR(10)          -- FK to Section, added later
);

CREATE TABLE Teacher_Qualification (
    Qualification VARCHAR(50),
    Teacher_ID    VARCHAR(10),
    PRIMARY KEY (Qualification, Teacher_ID),
    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID)
        ON DELETE CASCADE
);

CREATE TABLE Teacher_Specialization (
    Specialization VARCHAR(50),
    Teacher_ID     VARCHAR(10),
    PRIMARY KEY (Specialization, Teacher_ID),
    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID)
        ON DELETE CASCADE
);

-- Now that Staff exists, add Transport's driver FK
ALTER TABLE Transport
    ADD CONSTRAINT fk_transport_driver
    FOREIGN KEY (DriverID) REFERENCES Staff(Employee_ID);

-- ---------------------------------------------------------
-- Section (depends on Class and Teacher)
-- ---------------------------------------------------------

CREATE TABLE Section (
    Standard      INT          NOT NULL,
    Division      VARCHAR(5)   NOT NULL,
    Room_No       VARCHAR(10),
    Class_Teacher VARCHAR(10),
    PRIMARY KEY (Standard, Division),
    FOREIGN KEY (Standard) REFERENCES Class(Standard),
    FOREIGN KEY (Class_Teacher) REFERENCES Teacher(Employee_ID)
);

-- Now that Section exists, add Teacher's MentorOf FK
-- (MentorOf stores "Standard_Division" as a composite reference;
--  simplest correct way in MySQL is two columns, so we split it)
ALTER TABLE Teacher DROP COLUMN MentorOf;
ALTER TABLE Teacher
    ADD COLUMN Mentor_Standard INT,
    ADD COLUMN Mentor_Division VARCHAR(5),
    ADD CONSTRAINT fk_teacher_mentor
    FOREIGN KEY (Mentor_Standard, Mentor_Division)
    REFERENCES Section(Standard, Division);

-- ---------------------------------------------------------
-- Student
-- ---------------------------------------------------------

CREATE TABLE Student (
    Admission_No     VARCHAR(10)  PRIMARY KEY,
    Roll_No          INT          NOT NULL,
    First_Name       VARCHAR(50)  NOT NULL,
    Middle_Name      VARCHAR(50),
    Last_Name        VARCHAR(50)  NOT NULL,
    Blood_Group      VARCHAR(5),
    Standard         INT          NOT NULL,
    Division         VARCHAR(5)   NOT NULL,
    House            VARCHAR(30),
    Vehicle_No       VARCHAR(10),
    Date_Of_Admission DATE,
    Date_Of_Birth    DATE,
    Age              INT,
    Gender           VARCHAR(10),
    Remaining_Fees   DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (Standard, Division) REFERENCES Section(Standard, Division),
    FOREIGN KEY (House) REFERENCES House(Color),
    FOREIGN KEY (Vehicle_No) REFERENCES Transport(Vehicle_ID)
);

CREATE TABLE Student_Guardian (
    Student_ID   VARCHAR(10),
    Relationship VARCHAR(20),
    First_Name   VARCHAR(50) NOT NULL,
    Middle_Name  VARCHAR(50),
    Last_Name    VARCHAR(50) NOT NULL,
    Occupation   VARCHAR(50),
    Phone_No     VARCHAR(15),
    PRIMARY KEY (Student_ID, Relationship),
    FOREIGN KEY (Student_ID) REFERENCES Student(Admission_No)
        ON DELETE CASCADE
);

-- ---------------------------------------------------------
-- Subject and teaching relationships
-- ---------------------------------------------------------

CREATE TABLE Subject (
    Subject_Code     VARCHAR(10) PRIMARY KEY,
    Subject_Name     VARCHAR(50) NOT NULL,
    Reference_Books  VARCHAR(200),
    Periods_Per_Week INT,
    Class            INT,
    FOREIGN KEY (Class) REFERENCES Class(Standard)
);

CREATE TABLE Teacher_Subject (
    Teacher_ID VARCHAR(10),
    Subject_ID VARCHAR(10),
    PRIMARY KEY (Teacher_ID, Subject_ID),
    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID),
    FOREIGN KEY (Subject_ID) REFERENCES Subject(Subject_Code)
);

CREATE TABLE Enrollments (
    Student_ID VARCHAR(10),
    Subject_ID VARCHAR(10),
    Grade      VARCHAR(5),
    PRIMARY KEY (Student_ID, Subject_ID),
    FOREIGN KEY (Student_ID) REFERENCES Student(Admission_No)
        ON DELETE CASCADE,
    FOREIGN KEY (Subject_ID) REFERENCES Subject(Subject_Code)
);

CREATE TABLE Class_Schedule (
    Teacher_ID       VARCHAR(10),
    Subject_ID       VARCHAR(10),
    Standard         INT,
    Division         VARCHAR(5),
    PRIMARY KEY (Teacher_ID, Subject_ID, Standard, Division),
    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID),
    FOREIGN KEY (Subject_ID) REFERENCES Subject(Subject_Code),
    FOREIGN KEY (Standard, Division) REFERENCES Section(Standard, Division)
);

-- ---------------------------------------------------------
-- Payments and its specialisations (Fees / Salaries)
-- ---------------------------------------------------------

CREATE TABLE Payments (
    Transaction_ID  VARCHAR(15) PRIMARY KEY,
    Date_Of_Payment DATE NOT NULL,
    Payment_Type    VARCHAR(30),
    Receiver        VARCHAR(50),
    Sender          VARCHAR(50),
    Payment_Mode    VARCHAR(20),
    Amount          DECIMAL(10,2) NOT NULL
);

CREATE TABLE Fees (
    Payment_ID VARCHAR(15),
    Student_ID VARCHAR(10),
    PRIMARY KEY (Payment_ID, Student_ID),
    FOREIGN KEY (Payment_ID) REFERENCES Payments(Transaction_ID),
    FOREIGN KEY (Student_ID) REFERENCES Student(Admission_No)
);

CREATE TABLE Staff_Salary (
    Payment_ID VARCHAR(15),
    Staff_ID   VARCHAR(10),
    PRIMARY KEY (Payment_ID, Staff_ID),
    FOREIGN KEY (Payment_ID) REFERENCES Payments(Transaction_ID),
    FOREIGN KEY (Staff_ID) REFERENCES Staff(Employee_ID)
);

CREATE TABLE Teacher_Salary (
    Payment_ID VARCHAR(15),
    Teacher_ID VARCHAR(10),
    PRIMARY KEY (Payment_ID, Teacher_ID),
    FOREIGN KEY (Payment_ID) REFERENCES Payments(Transaction_ID),
    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID)
);

