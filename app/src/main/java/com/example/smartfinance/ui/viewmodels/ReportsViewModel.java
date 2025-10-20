package com.example.smartfinance.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.smartfinance.data.local.database.AppDatabase;

public class ReportsViewModel extends AndroidViewModel {

    private final AppDatabase database;

    public ReportsViewModel(@NonNull AppDatabase database) {
        super(null);
        this.database = database;
    }

    // TODO: Add LiveData methods and business logic for fetching reports later
}
