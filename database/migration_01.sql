-- =========================================================
-- School Management System - Migration 01
-- Adds missing client-required attributes without data loss:
--  - Student: Address (residential address), Contact_Number
--  - Student_Guardian: Email (guardian email address)
--  - Teacher: Address (residential address)
--  - House: Captain_ID (one student House Captain per house)
-- =========================================================

USE school_dbms;

-- 1. Student table: residential address and contact number
ALTER TABLE Student
    ADD COLUMN Address VARCHAR(200),
    ADD COLUMN Contact_Number VARCHAR(15);

-- 2. Student_Guardian table: email address
ALTER TABLE Student_Guardian
    ADD COLUMN Email VARCHAR(100);

-- 3. Teacher table: residential address
ALTER TABLE Teacher
    ADD COLUMN Address VARCHAR(200);

-- 4. House table: Captain_ID referencing a student in that house (one per house)
ALTER TABLE House
    ADD COLUMN Captain_ID VARCHAR(10) UNIQUE;

ALTER TABLE House
    ADD CONSTRAINT fk_house_captain
    FOREIGN KEY (Captain_ID) REFERENCES Student(Admission_No)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

