package com.example.smartfinance.ui.goals;

import androidx.appcompat.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.goals.adapter.GoalAdapter;
import com.example.smartfinance.ui.goals.model.Goal;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GoalsFragment extends Fragment implements GoalAdapter.OnGoalClickListener {

    private GoalsViewModel goalViewModel;
    private GoalAdapter adapter;
    private RecyclerView recyclerView;
    private NumberFormat inrFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        // Initialize INR currency format with explicit symbol
        inrFormat = NumberFormat.getCurrencyInstance();
        inrFormat.setCurrency(java.util.Currency.getInstance("INR"));

        initializeViews(view);
        setupRecyclerView();
        setupFabButton(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.goalsRecyclerView);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new GoalAdapter(this, inrFormat); // Pass INR format to adapter
        recyclerView.setAdapter(adapter);
    }

    private void setupFabButton(View view) {
        FloatingActionButton fabAddGoal = view.findViewById(R.id.fabAddGoal);
        fabAddGoal.setOnClickListener(v -> showAddGoalDialog());
    }

    private void setupViewModel() {
        goalViewModel = new ViewModelProvider(this).get(GoalsViewModel.class);
        goalViewModel.getAllGoals().observe(getViewLifecycleOwner(), goals -> {
            adapter.setGoals(goals);
        });
    }

    private void showAddGoalDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_goal, null);
        dialog.setContentView(dialogView);

        initializeAddGoalDialogViews(dialogView, dialog);
        dialog.show();
    }

    private void initializeAddGoalDialogViews(View dialogView, BottomSheetDialog dialog) {
        EditText etGoalName = dialogView.findViewById(R.id.etGoalName);
        EditText etTargetAmount = dialogView.findViewById(R.id.etTargetAmount);
        EditText etSavedAmount = dialogView.findViewById(R.id.etSavedAmount);
        TextView tvSelectedDate = dialogView.findViewById(R.id.tvSelectedDate);
        RadioGroup rgPriority = dialogView.findViewById(R.id.rgPriority);
        Button btnSelectDate = dialogView.findViewById(R.id.btnSelectDate);
        Button btnSaveGoal = dialogView.findViewById(R.id.btnSaveGoal);

        // Add INR hint to amount fields
        etTargetAmount.setHint("Target amount (₹)");
        etSavedAmount.setHint("Saved amount (₹)");

        final Calendar calendar = Calendar.getInstance();
        Date[] selectedDate = {null};

        setupDatePicker(btnSelectDate, tvSelectedDate, calendar, selectedDate);
        setupSaveGoalButton(btnSaveGoal, etGoalName, etTargetAmount, etSavedAmount,
                selectedDate, rgPriority, dialog);
    }

    private void setupDatePicker(Button btnSelectDate, TextView tvSelectedDate,
                                 Calendar calendar, Date[] selectedDate) {
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        selectedDate[0] = calendar.getTime();
                        tvSelectedDate.setText(new java.text.SimpleDateFormat("MMM dd, yyyy")
                                .format(selectedDate[0]));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupSaveGoalButton(Button btnSaveGoal, EditText etGoalName,
                                     EditText etTargetAmount, EditText etSavedAmount,
                                     Date[] selectedDate, RadioGroup rgPriority,
                                     BottomSheetDialog dialog) {
        btnSaveGoal.setOnClickListener(v -> {
            if (validateGoalInputs(etGoalName, etTargetAmount, etSavedAmount, selectedDate)) {
                String goalName = etGoalName.getText().toString().trim();
                double targetAmount = Double.parseDouble(etTargetAmount.getText().toString().trim());
                double savedAmount = Double.parseDouble(etSavedAmount.getText().toString().trim());
                String priority = getSelectedPriority(rgPriority);

                Goal goal = new Goal(goalName, targetAmount, savedAmount, selectedDate[0], priority);
                goalViewModel.insert(goal);

                Toast.makeText(getContext(), "Goal added successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private boolean validateGoalInputs(EditText etGoalName, EditText etTargetAmount,
                                       EditText etSavedAmount, Date[] selectedDate) {
        String goalName = etGoalName.getText().toString().trim();
        String targetAmountStr = etTargetAmount.getText().toString().trim();
        String savedAmountStr = etSavedAmount.getText().toString().trim();

        if (goalName.isEmpty()) {
            etGoalName.setError("Goal name is required");
            return false;
        }

        if (targetAmountStr.isEmpty()) {
            etTargetAmount.setError("Target amount is required");
            return false;
        }

        if (savedAmountStr.isEmpty()) {
            etSavedAmount.setError("Saved amount is required");
            return false;
        }

        try {
            double targetAmount = Double.parseDouble(targetAmountStr);
            if (targetAmount <= 0) {
                etTargetAmount.setError("Target amount must be positive");
                return false;
            }

            double savedAmount = Double.parseDouble(savedAmountStr);
            if (savedAmount < 0) {
                etSavedAmount.setError("Saved amount cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            etTargetAmount.setError("Invalid number format");
            return false;
        }

        if (selectedDate[0] == null) {
            Toast.makeText(getContext(), "Please select a target date", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String getSelectedPriority(RadioGroup rgPriority) {
        int selectedPriorityId = rgPriority.getCheckedRadioButtonId();
        if (selectedPriorityId == R.id.rbHigh) {
            return "HIGH";
        } else if (selectedPriorityId == R.id.rbLow) {
            return "LOW";
        }
        return "MEDIUM"; // Default
    }

    @Override
    public void onGoalClick(Goal goal) {
        showUpdateAmountDialog(goal);
    }

    @Override
    public void onGoalLongClick(Goal goal) {
        showDeleteConfirmationDialog(goal);
    }

    private void showDeleteConfirmationDialog(Goal goal) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete '" + goal.getGoalName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    goalViewModel.delete(goal);
                    Toast.makeText(getContext(), "Goal deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showUpdateAmountDialog(Goal goal) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_amount, null);

        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        Button btnAdd10 = dialogView.findViewById(R.id.btnAdd10);
        Button btnAdd50 = dialogView.findViewById(R.id.btnAdd50);
        Button btnAdd100 = dialogView.findViewById(R.id.btnAdd100);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);

        // Update button texts to show INR symbols
        btnAdd10.setText("+ ₹10");
        btnAdd50.setText("+ ₹50");
        btnAdd100.setText("+ ₹100");

        // Set current saved amount with INR format
        etAmount.setHint("Amount in ₹");
        etAmount.setText(String.valueOf(goal.getSavedAmount()));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setTitle("Update " + goal.getGoalName())
                .create();

        setupQuickAddButtons(etAmount, btnAdd10, btnAdd50, btnAdd100);
        setupDialogButtons(dialog, etAmount, goal, btnCancel, btnUpdate);

        dialog.show();
    }

    private void setupQuickAddButtons(EditText etAmount, Button btnAdd10,
                                      Button btnAdd50, Button btnAdd100) {
        btnAdd10.setOnClickListener(v -> updateAmountField(etAmount, 10));
        btnAdd50.setOnClickListener(v -> updateAmountField(etAmount, 50));
        btnAdd100.setOnClickListener(v -> updateAmountField(etAmount, 100));
    }

    private void updateAmountField(EditText etAmount, double amountToAdd) {
        double currentAmount = getCurrentAmount(etAmount);
        etAmount.setText(String.valueOf(currentAmount + amountToAdd));
    }

    private void setupDialogButtons(AlertDialog dialog, EditText etAmount,
                                    Goal goal, Button btnCancel, Button btnUpdate) {
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnUpdate.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (validateAmountInput(etAmount, amountStr)) {
                double newAmount = Double.parseDouble(amountStr);
                handleAmountUpdate(goal, newAmount, dialog);
            }
        });
    }

    private boolean validateAmountInput(EditText etAmount, String amountStr) {
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Please enter an amount");
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount < 0) {
                etAmount.setError("Amount cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("Please enter a valid number");
            return false;
        }

        return true;
    }

    private void handleAmountUpdate(Goal goal, double newAmount, AlertDialog dialog) {
        if (newAmount > goal.getTargetAmount()) {
            showExceedTargetConfirmation(goal, newAmount, dialog);
        } else {
            updateGoalAmount(goal, newAmount);
            dialog.dismiss();
        }
    }

    private void showExceedTargetConfirmation(Goal goal, double newAmount, AlertDialog dialog) {
        String formattedTarget = formatAmountInINR(goal.getTargetAmount());
        String formattedNewAmount = formatAmountInINR(newAmount);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Amount Exceeds Target")
                .setMessage("You're saving " + formattedNewAmount + " which exceeds your target of " + formattedTarget + ". Continue?")
                .setPositiveButton("Yes", (d, which) -> {
                    updateGoalAmount(goal, newAmount);
                    dialog.dismiss();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private double getCurrentAmount(EditText etAmount) {
        try {
            String amountStr = etAmount.getText().toString().trim();
            if (TextUtils.isEmpty(amountStr)) {
                return 0;
            }
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateGoalAmount(Goal goal, double newAmount) {
        goal.setSavedAmount(newAmount);
        goalViewModel.update(goal);
        Toast.makeText(requireContext(), "Amount updated to " + formatAmountInINR(newAmount), Toast.LENGTH_SHORT).show();
    }

    // Helper method to format amount in INR with explicit symbol
    private String formatAmountInINR(double amount) {
        try {
            return "₹" + String.format(Locale.ENGLISH, "%.2f", amount);
        } catch (Exception e) {
            return "₹" + amount;
        }
    }
}