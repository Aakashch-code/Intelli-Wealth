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
    public double currentSpent = 0.0;
    public String period;
    public long startDate;

    public Budget() {
    }
}