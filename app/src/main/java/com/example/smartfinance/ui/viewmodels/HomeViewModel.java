package com.example.smartfinance.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartfinance.data.repository.TransactionRepository;
import com.example.smartfinance.data.model.Transaction;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final LiveData<List<Transaction>> allTransactions;
    private final LiveData<Double> totalIncome;
    private final LiveData<Double> totalExpense;
    private final MutableLiveData<Double> totalBudget;
    private final MutableLiveData<Double> savings;

    public HomeViewModel(Application application) {
        super(application);

        repository = new TransactionRepository(application);

        allTransactions = repository.getAllTransactions();
        totalIncome = repository.getTotalByType("Income");
        totalExpense = repository.getTotalByType("Expense");

        totalBudget = new MutableLiveData<>();
        totalBudget.setValue(0.00);

        savings = new MutableLiveData<>();
        savings.setValue(0.00);

        // Observe income and expense changes to update budget
        totalIncome.observeForever(income -> {
            Double expense = totalExpense.getValue();
            if (expense != null && income != null) {
                updateBudget(income, expense);
            }
        });

        totalExpense.observeForever(expense -> {
            Double income = totalIncome.getValue();
            if (income != null && expense != null) {
                updateBudget(income, expense);
            }
        });
    }

    public void insertTransaction(Transaction transaction) {
        if (repository != null) {
            repository.insertTransaction(transaction);
        }
    }

    public void updateTransaction(Transaction transaction) {
        if (repository != null) {
            repository.updateTransaction(transaction);
        }
    }

    public void deleteTransaction(Transaction transaction) {
        if (repository != null) {
            repository.deleteTransaction(transaction);
        }
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Double> getTotalExpense() {
        return totalExpense;
    }

    private void updateBudget(double income, double expense) {
        double budget = income - expense;
        totalBudget.setValue(budget);
    }

    public LiveData<Double> getTotalBudget() {
        return totalBudget;
    }

    public LiveData<Double> getSavings() {
        return savings;
    }

    public void setSavings(double savingsAmount) {
        savings.setValue(savingsAmount);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}