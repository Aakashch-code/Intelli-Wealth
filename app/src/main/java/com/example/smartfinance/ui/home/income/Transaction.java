package com.example.smartfinance.ui.home.income;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String type;
    public String category;
    public double amount;
    public String date;
    public String paymentMethod;
    public String note;
    public long timestamp;

    public Transaction(@NonNull String type, String category, double amount, String date,
                       String paymentMethod, String note, long timestamp) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() { return id; }
    @NonNull public String getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getNote() { return note; }
    public long getTimestamp() { return timestamp; }
}