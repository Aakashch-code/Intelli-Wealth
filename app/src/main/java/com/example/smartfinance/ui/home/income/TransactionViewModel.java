package com.example.smartfinance.ui.home.income;

// package: com.example.smartfinance.viewmodel

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {

    private TransactionRepository repository;
    private LiveData<List<Transaction>> allTransactions;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        allTransactions = repository.getAllTransactions();
    }

    public void insert(Transaction transaction ) {
        repository.insertTransaction(transaction);
    }


    public LiveData<Double> getTotalByType(String type) {
        return repository.getTotalByType(type);
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

}
