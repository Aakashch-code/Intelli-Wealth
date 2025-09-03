package com.example.smartfinance.ui.budget;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartfinance.ui.budget.Recyclerview.Budget;
import com.example.smartfinance.ui.budget.Recyclerview.BudgetDao;
import com.example.smartfinance.ui.budget.sheets.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetRepository {
    private BudgetDao budgetDao;
    private ExecutorService executorService;

    public BudgetRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        budgetDao = database.budgetDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Budget>> getAllCategoryBudgets() {
        return budgetDao.getAllCategoryBudgets();
    }
    public void updateTotalSpent(double amount) {
        executorService.execute(() -> budgetDao.getTotalCategorySpent());
    }

    public LiveData<Budget> getTotalBudget() {
        return budgetDao.getTotalBudget();
    }

    public LiveData<Double> getTotalCategoryBudget() {
        return budgetDao.getTotalCategoryBudget();
    }

    public LiveData<Double> getTotalSpent() {
        return budgetDao.getTotalCategorySpent();
    }

    public void insert(Budget budget) {
        executorService.execute(() -> budgetDao.insert(budget));
    }

    public void update(Budget budget) {
        executorService.execute(() -> budgetDao.update(budget));
    }

    public void delete(Budget budget) {
        executorService.execute(() -> budgetDao.delete(budget));
    }

    public void updateTotalBudget(double amount) {
        executorService.execute(() -> {
            Budget totalBudget = budgetDao.getTotalBudgetSync();
            if (totalBudget != null) {
                totalBudget.setAllocatedAmount(amount);
                budgetDao.update(totalBudget);
            } else {
                Budget newTotalBudget = new Budget(amount, true);
                budgetDao.insert(newTotalBudget);
            }
        });
    }

    public void addToSpentAmount(int budgetId, double amount) {
        executorService.execute(() -> budgetDao.addToSpentAmount(budgetId, amount));
    }
}