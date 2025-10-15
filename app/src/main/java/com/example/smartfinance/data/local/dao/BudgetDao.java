package com.example.smartfinance.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartfinance.data.model.Budget;

import java.util.List;

@Dao
public interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY category ASC")
    LiveData<List<Budget>> getAllBudgets();

    @Insert
    long insertBudget(Budget budget);

    @Update
    void updateBudget(Budget budget);

    @Delete
    void deleteBudget(Budget budget);

    @Query("SELECT * FROM budgets WHERE id = :id")
    Budget getBudgetById(int id);

    @Query("SELECT * FROM budgets WHERE category = :category LIMIT 1")
    Budget getBudgetByCategory(String category);
}