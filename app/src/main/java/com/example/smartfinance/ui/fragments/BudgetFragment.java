package com.example.smartfinance.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.data.model.Budget;
import com.example.smartfinance.ui.viewmodels.BudgetViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetFragment extends Fragment {

    private BudgetViewModel mViewModel;
    private BudgetAdapter mAdapter;
    private TextView tvTotalBudget, tvTotalSpent, tvTotalRemaining;
    private AppDatabase database;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public static BudgetFragment newInstance() {
        return new BudgetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        database = AppDatabase.getDatabase(requireContext());
        initViews(view);
        setupRecyclerView(view);
        setupViewModel();
        setupObservers();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    private void initViews(View view) {
        tvTotalBudget = view.findViewById(R.id.tvTotalBudget);
        tvTotalSpent = view.findViewById(R.id.tvTotalSpent);
        tvTotalRemaining = view.findViewById(R.id.tvTotalRemaining);
        ExtendedFloatingActionButton fabAddBudget = view.findViewById(R.id.fabAddBudget);
        fabAddBudget.setOnClickListener(v -> showAddBudgetDialog());
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewBudgets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new BudgetAdapter(new ArrayList<>(), this::handleBudgetAction);
        recyclerView.setAdapter(mAdapter);
    }

    private void setupViewModel() {
        BudgetViewModelFactory factory = new BudgetViewModelFactory(database);
        mViewModel = new ViewModelProvider(this, factory).get(BudgetViewModel.class);
    }

    private void setupObservers() {
        mViewModel.getAllBudgets().observe(getViewLifecycleOwner(), budgets -> {
            mAdapter.updateBudgets(budgets);
            computeAndUpdateTotals(budgets);
        });
    }

    private void computeAndUpdateTotals(List<Budget> budgets) {
        executor.execute(() -> {
            double totalBudget = 0;
            double totalSpent = 0;
            for (Budget budget : budgets) {
                totalBudget += budget.allocatedAmount;
                totalSpent += budget.currentSpent;
            }
            double totalRemaining = totalBudget - totalSpent;

            final NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            final String budgetStr = formatter.format(totalBudget);
            final String spentStr = formatter.format(totalSpent);
            final String remainingStr = formatter.format(totalRemaining);

            mainHandler.post(() -> {
                tvTotalBudget.setText(budgetStr);
                tvTotalSpent.setText(spentStr);
                tvTotalRemaining.setText(remainingStr);
            });
        });
    }

    private void handleBudgetAction(BudgetAction action, Budget budget) {
        switch (action) {
            case EDIT:
                showEditBudgetDialog(budget);
                break;
            case DELETE:
                showDeleteConfirmDialog(budget);
                break;
            case UPDATE_SPENT:
                showUpdateSpentDialog(budget);
                break;
        }
    }

    private void showAddBudgetDialog() {
        showBudgetDialog(null, "Add New Budget", (dialog, which, budget) -> {
            if (budget != null) {
                mViewModel.insertBudget(budget);
                Toast.makeText(getContext(), "Budget added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditBudgetDialog(Budget budget) {
        showBudgetDialog(budget, "Edit Budget", (dialog, which, editedBudget) -> {
            if (editedBudget != null) {
                mViewModel.updateBudget(editedBudget);
                Toast.makeText(getContext(), "Budget updated", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showUpdateSpentDialog(Budget budget) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_spent, null);
        TextInputEditText etSpent = dialogView.findViewById(R.id.etSpent);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        Dialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setView(dialogView)
                .create();

        btnConfirm.setOnClickListener(v -> {
            String spentStr = etSpent.getText().toString().trim();
            if (TextUtils.isEmpty(spentStr)) {
                Toast.makeText(getContext(), "Please enter spent amount", Toast.LENGTH_SHORT).show();
                return;
            }
            double spent;
            try {
                spent = Double.parseDouble(spentStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }
            budget.currentSpent = spent;
            mViewModel.updateBudget(budget);
            Toast.makeText(getContext(), "Spent updated", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showBudgetDialog(Budget budget, String title, BudgetDialogCallback callback) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_budget, null);
        TextInputEditText etCategory = dialogView.findViewById(R.id.etCategory);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etAmount);
        TextInputEditText etSpent = dialogView.findViewById(R.id.etSpent);
        TextInputEditText etPeriod = dialogView.findViewById(R.id.etPeriod);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        if (budget != null) {
            etCategory.setText(budget.category);
            etAmount.setText(String.valueOf(budget.allocatedAmount));
            etSpent.setText(String.valueOf(budget.currentSpent));
            etPeriod.setText(budget.period);
        }

        Dialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setTitle(title)
                .setView(dialogView)
                .create();

        btnSave.setOnClickListener(v -> {
            String category = etCategory.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String spentStr = etSpent.getText().toString().trim();
            String period = etPeriod.getText().toString().trim();

            if (TextUtils.isEmpty(category) || TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(period)) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount, spent = 0;
            try {
                amount = Double.parseDouble(amountStr);
                if (!TextUtils.isEmpty(spentStr)) {
                    spent = Double.parseDouble(spentStr);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            Budget newBudget = budget != null ? budget : new Budget();
            newBudget.category = category;
            newBudget.allocatedAmount = amount;
            newBudget.currentSpent = spent;
            newBudget.period = period.toLowerCase();
            newBudget.startDate = getPeriodStartTimestamp(period);

            callback.onSave(dialog, 0, newBudget);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmDialog(Budget budget) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Delete Budget")
                .setMessage("Are you sure you want to delete the budget for " + budget.category + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    mViewModel.deleteBudget(budget);
                    Toast.makeText(getContext(), "Budget deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private long getPeriodStartTimestamp(String period) {
        Calendar cal = Calendar.getInstance();
        if ("monthly".equals(period)) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
        } else if ("weekly".equals(period)) {
            while (cal.get(Calendar.DAY_OF_WEEK) != cal.getFirstDayOfWeek()) {
                cal.add(Calendar.DAY_OF_WEEK, -1);
            }
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // Enums and Callback
    enum BudgetAction { EDIT, DELETE, UPDATE_SPENT }

    interface BudgetDialogCallback {
        void onSave(android.content.DialogInterface dialog, int which, Budget budget);
    }

    // Inner Adapter class
    private static class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {
        private List<Budget> budgets;
        private BudgetActionListener listener;

        interface BudgetActionListener {
            void onBudgetAction(BudgetAction action, Budget budget);
        }

        BudgetAdapter(List<Budget> budgets, BudgetActionListener listener) {
            this.budgets = budgets;
            this.listener = listener;
        }

        void updateBudgets(List<Budget> newBudgets) {
            this.budgets = newBudgets;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Budget budget = budgets.get(position);
            holder.tvCategory.setText(budget.category);
            String periodDisplay = !TextUtils.isEmpty(budget.period)
                    ? budget.period.substring(0, 1).toUpperCase() + budget.period.substring(1)
                    : "Unknown";
            holder.tvPeriod.setText(periodDisplay);
            holder.tvAllocated.setText(String.format(Locale.getDefault(), "%.2f", budget.allocatedAmount));
            holder.tvSpent.setText(String.format(Locale.getDefault(), "%.2f", budget.currentSpent));
            double remaining = budget.allocatedAmount - budget.currentSpent;
            holder.tvRemaining.setText(String.format(Locale.getDefault(), "%.2f", remaining));
            Double progress = budget.allocatedAmount > 0 ? (float) budget.currentSpent / budget.allocatedAmount : 0f;
            holder.progressBar.setProgressCompat((int) (progress * 100), true);

            holder.btnEdit.setOnClickListener(v -> listener.onBudgetAction(BudgetAction.EDIT, budget));
            holder.btnDelete.setOnClickListener(v -> listener.onBudgetAction(BudgetAction.DELETE, budget));
            holder.tvUpdateSpent.setOnClickListener(v -> listener.onBudgetAction(BudgetAction.UPDATE_SPENT, budget));
        }

        @Override
        public int getItemCount() {
            return budgets != null ? budgets.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCategory, tvPeriod, tvAllocated, tvSpent, tvRemaining, tvUpdateSpent;
            LinearProgressIndicator progressBar;
            ImageButton btnEdit, btnDelete;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvPeriod = itemView.findViewById(R.id.tvPeriod);
                tvAllocated = itemView.findViewById(R.id.tvAllocated);
                tvSpent = itemView.findViewById(R.id.tvSpent);
                tvRemaining = itemView.findViewById(R.id.tvRemaining);
                tvUpdateSpent = itemView.findViewById(R.id.tvUpdateSpent);
                progressBar = itemView.findViewById(R.id.progressBar);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }

    // Simple Factory for ViewModel
    private static class BudgetViewModelFactory implements ViewModelProvider.Factory {
        private final AppDatabase database;

        BudgetViewModelFactory(AppDatabase database) {
            this.database = database;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(BudgetViewModel.class)) {
                return (T) new BudgetViewModel(database);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}