
package com.example.smartfinance.ui.viewmodels;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartfinance.data.repository.GoalRepository;
import com.example.smartfinance.data.model.Goal;

import java.util.List;

public class GoalsViewModel extends AndroidViewModel {
    private GoalRepository repository;
    private LiveData<List<Goal>> allGoals;

    public GoalsViewModel(@NonNull Application application) {
        super(application);
        repository = new GoalRepository(application);
        allGoals = repository.getAllGoals();
    }

    public void insert(Goal goal) {
        repository.insert(goal);
    }

    public void update(Goal goal) {
        repository.update(goal);
    }

    public void delete(Goal goal) {
        repository.delete(goal);
    }

    public void updateSavedAmount(int goalId, double newAmount) {
        repository.updateSavedAmount(goalId, newAmount);
    }

    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }
}