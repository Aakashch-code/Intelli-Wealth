package com.example.smartfinance.ui.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.home.income.Transaction;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseBottomSheet extends BottomSheetDialogFragment {

    private EditText editTextAmount, editTextNote;
    private Button buttonSave;

    public interface ExpenseListener {
        void onExpenseAdded(double amount, String note);
    }

    private ExpenseListener listener;

    public void setExpenseListener(ExpenseListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_expense_modal, container, false); // Make sure this layout exists
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editTextAmount = view.findViewById(R.id.expenseAmountEditText);
        editTextNote = view.findViewById(R.id.expenseNoteEditText);
        buttonSave = view.findViewById(R.id.saveExpenseBtn);

        buttonSave.setOnClickListener(v -> {
            String amountStr = editTextAmount.getText().toString().trim();
            String note = editTextNote.getText().toString().trim();

            if (!amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);

                if (listener != null) {
                    listener.onExpenseAdded(amount, note);
                }

                dismiss();
            } else {
                editTextAmount.setError("Amount required");
            }
        });

        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());

        // Set up category spinner
        AutoCompleteTextView categorySpinner = view.findViewById(R.id.expenseCategorySpinner);
        String[] categories = {"Food", "Transport", "Shopping", "Health", "Entertainment", "Education", "Bills", "EMI", "Others"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnClickListener(v -> categorySpinner.showDropDown());


        // Set payment method spinner
        AutoCompleteTextView paymentMethodSpinner = view.findViewById(R.id.expensePaymentMethod);
        String[] paymentMethods = {"Cash", "Card", "Google Pay", "PhonePe", "Paytm", "Other"};

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, paymentMethods);
        paymentMethodSpinner.setAdapter(adapter1);
        paymentMethodSpinner.setOnClickListener(v -> paymentMethodSpinner.showDropDown());

        // Set up date picker
        TextInputEditText expenseDateEditText = view.findViewById(R.id.expenseDateEditText);
        Calendar calendar = Calendar.getInstance();

        expenseDateEditText.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        expenseDateEditText.setText(sdf.format(calendar.getTime()));
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

    }
}
