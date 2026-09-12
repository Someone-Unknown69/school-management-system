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
    Color               VARCHAR(30)       PRIMARY KEY,
    House_Name          VARCHAR(50)       NOT NULL,
    No_Of_Wins          INT               DEFAULT 0
);

CREATE TABLE Class (
    Standard            VARCHAR(5)        PRIMARY KEY       -- 1 to 12
);

CREATE TABLE Transport (
    Vehicle_ID          VARCHAR(10)       PRIMARY KEY,
    Route_Name          VARCHAR(50)       NOT NULL,
    Registration_Number VARCHAR(20)       UNIQUE NOT NULL,
    Capacity            INT               NOT NULL,
    DriverID            VARCHAR(10)       -- FK to Staff, added later
);

-- ---------------------------------------------------------
-- Staff / Teacher (Staff is the general employee table;
--    Teacher is a specialised employee with its own attributes)
-- ---------------------------------------------------------

CREATE TABLE Staff (
    Employee_ID         VARCHAR(10)       PRIMARY KEY,
    First_Name          VARCHAR(50)       NOT NULL,
    Middle_Name         VARCHAR(50),
    Last_Name           VARCHAR(50),
    Contact_Number      VARCHAR(15)       NOT NULL,
    Designated_Role     VARCHAR(50)       NOT NULL,
    Date_Of_Joining     DATE              NOT NULL,
    Salary              DECIMAL(10,2)     NOT NULL        CHECK (Salary > 0),
);

CREATE TABLE Teacher (
    Employee_ID         VARCHAR(10)       PRIMARY KEY,
    First_Name          VARCHAR(50)       NOT NULL,
    Middle_Name         VARCHAR(50),
    Last_Name           VARCHAR(50),
    Date_Of_Joining     DATE              NOT NULL,
    Phone_No            VARCHAR(15)       NOT NULL,
    Email               VARCHAR(100)      UNIQUE NOT NULL,
    Salary              DECIMAL(10,2)     NOT NULL        CHECK (Salary > 0),
    MentorOf            VARCHAR(30)       -- FK to House, can be null (there can be a teacher not alloted to a house)

    FOREIGN KEY (MentorOf) REFERENCES House(Color)
        ON DELETE SET NULL,
        ON UPDATE CASCADE
);

-- No need to put NOT NULL in the below two tables as they only consist of PKs
CREATE TABLE Teacher_Qualification (
    Qualification       VARCHAR(50),
    Teacher_ID          VARCHAR(10),

    PRIMARY KEY (Qualification, Teacher_ID),
    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID)
        ON DELETE CASCADE,
        ON UPDATE CASCADE
);

CREATE TABLE Teacher_Specialization (
    Specialization      VARCHAR(50),
    Teacher_ID          VARCHAR(10),

    PRIMARY KEY (Specialization, Teacher_ID),
    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID)
        ON DELETE CASCADE,
        ON UPDATE CASCADE
);

-- Now that Staff exists, add Transport's driver FK
ALTER TABLE Transport
    ADD CONSTRAINT fk_transport_driver
    FOREIGN KEY (DriverID) REFERENCES Staff(Employee_ID);
        ON DELETE SET NULL;
        ON UPDATE CASCADE;

-- ---------------------------------------------------------
-- Section (depends on Class and Teacher)
-- ---------------------------------------------------------

-- considering if a teacher leaves the school mid term and a class could be left unappointed for rest of the term
-- It's sensible to keep class teacher attribute nullable
CREATE TABLE Section (
    Standard            VARCHAR(5),       NOT NULL,
    Division            VARCHAR(5),       NOT NULL,
    Room_No             VARCHAR(10)       NOT NULL,
    Class_Teacher       VARCHAR(10),

    PRIMARY KEY (Standard, Division),
    FOREIGN KEY (Standard) REFERENCES Class(Standard),
        ON DELETE CASCADE,
        ON UPDATE CASCADE,
    FOREIGN KEY (Class_Teacher) REFERENCES Teacher(Employee_ID)
        ON DELETE SET NULL,
        ON UPDATE CASCADE,
);

-- ---------------------------------------------------------
-- Student
-- ---------------------------------------------------------

CREATE TABLE Student (
    Admission_No        VARCHAR(10)       PRIMARY KEY,
    Roll_No             INT               NOT NULL,
    First_Name          VARCHAR(50)       NOT NULL,
    Middle_Name         VARCHAR(50),
    Last_Name           VARCHAR(50),
    Blood_Group         VARCHAR(5)        NOT NULL,
    Standard            VARCHAR(5)        NOT NULL,
    Division            VARCHAR(5)        NOT NULL,
    House               VARCHAR(30)       NOT NULL,
    Vehicle_No          VARCHAR(10),
    Date_Of_Admission   DATE              NOT NULL,
    Date_Of_Birth       DATE              NOT NULL,
    Age                 INT               NOT NULL,
    Gender              VARCHAR(20),      NOT NULL,
    Remaining_Fees      DECIMAL(10,2)     DEFAULT 0,

    FOREIGN KEY (Standard, Division) REFERENCES Section(Standard, Division),
        ON UPDATE CASCADE,
    FOREIGN KEY (House) REFERENCES House(Color),
        ON UPDATE CASCADE,
    FOREIGN KEY (Vehicle_No) REFERENCES Transport(Vehicle_ID)
        ON DELETE SET NULL,
        ON UPDATE CASCADE,
);

