package com.example.smartfinance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartfinance.R;
import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.ui.viewmodels.ReportsViewModel;

public class ReportsFragment extends Fragment {

    private ReportsViewModel mViewModel;
    private AppDatabase database;

    public static ReportsFragment newInstance() {
        return new ReportsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = AppDatabase.getDatabase(requireContext());
        setupViewModel();
        setupUI(view);
    }

    private void setupViewModel() {
        ReportsViewModelFactory factory = new ReportsViewModelFactory(database);
        mViewModel = new ViewModelProvider(this, factory).get(ReportsViewModel.class);
    }

    private void setupUI(View view) {
        // TODO: Initialize your UI components and set listeners here.
        // Example:
        // Button btnGenerateReport = view.findViewById(R.id.btnGenerateReport);
        // btnGenerateReport.setOnClickListener(v -> {
        //     // Handle button click
        // });
    }

    private static class ReportsViewModelFactory implements ViewModelProvider.Factory {
        private final AppDatabase database;

        ReportsViewModelFactory(AppDatabase database) {
            this.database = database;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ReportsViewModel.class)) {
                return (T) new ReportsViewModel(database);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
