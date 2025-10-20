package com.example.smartfinance.ui.fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.smartfinance.data.local.dao.SubscriptionDao;
import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.data.model.Subscription;
import com.example.smartfinance.data.repository.SubscriptionRepository;

import java.util.List;
public class SubscriptionViewModel extends AndroidViewModel {
    private final SubscriptionRepository repository;
    private final LiveData<List<Subscription>> allActiveSubscriptions;

    public SubscriptionViewModel(@NonNull Application application) {
        super(application);
        SubscriptionDao subscriptionDao = AppDatabase.getDatabase(application).subscriptionDao();
        repository = new SubscriptionRepository(subscriptionDao);
        allActiveSubscriptions = repository.getAllActiveSubscriptions();
    }

    // Add this public getter method
    public LiveData<List<Subscription>> getAllActiveSubscriptions() {
        return allActiveSubscriptions;
    }

    public void insertSubscription(String name, double monthlyCost) {
        Subscription subscription = new Subscription(name, monthlyCost);
        repository.insert(subscription);
    }

    public void deleteSubscription(Subscription subscription) {
        repository.delete(subscription);
    }
}