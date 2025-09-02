// Budget.java
package com.example.smartfinance.ui.budget.Recyclerview;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "budgets")
public class Budget {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "category")
    private String text_category;

    @ColumnInfo(name = "amount")
    private double amount;

    // Your existing constructor
    public Budget(String categoryName, double amount) {
        this.text_category = categoryName;
        this.amount = amount;
    }

    // Default constructor (required by Room)
    public Budget() {
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Fix: Use proper naming convention for the category field
    public String getText_category() {
        return text_category;
    }

    public void setText_category(String text_category) {
        this.text_category = text_category;
    }

    public String getCategoryName() {
        return text_category;
    }

    public void setCategoryName(String categoryName) {
        this.text_category = categoryName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}