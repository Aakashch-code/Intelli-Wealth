package com.example.smartfinance.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<Double> totalAmount;
    private final MutableLiveData<Double> mSavings;

    public HomeViewModel() {
        totalAmount = new MutableLiveData<>();
        totalAmount.setValue(0.00);

        mSavings = new MutableLiveData<>();
        mSavings.setValue(150.00);
    }

    public void updateBudget(double income, double expense) {
        double total = income - expense;
        totalAmount.setValue(total);
    }

    public LiveData<Double> getBudget() {
        return totalAmount;
    }

    public LiveData<Double> getSavings() {
        return mSavings;
    }

}
