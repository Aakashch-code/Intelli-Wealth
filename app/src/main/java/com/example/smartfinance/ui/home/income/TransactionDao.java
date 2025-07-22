package com.example.smartfinance.ui.home.income;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insertTransaction(Transaction transaction);

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    LiveData<Double> getTotalByType(String type);

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getTransactionsByType(String type);

}
