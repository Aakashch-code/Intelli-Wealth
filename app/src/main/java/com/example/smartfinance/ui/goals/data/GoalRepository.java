// GoalRepository.java
package com.example.smartfinance.ui.goals.data;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.smartfinance.ui.goals.model.Goal;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoalRepository {
    private GoalDao goalDao;
    private LiveData<List<Goal>> allGoals;
    private ExecutorService executorService;

    public GoalRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
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