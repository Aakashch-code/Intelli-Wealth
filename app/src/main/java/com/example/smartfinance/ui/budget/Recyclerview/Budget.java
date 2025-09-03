// Entity class
package com.example.smartfinance.ui.budget.Recyclerview;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

@Entity(tableName = "budgets")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "allocated_amount")
    private double allocatedAmount;

    @ColumnInfo(name = "spent_amount")
    private double spentAmount;

    @ColumnInfo(name = "icon_res_id")
    private int iconResId;

    @ColumnInfo(name = "start_date")
    private String startDate;

    @ColumnInfo(name = "end_date")
    private String endDate;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "is_total_budget")
    private boolean isTotalBudget;

    // Primary constructor for Room (must have)
    public Budget() {
    }

    // Constructor for total budget
    @Ignore
    public Budget(double allocatedAmount, boolean isTotalBudget) {
        this.category = "Total Budget";
        this.allocatedAmount = allocatedAmount;
        this.spentAmount = 0.0;
        this.iconResId = 0;
        this.isTotalBudget = isTotalBudget;
    }

    // Constructor for category budget
    @Ignore
    public Budget(String category, double allocatedAmount, String startDate, String endDate, String description) {
        this.category = category;
        this.allocatedAmount = allocatedAmount;
        this.spentAmount = 0.0;
        this.iconResId = 0;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.isTotalBudget = false;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(double allocatedAmount) { this.allocatedAmount = allocatedAmount; }

    public double getSpentAmount() { return spentAmount; }
    public void setSpentAmount(double spentAmount) { this.spentAmount = spentAmount; }

    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isTotalBudget() { return isTotalBudget; }
    public void setTotalBudget(boolean totalBudget) { isTotalBudget = totalBudget; }
}