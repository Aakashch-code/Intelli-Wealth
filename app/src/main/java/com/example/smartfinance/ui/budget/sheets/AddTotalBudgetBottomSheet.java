package com.example.smartfinance.ui.budget.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.budget.BudgetViewModel;
import com.example.smartfinance.ui.budget.Recyclerview.Budget;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddTotalBudgetBottomSheet extends BottomSheetDialogFragment {
    private EditText amountInput;
    private BudgetViewModel budgetViewModel;

    // Interface for callback
    public interface BudgetListener {
        void onBudgetAdded(double amount);
    }

    private BudgetListener budgetListener;

    public void setBudgetListener(BudgetListener listener) {
        this.budgetListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_budget, container, false);
        amountInput = view.findViewById(R.id.totalBudgetAmount);
        Button btnSave = view.findViewById(R.id.btn_save_income);
        Button btnCancel = view.findViewById(R.id.btn_cancel_income);

        // Get the ViewModel
        budgetViewModel = new androidx.lifecycle.ViewModelProvider(requireActivity(),
                new BudgetViewModel.Factory(requireActivity().getApplication())).get(BudgetViewModel.class);

        btnSave.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString();

            if (amountStr.isEmpty()) {
                amountInput.setError("Please enter an amount");
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    amountInput.setError("Amount must be greater than 0");
                    return;
                }


                // Notify listener if set
                if (budgetListener != null) {
                    budgetListener.onBudgetAdded(amount);
                }

                Toast.makeText(requireContext(), "Budget added: " + amount, Toast.LENGTH_SHORT).show();
                dismiss();
            } catch (NumberFormatException e) {
                amountInput.setError("Please enter a valid number");
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }
}