// BudgetViewModel.java (in com.example.smartfinance.ui.budget package)
package com.example.smartfinance.ui.budget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.example.smartfinance.ui.budget.Recyclerview.Budget;
import com.example.smartfinance.ui.budget.Recyclerview.BudgetDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetViewModel extends ViewModel {

    private BudgetDao budgetDao;
    private ExecutorService executorService;

    public BudgetViewModel(BudgetDao budgetDao) {
        this.budgetDao = budgetDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    // Change BudgetEntity to Budget
    public void insertBudget(Budget budget) {
        executorService.execute(() -> budgetDao.insert(budget));
    }

    // Change BudgetEntity to Budget
    public void updateBudget(Budget budget) {
        executorService.execute(() -> budgetDao.update(budget));
    }

    // Change BudgetEntity to Budget
    public void deleteBudget(Budget budget) {
        executorService.execute(() -> budgetDao.delete(budget));
    }

    // Change BudgetEntity to Budget
    public LiveData<List<Budget>> getAllBudgets() {
        return budgetDao.getAllBudgets();
    }

    public LiveData<Double> getTotalBudget() {
        return budgetDao.getTotalBudget();
    }

    // ViewModel Factory
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private BudgetDao budgetDao;

        public Factory(BudgetDao budgetDao) {
            this.budgetDao = budgetDao;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new BudgetViewModel(budgetDao);
        }
    }
}