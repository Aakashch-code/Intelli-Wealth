package com.example.smartfinance.ui.home.Transactions;


public class recentTransactions {

    double amount;
    String type;
    String note;
    long timestamp;
    public recentTransactions(double amount, String type, String note, long timestamp) {
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.timestamp = timestamp;
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
    }
}
