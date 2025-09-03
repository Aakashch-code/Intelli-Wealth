package com.example.smartfinance.ui.budget.Recyclerview;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface BudgetDao {
    // Budget operations
    @Insert
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);

    @Query("DELETE FROM budgets WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM budgets WHERE is_total_budget = 0")
    LiveData<List<Budget>> getAllCategoryBudgets();

    @Query("SELECT * FROM budgets WHERE id = :id")
    LiveData<Budget> getBudgetById(int id);

    @Query("SELECT * FROM budgets WHERE category = :category")
    LiveData<List<Budget>> getBudgetsByCategory(String category);

    // Total budget operations
    @Query("SELECT * FROM budgets WHERE is_total_budget = 1 LIMIT 1")
    LiveData<Budget> getTotalBudget();

    @Query("UPDATE budgets SET allocated_amount = :amount WHERE is_total_budget = 1")
    void updateTotalBudget(double amount);

    @Query("SELECT * FROM budgets WHERE is_total_budget = 1 LIMIT 1")
    Budget getTotalBudgetSync();

    // Category budget calculations - FIXED: Use allocated_amount for spending calculation
    @Query("SELECT SUM(allocated_amount) FROM budgets WHERE is_total_budget = 0")
    LiveData<Double> getTotalCategoryBudget();

    @Query("SELECT SUM(allocated_amount) FROM budgets WHERE is_total_budget = 0")
    LiveData<Double> getTotalCategorySpent();

    @Query("UPDATE budgets SET spent_amount = spent_amount + :amount WHERE id = :budgetId")
    void addToSpentAmount(int budgetId, double amount);

    @Query("DELETE FROM budgets")
    void deleteAllBudgets();
}