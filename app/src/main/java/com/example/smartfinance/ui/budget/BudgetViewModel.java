package com.example.smartfinance.ui.budget;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartfinance.ui.budget.Recyclerview.Budget;

import java.util.List;

public class BudgetViewModel extends AndroidViewModel {
    private BudgetRepository repository;

    public BudgetViewModel(Application application) {
        super(application);
        repository = new BudgetRepository(application);
    }

    public LiveData<List<Budget>> getAllCategoryBudgets() {
        return repository.getAllCategoryBudgets();
    }


    public LiveData<Budget> getTotalBudget() {
        return repository.getTotalBudget();
    }

    public LiveData<Double> getTotalCategoryBudget() {
        return repository.getTotalCategoryBudget();
    }

    public LiveData<Double> getTotalCategorySpent() {
        return repository.getTotalSpent();
    }
    public void updateTotalSpent(double amount) {
        repository.updateTotalSpent(amount);
    }

    public void insertBudget(Budget budget) {
        repository.insert(budget);
    }

    public void updateBudget(Budget budget) {
        repository.update(budget);
    }

    public void deleteBudget(Budget budget) {
        repository.delete(budget);
    }

    public void updateTotalBudget(double amount) {
        repository.updateTotalBudget(amount);
    }

    public void addToSpentAmount(int budgetId, double amount) {
        repository.addToSpentAmount(budgetId, amount);
    }
    // In BudgetViewModel.java

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {
        private Application application;

        public Factory(Application application) {
            super(application);
            this.application = application;
        }

        @Override
        public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
            return (T) new BudgetViewModel(application);
        }
    }
}