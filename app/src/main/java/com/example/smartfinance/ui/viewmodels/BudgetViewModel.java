package com.example.smartfinance.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.data.model.Budget;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetViewModel extends ViewModel {
    private final AppDatabase database;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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
            syncBudgetToFirebase(budget, true); // Insert
        }).start();
    }

    public void updateBudget(Budget budget) {
        new Thread(() -> {
            database.budgetDao().updateBudget(budget);
            syncBudgetToFirebase(budget, false); // Update
        }).start();
    }

    public void deleteBudget(Budget budget) {
        new Thread(() -> {
            database.budgetDao().deleteBudget(budget);
            // Sync delete to Firebase
            firestore.collection("budgets").document(budget.category).delete();
        }).start();
    }

    public void syncBudgetsWithFirebase() {
        new Thread(() -> {
            firestore.collection("budgets")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (var doc : querySnapshot.getDocuments()) {
                            Budget budget = doc.toObject(Budget.class);
                            if (budget != null) {
                                Long idLong = doc.getLong("id");
                                budget.id = idLong != null ? idLong.intValue() : 0;
                                Timestamp ts = doc.getTimestamp("startDate");
                                if (ts != null) {
                                    budget.startDate = ts.toDate().getTime();
                                }
                                Double spent = doc.getDouble("currentSpent");
                                if (spent != null) {
                                    budget.currentSpent = spent;
                                }
                                // Upsert to Room
                                Budget existing = database.budgetDao().getBudgetByCategory(budget.category);
                                if (existing != null) {
                                    database.budgetDao().updateBudget(budget);
                                } else {
                                    database.budgetDao().insertBudget(budget);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle permission denied or other errors, perhaps log
                    });
        }).start();
    }

    private void syncBudgetToFirebase(Budget budget, boolean isInsert) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", budget.id);
        data.put("category", budget.category);
        data.put("allocatedAmount", budget.allocatedAmount);
        data.put("currentSpent", budget.currentSpent);
        data.put("period", budget.period);
        data.put("startDate", new Timestamp(new Date(budget.startDate)));

        if (isInsert) {
            firestore.collection("budgets").document(budget.category).set(data);
        } else {
            firestore.collection("budgets").document(budget.category).set(data);
        }
    }
}