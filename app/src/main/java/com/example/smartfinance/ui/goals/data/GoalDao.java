// GoalDao.java
package com.example.smartfinance.ui.goals.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartfinance.ui.goals.model.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Delete
    void delete(Goal goal);

    @Query("DELETE FROM goals WHERE id = :goalId")
    void deleteById(int goalId);

    @Query("SELECT * FROM goals ORDER BY " +
            "CASE priority " +
            "WHEN 'HIGH' THEN 1 " +
            "WHEN 'MEDIUM' THEN 2 " +
            "WHEN 'LOW' THEN 3 " +
            "END, targetDate ASC")
    LiveData<List<Goal>> getAllGoals();

    @Query("SELECT * FROM goals WHERE id = :goalId")
    LiveData<Goal> getGoalById(int goalId);

    @Query("UPDATE goals SET savedAmount = :newAmount WHERE id = :goalId")
    void updateSavedAmount(int goalId, double newAmount);
}