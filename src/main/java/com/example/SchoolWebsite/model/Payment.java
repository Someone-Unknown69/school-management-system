package com.example.SchoolWebsite.model;

public class Payment {
    private String transactionId;
    private String dateOfPayment;
    private String paymentType;
    private String receiver;
    private String sender;
    private String paymentMode;
    private double amount;
    private String relatedId;
    private String relatedRole;
    private String subType;

    public Payment() {}

    public Payment(String transactionId, String dateOfPayment, String paymentType, String receiver, String sender, String paymentMode, double amount) {
        this.transactionId = transactionId;
        this.dateOfPayment = dateOfPayment;
        this.paymentType = paymentType;
        this.receiver = receiver;
        this.sender = sender;
        this.paymentMode = paymentMode;
        this.amount = amount;
    }

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

    public String getRelatedId() { return relatedId; }
    public void setRelatedId(String relatedId) { this.relatedId = relatedId; }

    public String getRelatedRole() { return relatedRole; }
    public void setRelatedRole(String relatedRole) { this.relatedRole = relatedRole; }

    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }
}
