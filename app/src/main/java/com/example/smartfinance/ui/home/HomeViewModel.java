package com.example.smartfinance.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartfinance.ui.home.data.TransactionRepository;
import com.example.smartfinance.ui.home.model.Transaction;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final LiveData<List<Transaction>> allTransactions;
    private final LiveData<Double> totalIncome;
    private final LiveData<Double> totalExpense;
    private final MutableLiveData<Double> totalBudget;
    private final MutableLiveData<Double> savings;
    private final MutableLiveData<String> syncStatus = new MutableLiveData<>("Initializing...");

    public HomeViewModel(Application application) {
        super(application);

        // Initialize repository with safe approach
        try {
            repository = new TransactionRepository(application);
            if (repository.isFirebaseInitialized()) {
                syncStatus.setValue("Firebase ready");
            } else {
                syncStatus.setValue("Firebase not initialized");
            }
        } catch (Exception e) {
            syncStatus.setValue("Error: " + e.getMessage());
            throw new RuntimeException("Firebase not initialized. Please check Firebase setup.", e);
        }

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

    public LiveData<String> getSyncStatus() {
        return syncStatus;
    }

    public void insertTransaction(Transaction transaction) {
        if (repository != null) {
            repository.insertTransaction(transaction);
            syncStatus.setValue("Transaction saved locally, syncing to Firestore...");
            repository.forceSync(); // Ensure sync is triggered
        }
    }

    public void updateTransaction(Transaction transaction) {
        if (repository != null) {
            repository.updateTransaction(transaction);
            syncStatus.setValue("Transaction updated locally, syncing to Firestore...");
            repository.forceSync();
        }
    }

    public void deleteTransaction(Transaction transaction) {
        if (repository != null) {
            repository.deleteTransaction(transaction);
            syncStatus.setValue("Transaction deleted locally, syncing to Firestore...");
            repository.forceSync();
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

    public void forceSync() {
        if (repository != null) {
            repository.forceSync();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (repository != null) {
            repository.shutdown();
        }
    }
}