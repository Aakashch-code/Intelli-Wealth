// BudgetDao.java (in com.example.smartfinance.ui.budget.Recyclerview package)
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

    @Insert
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);

    @Query("DELETE FROM budgets WHERE id = :id")
    void deleteById(int id);

    // Remove ORDER BY createdAt since it doesn't exist in your Budget class
    @Query("SELECT * FROM budgets")
    LiveData<List<Budget>> getAllBudgets();

    @Query("SELECT * FROM budgets WHERE id = :id")
    LiveData<Budget> getBudgetById(int id);

    // Use the correct column name "category" (mapped to text_category field)
    @Query("SELECT * FROM budgets WHERE category = :category")
    LiveData<List<Budget>> getBudgetsByCategory(String category);

    @Query("SELECT SUM(amount) FROM budgets")
    LiveData<Double> getTotalBudget();

    @Query("DELETE FROM budgets")
    void deleteAllBudgets();
}