package com.example.smartfinance.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.smartfinance.data.local.dao.SubscriptionDao;
import com.example.smartfinance.data.model.Subscription;
import com.example.smartfinance.utils.DateConverter;

@Database(entities = {Subscription.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class SmartFinanceDatabase extends RoomDatabase {
    public abstract SubscriptionDao subscriptionDao();

    private static volatile SmartFinanceDatabase INSTANCE;

    public static SmartFinanceDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SmartFinanceDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SmartFinanceDatabase.class, "smart_finance_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}