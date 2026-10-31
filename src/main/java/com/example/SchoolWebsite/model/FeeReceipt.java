package com.example.SchoolWebsite.model;

public class FeeReceipt {
    private String transactionId;
    private String dateOfPayment;
    private String paymentType;
    private String receiver;
    private String sender;
    private String paymentMode;
    private double amount;

    private String studentId;
    private int rollNo;
    private String studentName;
    private String standard;
    private String division;
    private double remainingFees;

    public FeeReceipt() {}

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

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public int getRollNo() { return rollNo; }
    public void setRollNo(int rollNo) { this.rollNo = rollNo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStandard() { return standard; }
    public void setStandard(String standard) { this.standard = standard; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public double getRemainingFees() { return remainingFees; }
    public void setRemainingFees(double remainingFees) { this.remainingFees = remainingFees; }
}

