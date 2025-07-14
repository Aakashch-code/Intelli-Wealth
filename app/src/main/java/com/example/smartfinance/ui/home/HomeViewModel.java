package com.example.smartfinance.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    public void addIncome(double amount, String note) {
        // TODO: Save income to DB or update LiveData
    }


    private final MutableLiveData<Double> totalAmount;
    private final MutableLiveData<Double> mExpenses;
    private final MutableLiveData<Double> mIncome;
    private final MutableLiveData<Double> mSavings;

    public HomeViewModel() {
        totalAmount = new MutableLiveData<>();
        totalAmount.setValue(249.40);
        mExpenses = new MutableLiveData<>();
        mExpenses.setValue(750.85);
        mIncome = new MutableLiveData<>();
        mIncome.setValue(1000.25);
        mSavings = new MutableLiveData<>();
        mSavings.setValue(150.00);

    }


    public LiveData<Double> getBudget() {
        return totalAmount;
    }
    public LiveData<Double> getExpenses() {
        return mExpenses;
    }
    public LiveData<Double> getIncome() {
        return mIncome;
    }
    public LiveData<Double> getSavings() {
        return mSavings;
    }



}