package com.example.smartfinance.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.ui.budget.AppDatabase;
import com.example.smartfinance.R;
import com.example.smartfinance.ui.budget.Recyclerview.Budget;
import com.example.smartfinance.ui.budget.Recyclerview.BudgetAdapter;
import com.example.smartfinance.ui.budget.sheets.AddBudgetBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BudgetFragment extends Fragment {

    private BudgetViewModel budgetViewModel;
    private BudgetAdapter budgetAdapter;
    private RecyclerView budgetRecyclerView;
    private TextView viewAllBudgets;

    private FloatingActionButton fabMenu, btnAddBudget;
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

        // Setup RecyclerView
        budgetAdapter = new BudgetAdapter(new ArrayList<>());
        budgetRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        budgetRecyclerView.setAdapter(budgetAdapter);

        // Initialize database and ViewModel
        AppDatabase database = AppDatabase.getDatabase(requireContext());
        BudgetViewModel.Factory factory = new BudgetViewModel.Factory(database.budgetDao());
        budgetViewModel = new ViewModelProvider(this, factory).get(BudgetViewModel.class);

        // Observe budget data from Room database
        budgetViewModel.getAllBudgets().observe(getViewLifecycleOwner(), budgets -> {
            if (budgets != null) {
                budgetAdapter.setBudgets(budgets);
            }
        });

        // Handle "View All" click
        viewAllBudgets.setOnClickListener(v ->
                Toast.makeText(requireContext(), "View All Budgets Clicked!", Toast.LENGTH_SHORT).show()
        );

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Main FAB toggle
        fabMenu.setOnClickListener(v -> toggleFabMenu());

        // Mini FAB -> open AddBudgetBottomSheet
        btnAddBudget.setOnClickListener(v -> {
            AddBudgetBottomSheet bottomSheet = new AddBudgetBottomSheet();
            bottomSheet.setBudgetListener((category, amount, startDate, endDate, description) -> {
                // Create new Budget
                Budget newBudget = new Budget(category, amount);

                // Insert into Room database (NOT to local list)
                budgetViewModel.insertBudget(newBudget);

                Toast.makeText(requireContext(),
                        "Budget Added: " + category + " - ₹" + amount,
                        Toast.LENGTH_LONG).show();
            });

            bottomSheet.show(getParentFragmentManager(), "AddBudgetBottomSheet");
        });
    }

    private void toggleFabMenu() {
        if (isFabOpen) {
            btnAddBudget.animate().translationY(0).alpha(0f).setDuration(200)
                    .withEndAction(() -> btnAddBudget.setVisibility(View.GONE));
        } else {
            btnAddBudget.setVisibility(View.VISIBLE);
            btnAddBudget.setAlpha(0f);
            btnAddBudget.animate().translationY(-200).alpha(1f).setDuration(200);
        }
        isFabOpen = !isFabOpen;
    }
}