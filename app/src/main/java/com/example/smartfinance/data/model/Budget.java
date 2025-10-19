package com.example.smartfinance.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "budgets")
public class Budget implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String category;
    public double allocatedAmount;
    public double currentSpent = 0.0; // Added for manual update
    public String period; // e.g., "monthly", "weekly"
    public long startDate; // Timestamp for period start

    // Default constructor required for Room
    public Budget() {
    }
}