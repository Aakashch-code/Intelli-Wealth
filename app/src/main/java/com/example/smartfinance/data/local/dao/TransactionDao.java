package com.example.smartfinance.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.smartfinance.data.model.MonthlyTotal;
import com.example.smartfinance.data.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT strftime('%Y-%m', datetime(timestamp / 1000, 'unixepoch')) AS month, SUM(amount) AS total " +
            "FROM transactions " +
            "WHERE type = :type " +
            "GROUP BY month " +
            "ORDER BY month ASC")
    LiveData<List<MonthlyTotal>> getMonthlyTotals(String type);
    @Query("SELECT * FROM transactions WHERE category = :category")
    List<Transaction> getTransactionsByCategory(String category);

    // For spent sum, add:
    @Query("SELECT SUM(ABS(amount)) FROM transactions WHERE category = :category AND amount < 0 AND timestamp >= :startTime")
    double getSpentForCategoryInPeriod(String category, long startTime);
    @Insert
    long insertTransaction(Transaction transaction);

    @Update
    void updateTransaction(Transaction transaction);

    @Delete
    void deleteTransaction(Transaction transaction);

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Income'")
    LiveData<Float> getTotalIncome();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Expense'")
    LiveData<Float> getTotalExpense();

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    List<Transaction> getAllTransactionsSync();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    LiveData<Double> getTotalByType(String type);

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getTransactionsByType(String type);

    // New methods for Firestore synchronization - CORRECTED VERSION
    @Query("SELECT * FROM transactions WHERE firestoreId IS NULL")
    List<Transaction> getUnsyncedTransactions();

    @Query("SELECT * FROM transactions WHERE firestoreId = :firestoreId")
    Transaction getTransactionByFirestoreId(String firestoreId);

    @Query("UPDATE transactions SET firestoreId = :firestoreId WHERE id = :localId")
    void updateFirestoreId(int localId, String firestoreId);

    @Query("DELETE FROM transactions WHERE firestoreId IS NULL")
    void deleteUnsyncedTransactions();

    @Query("SELECT COUNT(*) FROM transactions WHERE firestoreId = :firestoreId")
    int countByFirestoreId(String firestoreId);

    @Query("DELETE FROM transactions")
    void deleteAllTransactions();

    @Insert
    void insertAll(List<Transaction> transactions);
}