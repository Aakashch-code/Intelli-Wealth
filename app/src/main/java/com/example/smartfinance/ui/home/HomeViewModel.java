package com.example.smartfinance.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {


    private final MutableLiveData<Double> mIncome;
    private final MutableLiveData<Double> totalAmount;
    private final MutableLiveData<Double> mExpenses;
    private final MutableLiveData<Double> mSavings;

    public HomeViewModel() {
        mIncome = new MutableLiveData<>();
        mIncome.setValue(0.00);

        totalAmount = new MutableLiveData<>();
        totalAmount.setValue(249.40);

        mExpenses = new MutableLiveData<>();
        mExpenses.setValue(750.85);

        mSavings = new MutableLiveData<>();
        mSavings.setValue(150.00);
    }

    public void addIncome(double amount, String note) {
        double currentIncome = mIncome.getValue() != null ? mIncome.getValue() : 0.0;
        mIncome.setValue(currentIncome + amount);

        // TODO: Also add note to DB if needed
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
