package com.example.smartfinance.ui.home;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
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
import com.example.smartfinance.ui.home.Transactions.AddExpenseBottomSheet;
import com.example.smartfinance.ui.home.Transactions.AddIncomeBottomSheet;
import com.example.smartfinance.ui.home.Transactions.TransactionAdapter;
import com.example.smartfinance.ui.home.model.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * HomeFragment - Main screen for showing budget, income, expenses,
 * and recent transactions.
 */
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private double currentIncome = 0.0;
    private double currentExpense = 0.0;
    private NumberFormat currencyFormat;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        Application application = (Application) requireContext().getApplicationContext();
        homeViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(application))
                .get(HomeViewModel.class);

        setupTransactionButtons();
        setupRecyclerView();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupFabMenu(view);
        observeViewModels();
        observeSyncStatus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupFabMenu(View view) {
        FloatingActionButton fabMenu = view.findViewById(R.id.fabMenu);
        FloatingActionButton fabAddIncome = view.findViewById(R.id.btnAddIncome);
        FloatingActionButton fabAddExpense = view.findViewById(R.id.btnAddExpense);

        final boolean[] isFabMenuOpen = {false};

        fabMenu.setOnClickListener(v -> {
            if (!isFabMenuOpen[0]) {
                showFab(fabAddIncome);
                showFab(fabAddExpense);
                fabMenu.animate().rotation(45f).setDuration(200).start();
            } else {
                hideFab(fabAddIncome);
                hideFab(fabAddExpense);
                fabMenu.animate().rotation(0f).setDuration(200).start();
            }
            isFabMenuOpen[0] = !isFabMenuOpen[0];
        });
    }

    private void showFab(FloatingActionButton fab) {
        fab.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);
        fab.setTranslationY(100f);
        fab.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(200)
                .start();
    }

    private void hideFab(FloatingActionButton fab) {
        fab.animate()
                .alpha(0f)
                .translationY(100f)
                .setDuration(200)
                .withEndAction(() -> fab.setVisibility(View.GONE))
                .start();
    }

    private void setupTransactionButtons() {
        binding.btnAddIncome.setOnClickListener(v -> {
            AddIncomeBottomSheet bottomSheet = new AddIncomeBottomSheet();
            bottomSheet.setIncomeListener(new AddIncomeBottomSheet.IncomeListener() {
                @Override
                public void onIncomeAdded(double amount, String note, String category,
                                          String paymentMethod, String date, long timestamp) {
                    Toast.makeText(getContext(), "Income added: " + currencyFormat.format(amount), Toast.LENGTH_SHORT).show();

                    Transaction transaction = new Transaction(
                            "Income", category, amount, date, paymentMethod, note, timestamp
                    );
                    homeViewModel.insertTransaction(transaction);
                }
            });
            bottomSheet.show(getChildFragmentManager(), "AddIncomeBottomSheet");
        });

        binding.btnAddExpense.setOnClickListener(v -> {
            AddExpenseBottomSheet bottomSheet = new AddExpenseBottomSheet();
            bottomSheet.setExpenseListener(new AddExpenseBottomSheet.ExpenseListener() {
                @Override
                public void onExpenseAdded(double amount, String note, String category,
                                           String paymentMethod, String date, long timestamp) {
                    Toast.makeText(getContext(), "Expense added: " + currencyFormat.format(amount), Toast.LENGTH_SHORT).show();

                    Transaction transaction = new Transaction(
                            "Expense", category, amount, date, paymentMethod, note, timestamp
                    );
                    homeViewModel.insertTransaction(transaction);
                }
            });
            bottomSheet.show(getChildFragmentManager(), "AddExpenseBottomSheet");
        });
    }

    private void setupRecyclerView() {
        TransactionAdapter transactionAdapter = new TransactionAdapter(new ArrayList<>(), getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(transactionAdapter);

        homeViewModel.getAllTransactions().observe(getViewLifecycleOwner(),
                transactionAdapter::setTransactions);
    }

    private void observeViewModels() {
        homeViewModel.getTotalBudget().observe(getViewLifecycleOwner(), value ->
                binding.totalBalance.setText(currencyFormat.format(value != null ? value : 0.0)));

        TextView incomeText = binding.incomeAmount;
        homeViewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            currentIncome = income != null ? income : 0.0;
            incomeText.setText(currencyFormat.format(currentIncome));
        });

        TextView expenseText = binding.expenseAmount;
        homeViewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            currentExpense = expense != null ? expense : 0.0;
            expenseText.setText(currencyFormat.format(currentExpense));
        });

        homeViewModel.getSavings().observe(getViewLifecycleOwner(), savings -> {
            if (savings != null) {
                // Update savings UI if you have it
            }
        });
    }

    private void observeSyncStatus() {
        homeViewModel.getSyncStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
                if (status.contains("Error")) {
                    Log.e("HomeFragment", "Sync error: " + status);
                }
            }
        });
    }
}