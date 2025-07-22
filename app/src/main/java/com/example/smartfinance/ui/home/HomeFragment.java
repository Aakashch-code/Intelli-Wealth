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
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.databinding.FragmentHomeBinding;
import com.example.smartfinance.ui.home.Transactions.TransactionAdapter;
import com.example.smartfinance.ui.home.Transactions.recentTransactions;
import com.example.smartfinance.ui.home.income.Transaction;
import com.example.smartfinance.ui.home.income.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private double currentIncome = 0.0;
    private double currentExpense = 0.0;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
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

        // Set up RecyclerView with dummy data (replace with live data later)
        setupRecyclerView(root);

        return root;
    }

    private void observeViewModels(HomeViewModel homeViewModel, TransactionViewModel transactionViewModel) {
        // Budget, Expense, Income, and Savings observers
        homeViewModel.getBudget().observe(getViewLifecycleOwner(),
                value -> binding.totalBalance.setText(String.format("$ %.2f", value)));

        homeViewModel.getSavings().observe(getViewLifecycleOwner(),
                value -> binding.savingsAmount.setText("$" + value));

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

    private void setupRecyclerView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        List<recentTransactions> transactions = new ArrayList<>();

        // Dummy data for now — replace with database data later
        transactions.add(new recentTransactions(50.0, "Income", "Salary", System.currentTimeMillis()));
        transactions.add(new recentTransactions(50.0, "Expense", "Groceries", System.currentTimeMillis() - 86400000));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TransactionAdapter(transactions, getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
