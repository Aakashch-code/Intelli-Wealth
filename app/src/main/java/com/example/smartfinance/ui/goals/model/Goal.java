// Goal.java
package com.example.smartfinance.ui.goals.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "goals")
public class Goal {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String goalName;
    private double targetAmount;
    private double savedAmount;
    private Date targetDate;
    private String priority; // HIGH, MEDIUM, LOW

    public Goal(String goalName, double targetAmount, double savedAmount, Date targetDate, String priority) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.targetDate = targetDate;
        this.priority = priority;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }

    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }

    public double getSavedAmount() { return savedAmount; }
    public void setSavedAmount(double savedAmount) { this.savedAmount = savedAmount; }

    public Date getTargetDate() { return targetDate; }
    public void setTargetDate(Date targetDate) { this.targetDate = targetDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    // Helper method to calculate progress percentage
    public int getProgressPercentage() {
        if (targetAmount <= 0) return 0;
        return (int) ((savedAmount / targetAmount) * 100);
    }
}