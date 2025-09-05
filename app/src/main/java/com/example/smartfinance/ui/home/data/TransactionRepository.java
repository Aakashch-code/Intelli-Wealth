package com.example.smartfinance.ui.home.data;


// package: com.example.smartfinance.repository

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartfinance.ui.home.model.Transaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {

    private final TransactionDao transactionDao;
    private final LiveData<List<Transaction>> allTransactions;
    private final ExecutorService executorService;

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        transactionDao = db.transactionDao();
        allTransactions = transactionDao.getAllTransactions();
        executorService = Executors.newSingleThreadExecutor();
    }
    public LiveData<Double> getTotalByType(String type) {
        return transactionDao.getTotalByType(type);
    }


    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public void insertTransaction(Transaction transaction) {
        executorService.execute(() -> transactionDao.insertTransaction(transaction));
    }

    // Optional: Shutdown executor when needed
    public void shutDown() {
        executorService.shutdown();
    }

    public void insert(Transaction transaction) {
    }
}