CREATE TABLE Student_Guardian (
    Student_ID          VARCHAR(10)       NOT NULL,
    Relationship        VARCHAR(20)       NOT NULL,
    First_Name          VARCHAR(50)       NOT NULL,
    Middle_Name         VARCHAR(50),
    Last_Name           VARCHAR(50),
    Occupation          VARCHAR(50)       NOT NULL,
    Phone_No            VARCHAR(15)       NOT NULL,

    PRIMARY KEY (Student_ID, Relationship),
    FOREIGN KEY (Student_ID) REFERENCES Student(Admission_No)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- ---------------------------------------------------------
-- Subject and teaching relationships
-- ---------------------------------------------------------

CREATE TABLE Subject (
    Subject_Code       VARCHAR(10)      PRIMARY KEY,
    Subject_Name       VARCHAR(50)      NOT NULL,
    Periods_Per_Week   INT              NOT NULL         CHECK (Periods_Per_Week > 0),
    Class              VARCHAR(5),

    FOREIGN KEY (Class) REFERENCES Class(Standard)
        ON UPDATE CASCADE
);

CREATE TABLE Reference_Books (
    Book_name          VARCHAR(100)     NOT NULL,
    Subject_Code       VARCHAR(10)      NOT NULL,

    PRIMARY KEY (Book_name, Subject_Code),
    FOREIGN KEY (Subject_Code) REFERENCES Subject(Subject_Code)
        ON DELETE CASCADE,
        ON UPDATE CASCADE,
);

CREATE TABLE Teacher_Subject (
    Teacher_ID         VARCHAR(10)      NOT NULL,
    Subject_ID         VARCHAR(10)      NOT NULL,

    PRIMARY KEY (Teacher_ID, Subject_ID),

    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID),
        ON DELETE CASCADE,
        ON UPDATE CASCADE,

    FOREIGN KEY (Subject_ID) REFERENCES Subject(Subject_Code)
        ON DELETE CASCADE,
        ON UPDATE CASCADE,
);

CREATE TABLE Enrollments (
    Student_ID        VARCHAR(10)       NOT NULL,
    Subject_ID        VARCHAR(10)       NOT NULL,
    Grade             VARCHAR(5),

    PRIMARY KEY (Student_ID, Subject_ID),

    FOREIGN KEY (Student_ID) REFERENCES Student(Admission_No)
        ON DELETE CASCADE,
        ON UPDATE CASCADE,

    FOREIGN KEY (Subject_ID) REFERENCES Subject(Subject_Code)
        ON DELETE CASCADE,
        ON UPDATE CASCADE,
);

CREATE TABLE Class_Schedule (
    Teacher_ID        VARCHAR(10)       NOT NULL,
    Subject_ID        VARCHAR(10)       NOT NULL,
    Standard          VARCHAR(5)        NOT NULL,
    Division          VARCHAR(5)        NOT NULL,

    PRIMARY KEY (Teacher_ID, Subject_ID, Standard, Division),

    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID),
        ON DELETE CASCADE,
        ON UPDATE CASCADE,

    FOREIGN KEY (Subject_ID) REFERENCES Subject(Subject_Code),
        ON DELETE CASCADE,
        ON UPDATE CASCADE,

    FOREIGN KEY (Standard, Division) REFERENCES Section(Standard, Division)
        ON UPDATE CASCADE,
);

-- ---------------------------------------------------------
-- Payments and its specialisations (Fees / Salaries)
-- ---------------------------------------------------------

CREATE TABLE Payments (
    Transaction_ID    VARCHAR(15)       PRIMARY KEY,
    Date_Of_Payment   DATETIME          NOT NULL,
    Payment_Type      VARCHAR(30)       NOT NULL,
    Receiver          VARCHAR(50)       NOT NULL,
    Sender            VARCHAR(50)       NOT NULL,
    Payment_Mode      VARCHAR(20)       NOT NULL,
    Amount            DECIMAL(10,2)     NOT NULL        CHECK (Amount > 0)
);

CREATE TABLE Fees (
    Payment_ID        VARCHAR(15),
    Student_ID        VARCHAR(10),

    PRIMARY KEY (Payment_ID, Student_ID),

    FOREIGN KEY (Payment_ID) REFERENCES Payments(Transaction_ID),
        ON DELETE CASCADE,
        ON UPDATE CASCADE,

    FOREIGN KEY (Student_ID) REFERENCES Student(Admission_No)
        ON DELETE CASCADE,
        ON UPDATE CASCADE,

);

CREATE TABLE Staff_Salary (
    Payment_ID        VARCHAR(15),
    Staff_ID          VARCHAR(10),

    PRIMARY KEY (Payment_ID, Staff_ID),

    FOREIGN KEY (Payment_ID) REFERENCES Payments(Transaction_ID),
        ON DELETE CASCADE,
        ON UPDATE CASCADE,

    FOREIGN KEY (Staff_ID) REFERENCES Staff(Employee_ID)
        ON DELETE CASCADE,
        ON UPDATE CASCADE,
);

CREATE TABLE Teacher_Salary (
    Payment_ID        VARCHAR(15),
    Teacher_ID        VARCHAR(10),

    PRIMARY KEY (Payment_ID, Teacher_ID),

    FOREIGN KEY (Payment_ID) REFERENCES Payments(Transaction_ID),
        ON DELETE CASCADE,
        ON UPDATE CASCADE,

    FOREIGN KEY (Teacher_ID) REFERENCES Teacher(Employee_ID)
        ON DELETE CASCADE,
        ON UPDATE CASCADE,
);

