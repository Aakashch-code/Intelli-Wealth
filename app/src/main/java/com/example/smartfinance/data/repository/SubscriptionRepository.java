// app/src/main/java/com/example/smartfinance/repository/SubscriptionRepository.java
package com.example.smartfinance.data.repository;

import androidx.lifecycle.LiveData;


import com.example.smartfinance.data.local.dao.SubscriptionDao;
import com.example.smartfinance.data.model.Subscription;

import java.util.List;

public class SubscriptionRepository {
    private final SubscriptionDao subscriptionDao;
    private final LiveData<List<Subscription>> allActiveSubscriptions;

    public SubscriptionRepository(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
        allActiveSubscriptions = subscriptionDao.getActiveSubscriptions();
    }

    public LiveData<List<Subscription>> getAllActiveSubscriptions() {
        return allActiveSubscriptions;
    }

    public void insert(Subscription subscription) {
        new Thread(() -> subscriptionDao.insert(subscription)).start();
    }

    public void update(Subscription subscription) {
        new Thread(() -> subscriptionDao.update(subscription)).start();
    }

    public void delete(Subscription subscription) {
        new Thread(() -> subscriptionDao.delete(subscription)).start();
    }

    public void deleteById(long id) {
        new Thread(() -> subscriptionDao.deleteById(id)).start();
    }
}