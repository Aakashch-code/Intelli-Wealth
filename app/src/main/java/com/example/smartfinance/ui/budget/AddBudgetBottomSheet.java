package com.example.smartfinance.ui.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.smartfinance.R;
import java.util.Calendar;

public class AddBudgetBottomSheet extends BottomSheetDialogFragment {

    private EditText titleInput, amountInput, notesInput;
    private Spinner categorySpinner;
    private Button startDateBtn, endDateBtn, saveBtn;

    private String startDate = "", endDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_budget_bottom_sheet, container, false);

        titleInput = view.findViewById(R.id.budgetTitle);
        amountInput = view.findViewById(R.id.budgetAmount);
        notesInput = view.findViewById(R.id.budgetNotes);
        categorySpinner = view.findViewById(R.id.budgetCategorySpinner);
        startDateBtn = view.findViewById(R.id.startDateBtn);
        endDateBtn = view.findViewById(R.id.endDateBtn);
        saveBtn = view.findViewById(R.id.saveBudgetBtn);

        setupCategorySpinner();
        setupDatePickers();

        saveBtn.setOnClickListener(v -> saveBudget());

        return view;
    }

    private void setupCategorySpinner() {
        String[] categories = {"Groceries", "Bills", "Travel", "Entertainment", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void setupDatePickers() {
        startDateBtn.setOnClickListener(v -> showDatePicker(true));
        endDateBtn.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    if (isStart) {
                        startDate = date;
                        startDateBtn.setText("Start: " + date);
                    } else {
                        endDate = date;
                        endDateBtn.setText("End: " + date);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveBudget() {
        String title = titleInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (title.isEmpty() || amountStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // TODO: Save to DB or ViewModel
        Toast.makeText(getContext(), "Budget Saved: " + title, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
