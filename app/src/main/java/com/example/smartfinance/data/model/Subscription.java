// app/src/main/java/com/example/smartfinance/data/Subscription.java
package com.example.smartfinance.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "subscriptions")
public class Subscription {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private double monthlyCost;
    private String billingCycle = "Monthly";
    private Date nextBillingDate = new Date();
    private boolean isActive = true;

    // Constructors
    public Subscription() {}

    public Subscription(String name, double monthlyCost) {
        this.name = name;
        this.monthlyCost = monthlyCost;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getMonthlyCost() { return monthlyCost; }
    public void setMonthlyCost(double monthlyCost) { this.monthlyCost = monthlyCost; }

    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }

    public Date getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(Date nextBillingDate) { this.nextBillingDate = nextBillingDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}