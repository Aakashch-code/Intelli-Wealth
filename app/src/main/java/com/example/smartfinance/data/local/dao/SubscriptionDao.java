
package com.example.smartfinance.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartfinance.data.model.Subscription;

import java.util.List;

@Dao
public interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE isActive = 1")
    LiveData<List<Subscription>> getActiveSubscriptions();

    @Insert
    void insert(Subscription subscription);

    @Update
    void update(Subscription subscription);

    @Delete
    void delete(Subscription subscription);

    @Query("DELETE FROM subscriptions WHERE id = :id")
    void deleteById(long id);
}