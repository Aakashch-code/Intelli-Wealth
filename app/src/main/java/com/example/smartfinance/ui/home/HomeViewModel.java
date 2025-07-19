package com.example.smartfinance.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartfinance.ui.home.income.TransactionViewModel;

public class HomeViewModel extends ViewModel {


    private final MutableLiveData<Double> mIncome;
    private final MutableLiveData<Double> totalAmount;
    private final MutableLiveData<Double> mExpenses;
    private final MutableLiveData<Double> mSavings;

    public HomeViewModel() {
        mIncome = new MutableLiveData<>();
        mIncome.setValue(0.00);

        totalAmount = new MutableLiveData<>();
        totalAmount.setValue(0.00);

        mExpenses = new MutableLiveData<>();
        mExpenses.setValue(0.00);

        mSavings = new MutableLiveData<>();
        mSavings.setValue(150.00);
    }

    private void updateTotalAmount() {
        double income = mIncome.getValue() != null ? mIncome.getValue() : 0.0;
        double expenses = mExpenses.getValue() != null ? mExpenses.getValue() : 0.0;
        double total = income - expenses;
        totalAmount.setValue(total);
    }
    public void addIncome(double amount, String note) {
        double currentIncome = mIncome.getValue() != null ? mIncome.getValue() : 0.0;
        mIncome.setValue(currentIncome + amount);

        updateTotalAmount();

        // TODO: Also add note to DB if needed

    }
    public void addExpense(double amount, String note) {
        double currentExpense = mExpenses.getValue() != null ? mExpenses.getValue() : 0.0;
        mExpenses.setValue(currentExpense + amount);

        updateTotalAmount();

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
