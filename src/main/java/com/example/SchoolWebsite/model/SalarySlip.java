package com.example.SchoolWebsite.model;

public class SalarySlip {
    private String transactionId;
    private String dateOfPayment;
    private String paymentType;
    private String receiver;
    private String sender;
    private String paymentMode;
    private double amount;

    private String employeeId;
    private String employeeName;
    private String employeeType; // "Teacher" or "Staff"
    private String designatedRole;
    private String email;
    private String phoneNo;
    private int salaryYear;
    private int salaryMonth;
    private String monthName;

    public SalarySlip() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getDateOfPayment() { return dateOfPayment; }
    public void setDateOfPayment(String dateOfPayment) { this.dateOfPayment = dateOfPayment; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeType() { return employeeType; }
    public void setEmployeeType(String employeeType) { this.employeeType = employeeType; }

    public String getDesignatedRole() { return designatedRole; }
    public void setDesignatedRole(String designatedRole) { this.designatedRole = designatedRole; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public int getSalaryYear() { return salaryYear; }
    public void setSalaryYear(int salaryYear) { this.salaryYear = salaryYear; }

    public int getSalaryMonth() { return salaryMonth; }
    public void setSalaryMonth(int salaryMonth) { this.salaryMonth = salaryMonth; }

    public String getMonthName() { return monthName; }
    public void setMonthName(String monthName) { this.monthName = monthName; }
}

