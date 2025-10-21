package com.example.smartfinance.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.ui.viewmodels.ReportsViewModel;

public class ReportsViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final AppDatabase database;

    public ReportsViewModelFactory(Application application, AppDatabase database) {
        this.application = application;
        this.database = database;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReportsViewModel.class)) {
            return (T) new ReportsViewModel(application, database);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}