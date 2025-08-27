package com.example.smartfinance.ui.home;

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

import com.example.smartfinance.R;
import com.example.smartfinance.databinding.FragmentHomeBinding;
import com.example.smartfinance.ui.home.Transactions.TransactionAdapter;
import com.example.smartfinance.ui.home.income.Transaction;
import com.example.smartfinance.ui.home.income.TransactionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private double currentIncome = 0.0;
    private double currentExpense = 0.0;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fabMenu = view.findViewById(R.id.fabMenu);
        FloatingActionButton fabAddIncome = view.findViewById(R.id.btnAddIncome);
        FloatingActionButton fabAddExpense = view.findViewById(R.id.btnAddExpense);
        FloatingActionButton fabAddBudget = view.findViewById(R.id.btnAddBudget);

        final boolean[] isFabMenuOpen = {false};

        fabMenu.setOnClickListener(v -> {
            if (!isFabMenuOpen[0]) {
                // Show mini FABs with animation
                showFab(fabAddIncome);
                showFab(fabAddExpense);
                showFab(fabAddBudget);
                fabMenu.animate().rotation(45f).setDuration(200).start(); // rotate main FAB
            } else {
                // Hide mini FABs with animation
                hideFab(fabAddIncome);
                hideFab(fabAddExpense);
                hideFab(fabAddBudget);
                fabMenu.animate().rotation(0f).setDuration(200).start();
            }
            isFabMenuOpen[0] = !isFabMenuOpen[0];
        });

    }

    // Animation helpers
    private void showFab(FloatingActionButton fab) {
        fab.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);
        fab.setTranslationY(100f);
        fab.animate().alpha(1f).translationY(0f).setDuration(200).start();
    }

    private void hideFab(FloatingActionButton fab) {
        fab.animate().alpha(0f).translationY(100f).setDuration(200)
                .withEndAction(() -> fab.setVisibility(View.GONE))
                .start();
    }


    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout and bind views
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModels
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        TransactionViewModel transactionViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(TransactionViewModel.class);

        // Observe LiveData for UI updates
        observeViewModels(homeViewModel, transactionViewModel);

        // Set up Add Income button
        binding.btnAddIncome.setOnClickListener(v -> {
            AddIncomeBottomSheet bottomSheet = new AddIncomeBottomSheet();
            bottomSheet.setIncomeListener((amount, note) -> {
                Toast.makeText(getContext(), "Income added: $" + amount, Toast.LENGTH_SHORT).show();

                Transaction transaction = new Transaction("Income", amount, note, System.currentTimeMillis());
                transactionViewModel.insert(transaction);
            });
            bottomSheet.show(getChildFragmentManager(), "AddIncomeBottomSheet");
        });
        // Set up Add Expense button
        binding.btnAddExpense.setOnClickListener(v -> {
            AddExpenseBottomSheet bottomSheet = new AddExpenseBottomSheet();
            bottomSheet.setExpenseListener((amount, note) -> {
                Toast.makeText(getContext(), "Expense added: $" + amount, Toast.LENGTH_SHORT).show();

                Transaction transaction = new Transaction("Expense", amount, note, System.currentTimeMillis());
                transactionViewModel.insert(transaction);
            });
            bottomSheet.show(getChildFragmentManager(), "AddExpenseBottomSheet");
        });

        // Observe all transactions for RecyclerView
        TransactionAdapter transactionAdapter = new TransactionAdapter(new ArrayList<>(), getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(transactionAdapter);

        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            transactionAdapter.setTransactions(transactions);
        });

        return root;
    }

    private void observeViewModels(HomeViewModel homeViewModel, TransactionViewModel transactionViewModel) {
        // Budget, Expense, Income, and Savings observers
        homeViewModel.getBudget().observe(getViewLifecycleOwner(),
                value -> binding.totalBalance.setText(String.format("$ %.2f", value)));


        // Observe Room DB income directly
        TextView incomeText = binding.incomeAmount;
        transactionViewModel.getTotalByType("Income").observe(getViewLifecycleOwner(), income -> {
            currentIncome = income != null ? income : 0.0;
            incomeText.setText(String.format("$ %.2f", currentIncome));
            homeViewModel.updateBudget(currentIncome, currentExpense);
        });

        // Observe Room DB expense directly
        TextView expenseText = binding.expenseAmount;
        transactionViewModel.getTotalByType("Expense").observe(getViewLifecycleOwner(), expense -> {
            currentExpense = expense != null ? expense : 0.0;
            expenseText.setText(String.format("$ %.2f", currentExpense));
            homeViewModel.updateBudget(currentIncome, currentExpense);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
