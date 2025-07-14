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
import androidx.lifecycle.ViewModelProvider;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.home.income.Transaction;
import com.example.smartfinance.ui.home.income.TransactionViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());


        // Set up category spinner
        AutoCompleteTextView categorySpinner = view.findViewById(R.id.incomeCategorySpinner);
        String[] categories = {"-- Income --", "Salary", "Freelancing", "Business", "Investments", "Gifts", "Cashback / Rewards", "Rental Income", "Interest Income", "Refunds", "Other Income"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnClickListener(v -> categorySpinner.showDropDown());

        // Set Payment Method spinner
        AutoCompleteTextView paymentMethodSpinner = view.findViewById(R.id.incomePaymentMethod);
        String[] paymentMethods = {"BHIM UPI", "Google Pay", "PhonePe", "Paytm", "Other"};

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, paymentMethods);
        paymentMethodSpinner.setAdapter(adapter1);
        paymentMethodSpinner.setOnClickListener(v -> paymentMethodSpinner.showDropDown());

        // Set up date picker
        TextInputEditText incomeDateEditText = view.findViewById(R.id.incomeDateEditText);
        Calendar calendar = Calendar.getInstance();

        incomeDateEditText.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        incomeDateEditText.setText(sdf.format(calendar.getTime()));
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });



    }
}
