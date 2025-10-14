package com.example.smartfinance.data.sheets;

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddIncomeBottomSheet extends BottomSheetDialogFragment {

    private EditText editTextAmount, editTextNote;
    private AutoCompleteTextView categorySpinner, paymentMethodSpinner;
    private TextInputEditText incomeDateEditText;
    private Button buttonSave;

    public interface IncomeListener {
        void onIncomeAdded(double amount, String note, String category, String paymentMethod, String date, long timestamp);
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
        categorySpinner = view.findViewById(R.id.incomeCategorySpinner);
        paymentMethodSpinner = view.findViewById(R.id.incomePaymentMethod);
        incomeDateEditText = view.findViewById(R.id.incomeDateEditText);
        buttonSave = view.findViewById(R.id.saveIncomeBtn);

        // Set up category spinner
        String[] categories = {"Salary", "Freelancing", "Business", "Investments", "Gifts", "Cashback / Rewards", "Rental Income", "Interest Income", "Refunds", "Other Income"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        categorySpinner.setAdapter(categoryAdapter);

        // Set Payment Method spinner
        String[] paymentMethods = {"BHIM UPI", "Google Pay", "PhonePe", "Paytm", "Other"};
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, paymentMethods);
        paymentMethodSpinner.setAdapter(paymentAdapter);

        // Set default date to today
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        incomeDateEditText.setText(sdf.format(calendar.getTime()));

        // Date picker setup
        incomeDateEditText.setOnClickListener(v -> showDatePicker(calendar));

        buttonSave.setOnClickListener(v -> {
            String amountStr = editTextAmount.getText().toString().trim();
            String note = editTextNote.getText().toString().trim();
            String category = categorySpinner.getText().toString().trim();
            String paymentMethod = paymentMethodSpinner.getText().toString().trim();
            String date = incomeDateEditText.getText().toString().trim();

            if (validateInput(amountStr, category, paymentMethod, date)) {
                double amount = Double.parseDouble(amountStr);

                // Convert date string to timestamp
                long timestamp = convertDateToTimestamp(date);

                if (listener != null) {
                    listener.onIncomeAdded(amount, note, category, paymentMethod, date, timestamp);
                }
                dismiss();
            }
        });

        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());
    }

    private boolean validateInput(String amount, String category, String paymentMethod, String date) {
        boolean isValid = true;

        if (amount.isEmpty()) {
            editTextAmount.setError("Amount required");
            isValid = false;
        }

        if (category.isEmpty()) {
            categorySpinner.setError("Category required");
            isValid = false;
        }

        if (paymentMethod.isEmpty()) {
            paymentMethodSpinner.setError("Payment method required");
            isValid = false;
        }

        if (date.isEmpty()) {
            incomeDateEditText.setError("Date required");
            isValid = false;
        }

        return isValid;
    }

    private void showDatePicker(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    incomeDateEditText.setText(sdf.format(calendar.getTime()));
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private long convertDateToTimestamp(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.parse(dateString).getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}