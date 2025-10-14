// GoalRepository.java
package com.example.smartfinance.data.repository;

import android.app.Application; // Make sure this import is present
import androidx.lifecycle.LiveData;

// 1. Import the main AppDatabase from its correct package
import com.example.smartfinance.data.local.dao.GoalDao;
import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.data.model.Goal;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoalRepository {
    private final GoalDao goalDao;
    private final LiveData<List<Goal>> allGoals;
    private final ExecutorService executorService;

    public GoalRepository(Application application) {
        // 2. Use the 'getDatabase' static method from your main AppDatabase
        AppDatabase database = AppDatabase.getDatabase(application);
        goalDao = database.goalDao();
        allGoals = goalDao.getAllGoals();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Goal goal) {
        executorService.execute(() -> goalDao.insert(goal));
    }

    public void update(Goal goal) {
        executorService.execute(() -> goalDao.update(goal));
    }

    public void delete(Goal goal) {
        executorService.execute(() -> goalDao.delete(goal));
    }

    public void updateSavedAmount(int goalId, double newAmount) {
        executorService.execute(() -> goalDao.updateSavedAmount(goalId, newAmount));
    }

    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }
}