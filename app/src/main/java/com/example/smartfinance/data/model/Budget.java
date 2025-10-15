package com.example.smartfinance.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class Budget {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String category;
    public double allocatedAmount;
    public double currentSpent = 0.0; // Added for manual update
    public String period; // e.g., "monthly", "weekly"
    public long startDate; // Timestamp for period start
}