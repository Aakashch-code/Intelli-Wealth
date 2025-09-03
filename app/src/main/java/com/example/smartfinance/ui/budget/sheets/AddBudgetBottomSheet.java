package com.example.smartfinance.ui.budget.sheets;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smartfinance.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddBudgetBottomSheet extends BottomSheetDialogFragment {

    // Views
    private TextInputEditText etBudgetCategory, etBudgetAmount, etBudgetDescription;
    private TextInputEditText etStartDate, etEndDate;
    private TextInputLayout tilBudgetCategory, tilBudgetAmount, tilStartDate, tilEndDate;
    private MaterialButton btnSave, btnCancel;
    private ImageView btnClose;

    // Date management
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface BudgetListener {


        void onBudgetAdded(
                String category,
                double amount,
                String startDate,
                String endDate,
                String description
        );
    }

    private BudgetListener listener;

    public void setBudgetListener(BudgetListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_budget_modal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupClickListeners();
    }

    private void initializeViews(View view) {
        // Input fields
        etBudgetCategory = view.findViewById(R.id.et_budget_category);
        etBudgetAmount = view.findViewById(R.id.et_budget_amount);
        etBudgetDescription = view.findViewById(R.id.et_budget_description);
        etStartDate = view.findViewById(R.id.et_start_date);
        etEndDate = view.findViewById(R.id.et_end_date);

        // Input layouts for error handling
        tilBudgetCategory = view.findViewById(R.id.til_budget_category);
        tilBudgetAmount = view.findViewById(R.id.til_budget_amount);
        tilStartDate = view.findViewById(R.id.til_start_date);
        tilEndDate = view.findViewById(R.id.til_end_date);

        // Buttons
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnClose = view.findViewById(R.id.btn_close);
    }

    private void setupClickListeners() {
        // Close buttons
        btnClose.setOnClickListener(v -> dismiss());
        btnCancel.setOnClickListener(v -> dismiss());

        // Date pickers
        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etEndDate.setOnClickListener(v -> showDatePicker(false));

        // Save button
        btnSave.setOnClickListener(v -> saveBudget());


        };




    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);

                    if (isStartDate) {
                        etStartDate.setText(dateFormat.format(calendar.getTime()));
                        tilStartDate.setError(null);

                        // If end date is before start date, clear it
                        if (endCalendar.before(startCalendar)) {
                            etEndDate.setText("");
                            endCalendar = Calendar.getInstance();
                        }
                    } else {
                        // Validate that end date is after start date
                        if (calendar.before(startCalendar)) {
                            tilEndDate.setError("End date must be after start date");
                            return;
                        }
                        etEndDate.setText(dateFormat.format(calendar.getTime()));
                        tilEndDate.setError(null);
                    }


                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date for end date picker
        if (!isStartDate && !TextUtils.isEmpty(etStartDate.getText())) {
            datePickerDialog.getDatePicker().setMinDate(startCalendar.getTimeInMillis());
        }

        datePickerDialog.show();
    }





    private void saveBudget() {
        // Clear previous errors
        tilBudgetCategory.setError(null);
        tilBudgetAmount.setError(null);
        tilStartDate.setError(null);
        tilEndDate.setError(null);

        // Validate inputs
        String category = etBudgetCategory.getText().toString().trim();
        String amountStr = etBudgetAmount.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String description = etBudgetDescription.getText().toString().trim();

        boolean isValid = true;

        if (TextUtils.isEmpty(category)) {
            tilBudgetCategory.setError("Budget category is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(amountStr)) {
            tilBudgetAmount.setError("Budget amount is required");
            isValid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    tilBudgetAmount.setError("Amount must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilBudgetAmount.setError("Please enter a valid amount");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(startDate)) {
            tilStartDate.setError("Start date is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(endDate)) {
            tilEndDate.setError("End date is required");
            isValid = false;
        } else if (endCalendar.before(startCalendar)) {
            tilEndDate.setError("End date must be after start date");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Save the budget
        if (listener != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                listener.onBudgetAdded(category, amount, startDate, endDate, description);

                Toast.makeText(getContext(), "Budget saved successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            } catch (NumberFormatException e) {
                tilBudgetAmount.setError("Please enter a valid amount");
            }
        }
    }

    // Public method to pre-fill category if needed
    public void setPreFilledCategory(String category) {
        if (etBudgetCategory != null) {
            etBudgetCategory.setText(category);
        }
    }

    // Public method to set edit mode
    public void setEditMode(String category, double amount, String startDate, String endDate, String description) {
        if (etBudgetCategory != null) {
            etBudgetCategory.setText(category);
            etBudgetAmount.setText(String.valueOf(amount));
            etStartDate.setText(startDate);
            etEndDate.setText(endDate);
            etBudgetDescription.setText(description);

            // Update button text for edit mode
            btnSave.setText("Update Budget");
        }
    }
}