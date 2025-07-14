
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
    public String amount;
    public String date;
    public String paymentMethod;
    public String note;

    public long timestamp;  // For sorting
}
