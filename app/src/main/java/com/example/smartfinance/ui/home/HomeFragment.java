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

/**
 * HomeFragment - Main screen for showing budget, income, expenses,
 * and recent transactions.
 */
public class HomeFragment extends Fragment {

    // ----------------- UI & Data Members -----------------
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private double currentIncome = 0.0;
    private double currentExpense = 0.0;

    // ----------------- Constructor -----------------
    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    // ----------------- Lifecycle Methods -----------------
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupFabMenu(view);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout with ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModels
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        TransactionViewModel transactionViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(TransactionViewModel.class);

        // Observe LiveData (Budget, Income, Expense)
        observeViewModels(homeViewModel, transactionViewModel);

        // Setup Add Income & Expense Buttons
        setupTransactionButtons(transactionViewModel);

        // Setup RecyclerView for Transactions
        setupRecyclerView(transactionViewModel);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ----------------- FAB Menu (Main + Mini FABs) -----------------
    private void setupFabMenu(View view) {
        FloatingActionButton fabMenu = view.findViewById(R.id.fabMenu);
        FloatingActionButton fabAddIncome = view.findViewById(R.id.btnAddIncome);
        FloatingActionButton fabAddExpense = view.findViewById(R.id.btnAddExpense);
        FloatingActionButton fabAddBudget = view.findViewById(R.id.btnAddBudget);

        final boolean[] isFabMenuOpen = {false};

        fabMenu.setOnClickListener(v -> {
            if (!isFabMenuOpen[0]) {
                showFab(fabAddIncome);
                showFab(fabAddExpense);
                showFab(fabAddBudget);
                fabMenu.animate().rotation(45f).setDuration(200).start(); // Rotate open
            } else {
                hideFab(fabAddIncome);
                hideFab(fabAddExpense);
                hideFab(fabAddBudget);
                fabMenu.animate().rotation(0f).setDuration(200).start(); // Rotate close
            }
            isFabMenuOpen[0] = !isFabMenuOpen[0];
        });
    }

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

    // ----------------- Transaction Buttons -----------------
    private void setupTransactionButtons(TransactionViewModel transactionViewModel) {
        // Add Income
        binding.btnAddIncome.setOnClickListener(v -> {
            AddIncomeBottomSheet bottomSheet = new AddIncomeBottomSheet();
            bottomSheet.setIncomeListener(new AddIncomeBottomSheet.IncomeListener() {
                @Override
                public void onIncomeAdded(double amount, String note, String category, String paymentMethod, String date, long timestamp) {
                    Toast.makeText(getContext(), "Income added: $" + amount, Toast.LENGTH_SHORT).show();

                    // Create transaction with all fields
                    Transaction transaction = new Transaction(
                            "Income",
                            category,
                            amount,
                            date,
                            paymentMethod,
                            note,
                            timestamp
                    );

                    transactionViewModel.insert(transaction);
                }
            });
            bottomSheet.show(getChildFragmentManager(), "AddIncomeBottomSheet");
        });

        // Add Expense
        binding.btnAddExpense.setOnClickListener(v -> {
            AddExpenseBottomSheet bottomSheet = new AddExpenseBottomSheet();
            bottomSheet.setExpenseListener(new AddExpenseBottomSheet.ExpenseListener() {
                @Override
                public void onExpenseAdded(double amount, String note, String category, String paymentMethod, String date, long timestamp) {
                    Toast.makeText(getContext(), "Expense added: $" + amount, Toast.LENGTH_SHORT).show();

                    // Create transaction with all fields
                    Transaction transaction = new Transaction(
                            "Expense",
                            category,
                            amount,
                            date,
                            paymentMethod,
                            note,
                            timestamp
                    );

                    transactionViewModel.insert(transaction);
                }
            });
            bottomSheet.show(getChildFragmentManager(), "AddExpenseBottomSheet");
        });
    }

    // ----------------- RecyclerView Setup -----------------
    private void setupRecyclerView(TransactionViewModel transactionViewModel) {
        TransactionAdapter transactionAdapter = new TransactionAdapter(new ArrayList<>(), getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(transactionAdapter);

        // Observe DB changes -> Update list
        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(),
                transactionAdapter::setTransactions);
    }

    // ----------------- ViewModel Observers -----------------
    private void observeViewModels(HomeViewModel homeViewModel, TransactionViewModel transactionViewModel) {
        // Budget
        homeViewModel.getBudget().observe(getViewLifecycleOwner(),
                value -> binding.totalBalance.setText(String.format("$ %.2f", value)));

        // Income
        TextView incomeText = binding.incomeAmount;
        transactionViewModel.getTotalByType("Income").observe(getViewLifecycleOwner(), income -> {
            currentIncome = income != null ? income : 0.0;
            incomeText.setText(String.format("$ %.2f", currentIncome));
            homeViewModel.updateBudget(currentIncome, currentExpense);
        });

        // Expense
        TextView expenseText = binding.expenseAmount;
        transactionViewModel.getTotalByType("Expense").observe(getViewLifecycleOwner(), expense -> {
            currentExpense = expense != null ? expense : 0.0;
            expenseText.setText(String.format("$ %.2f", currentExpense));
            homeViewModel.updateBudget(currentIncome, currentExpense);
        });
    }
}