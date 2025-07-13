package com.example.smartfinance.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smartfinance.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddIncomeBottomSheet extends BottomSheetDialogFragment {

    private EditText editTextAmount, editTextNote;
    private Button buttonSave;

    public interface IncomeListener {
        void onIncomeAdded(double amount, String note);
    }

    private IncomeListener listener;

    public void setIncomeListener(IncomeListener listener) {
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_income_modal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editTextAmount = view.findViewById(R.id.incomeAmountEditText);
        editTextNote = view.findViewById(R.id.incomeNoteEditText);
        buttonSave = view.findViewById(R.id.saveIncomeBtn);

        buttonSave.setOnClickListener(v -> {
            String amountStr = editTextAmount.getText().toString().trim();
            String note = editTextNote.getText().toString().trim();

            if (!amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);

                if (listener != null) {
                    listener.onIncomeAdded(amount, note);
                }

                dismiss();
            } else {
                editTextAmount.setError("Amount required");
            }
        });
    }
}
