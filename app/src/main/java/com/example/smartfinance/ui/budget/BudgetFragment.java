package com.example.smartfinance.ui.budget;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.budget.Recyclerview.Budget;
import com.example.smartfinance.ui.budget.Recyclerview.BudgetAdapter;
import com.example.smartfinance.ui.budget.sheets.AddBudgetBottomSheet;
import com.example.smartfinance.ui.budget.sheets.AddTotalBudgetBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private BudgetViewModel budgetViewModel;
    private BudgetAdapter budgetAdapter;
    private RecyclerView budgetRecyclerView;
    private TextView viewAllBudgets;
    private TextView tvTotalBudget, tvAmountSpent, tvRemainingBudget;

    private FloatingActionButton fabMenu, btnAddBudget, btnAddTotalBudget;
    private boolean isFabOpen = false;

    public static BudgetFragment newInstance() {
        return new BudgetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_budget, container, false);

        // Initialize UI components
        budgetRecyclerView = root.findViewById(R.id.budgetRecyclerView);
        viewAllBudgets = root.findViewById(R.id.viewAllBudgets);
        fabMenu = root.findViewById(R.id.fabMenu);
        btnAddBudget = root.findViewById(R.id.btnAddBudget);
        btnAddTotalBudget = root.findViewById(R.id.btnAddTotalBudget);

        // Budget summary views
        tvTotalBudget = root.findViewById(R.id.tv_total_budget);
        tvAmountSpent = root.findViewById(R.id.tv_amount_spent);
        tvRemainingBudget = root.findViewById(R.id.tv_remaining_budget);

        // Setup RecyclerView
        budgetAdapter = new BudgetAdapter(new ArrayList<>());
        budgetRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        budgetRecyclerView.setAdapter(budgetAdapter);

        // Initialize ViewModel
        BudgetViewModel.Factory factory = new BudgetViewModel.Factory(requireActivity().getApplication());
        budgetViewModel = new ViewModelProvider(this, factory).get(BudgetViewModel.class);

        // Observe category budget data from Room database
        budgetViewModel.getAllCategoryBudgets().observe(getViewLifecycleOwner(), budgets -> {
            if (budgets != null) {
                budgetAdapter.setBudgets(budgets);
            }
        });

        // Observe budget summary data
        observeBudgetSummary();

        // Handle "View All" click
        viewAllBudgets.setOnClickListener(v ->
                Toast.makeText(requireContext(), "View All Budgets Clicked!", Toast.LENGTH_SHORT).show()
        );

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Main FAB toggle
        fabMenu.setOnClickListener(v -> toggleFabMenu());

        // Add Total Budget FAB
        btnAddTotalBudget.setOnClickListener(v -> {
            AddTotalBudgetBottomSheet bottomSheet = new AddTotalBudgetBottomSheet();
            bottomSheet.setBudgetListener(amount -> {
                budgetViewModel.updateTotalBudget(amount);
                Toast.makeText(requireContext(),
                        "Total Budget Updated: " + formatCurrency(amount),
                        Toast.LENGTH_LONG).show();
            });
            bottomSheet.show(getParentFragmentManager(), "AddTotalBudgetBottomSheet");
            toggleFabMenu();
        });

        // Add Category Budget FAB
        btnAddBudget.setOnClickListener(v -> {
            AddBudgetBottomSheet bottomSheet = new AddBudgetBottomSheet();
            bottomSheet.setBudgetListener((category, amount, startDate, endDate, description) -> {
                Budget newBudget = new Budget(category, amount, startDate, endDate, description);
                budgetViewModel.insertBudget(newBudget);
                Toast.makeText(requireContext(),
                        "Budget Added: " + category + " - " + formatCurrency(amount),
                        Toast.LENGTH_LONG).show();
            });
            bottomSheet.show(getParentFragmentManager(), "AddBudgetBottomSheet");
            toggleFabMenu();
        });
    }

    private void updateRemainingBudget() {
        Double totalBudgetObj = 0.0;
        if (budgetViewModel.getTotalBudget().getValue() != null) {
            totalBudgetObj = budgetViewModel.getTotalBudget().getValue().getAllocatedAmount();
        }
        
        Double totalSpent = budgetViewModel.getTotalCategorySpent().getValue();
        Double totalSpentValue = totalSpent != null ? totalSpent : 0.0;

        double remaining = totalBudgetObj - totalSpentValue;

        // Update remaining budget text
        tvRemainingBudget.setText(formatCurrency(remaining));

        // Set color based on remaining amount
        if (remaining < 0) {
            tvRemainingBudget.setTextColor(ContextCompat.getColor(requireContext(), R.color.error));
        } else {
            tvRemainingBudget.setTextColor(ContextCompat.getColor(requireContext(), R.color.success));
        }
    }

    private void observeBudgetSummary() {
        // Observe total budget (the overall budget limit)
        budgetViewModel.getTotalBudget().observe(getViewLifecycleOwner(), totalBudget -> {
            if (totalBudget != null) {
                tvTotalBudget.setText(formatCurrency(totalBudget.getAllocatedAmount()));
                updateRemainingBudget();
            } else {
                // Handle case when no total budget is set
                tvTotalBudget.setText(formatCurrency(0));
                updateRemainingBudget();
            }
        });

        // Observe total spent from categories
        budgetViewModel.getTotalCategorySpent().observe(getViewLifecycleOwner(), totalSpent -> {
            Double spentValue = totalSpent != null ? totalSpent : 0.0;
            tvAmountSpent.setText(formatCurrency(spentValue));
            updateRemainingBudget();
        });

        // Observe total category budget
        budgetViewModel.getTotalCategoryBudget().observe(getViewLifecycleOwner(), totalCategoryBudget -> {
            if (totalCategoryBudget != null) {
                updateRemainingBudget();
            }
        });
    }

    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return currencyFormat.format(amount);
    }

    private void toggleFabMenu() {
        if (isFabOpen) {
            // Close mini FABs
            btnAddBudget.animate().translationY(0).alpha(0f).setDuration(200)
                    .withEndAction(() -> btnAddBudget.setVisibility(View.GONE));
            btnAddTotalBudget.animate().translationY(0).alpha(0f).setDuration(200)
                    .withEndAction(() -> btnAddTotalBudget.setVisibility(View.GONE));
            Log.d("BudgetFragment", "FAB menu closed");
        } else {
            // Open mini FABs
            btnAddBudget.setVisibility(View.VISIBLE);
            btnAddBudget.setAlpha(0f);
            btnAddBudget.animate()
                    .translationY(-getResources().getDimension(R.dimen.fab_spacing))
                    .alpha(1f)
                    .setDuration(200);

            btnAddTotalBudget.setVisibility(View.VISIBLE);
            btnAddTotalBudget.setAlpha(0f);
            btnAddTotalBudget.animate()
                    .translationY(-getResources().getDimension(R.dimen.fab_spacing) * 8)
                    .alpha(1f)
                    .setDuration(200);

            Log.d("BudgetFragment", "FAB menu opened");
        }
        isFabOpen = !isFabOpen;
    }
}