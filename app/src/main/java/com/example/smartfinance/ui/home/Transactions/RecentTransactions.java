package com.example.smartfinance.ui.home.Transactions;

import java.util.Date;
import java.util.UUID;

public class RecentTransactions {
    private String transactionId;
    private double amount;
    private String type;
    private String note;
    private long timestamp;
    private Date date;
    private String category;
    private String paymentMethod;

    public RecentTransactions(double amount, String type, String note, long timestamp,
                              String category, String paymentMethod) {
        this.transactionId = generateTransactionId();
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.timestamp = timestamp;
        this.date = new Date(timestamp);
        this.category = category;
        this.paymentMethod = paymentMethod;
    }

    // Generate a unique transaction ID
    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    // Getters and setters for all fields
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        this.date = new Date(timestamp); // Update date when timestamp changes
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        this.timestamp = date.getTime(); // Update timestamp when date changes
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "RecentTransactions{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", note='" + note + '\'' +
                ", timestamp=" + timestamp +
                ", date=" + date +
                ", category='" + category + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}