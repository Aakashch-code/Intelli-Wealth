
package com.example.smartfinance.ui.home.income;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public int id;


    @NonNull
    public String type; // "income" or "expense"

    public String category;
    public double amount;
    public String date;
    public String paymentMethod;
    public String note;


    public long timestamp;  // For sorting
    public Transaction(String type, double amount, String note, long timestamp) {
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.timestamp = timestamp;
    }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
    public long getTimestamp() { return timestamp; }
}
