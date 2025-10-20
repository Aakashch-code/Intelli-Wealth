package com.example.smartfinance.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.data.model.Budget;

import java.util.Date;
import java.util.List;

public class BudgetViewModel extends ViewModel {
    private final AppDatabase database;

    public BudgetViewModel(AppDatabase database) {
        this.database = database;
    }

    public LiveData<List<Budget>> getAllBudgets() {
        return database.budgetDao().getAllBudgets();
    }

    public void insertBudget(Budget budget) {
        new Thread(() -> {
            long id = database.budgetDao().insertBudget(budget);
            budget.id = (int) id;
        }).start();
    }

    public void updateBudget(Budget budget) {
        new Thread(() -> {
            database.budgetDao().updateBudget(budget);
        }).start();
    }

    public void deleteBudget(Budget budget) {
        new Thread(() -> {
            database.budgetDao().deleteBudget(budget);
        }).start();
    }
}