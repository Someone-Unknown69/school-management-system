package com.example.SchoolWebsite.model;

public class Student {
    private String admissionNo;
    private int rollNo;
    private String firstName;
    private String middleName;
    private String lastName;
    private String bloodGroup;
    private String standard;
    private String division;
    private String house;
    private String houseName;
    private String vehicleNo;
    private String routeName;
    private String dateOfAdmission;
    private String dateOfBirth;
    private int age;
    private String gender;
    private double remainingFees;
    private String address;
    private String contactNumber;
    private int houseWins;
    private String roomNo;
    private String classTeacherName;
    private String grade;

    // Constructors
    public Student() {}

    // Simple constructor (for student directory listing)
    public Student(String admissionNo, int rollNo, String firstName, String standard, String house, double remainingFees) {
        this.admissionNo = admissionNo;
        this.rollNo = rollNo;
        this.firstName = firstName;
        this.standard = standard;
        this.house = house;
        this.remainingFees = remainingFees;
    }

    // Getters and Setters
    public String getAdmissionNo() { return admissionNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }

    public int getRollNo() { return rollNo; }
    public void setRollNo(int rollNo) { this.rollNo = rollNo; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getStandard() { return standard; }
    public void setStandard(String standard) { this.standard = standard; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }

    public String getHouseName() { return houseName; }
    public void setHouseName(String houseName) { this.houseName = houseName; }

    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getDateOfAdmission() { return dateOfAdmission; }
    public void setDateOfAdmission(String dateOfAdmission) { this.dateOfAdmission = dateOfAdmission; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getRemainingFees() { return remainingFees; }
    public void setRemainingFees(double remainingFees) { this.remainingFees = remainingFees; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getHouseWins() { return houseWins; }
    public void setHouseWins(int houseWins) { this.houseWins = houseWins; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public String getClassTeacherName() { return classTeacherName; }
    public void setClassTeacherName(String classTeacherName) { this.classTeacherName = classTeacherName; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    // Helper: full name
    public String getFullName() {
        StringBuilder sb = new StringBuilder(firstName);
        if (middleName != null && !middleName.isEmpty()) sb.append(" ").append(middleName);
        if (lastName != null && !lastName.isEmpty()) sb.append(" ").append(lastName);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Student{admissionNo='" + admissionNo + "', firstName='" + firstName + "', standard='" + standard + "'}";
    }
}
