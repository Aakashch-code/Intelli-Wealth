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
    private List<Transaction> transactions;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentHomeBinding.bind(view);


        // Initialize TransactionViewModel (for Room access)
        TransactionViewModel transactionViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(TransactionViewModel.class);

        // Reference to your income TextView
        TextView incomeText = view.findViewById(R.id.incomeAmount);

        // Observe total income from DB
        transactionViewModel.getTotalByType("Income").observe(getViewLifecycleOwner(), income -> {
            double incomeValue = income != null ? income : 0.0;
            incomeText.setText(String.format("$ %.2f", incomeValue));
        });
    }
    private List<Transaction> transactionList = new ArrayList<>();

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        // Observers for LiveData
        homeViewModel.getBudget().observe(getViewLifecycleOwner(),
                value -> binding.totalBalance.setText("$" + value));

        homeViewModel.getExpenses().observe(getViewLifecycleOwner(),
                value -> binding.expenseAmount.setText("$" + value));

        homeViewModel.getIncome().observe(getViewLifecycleOwner(),
                value -> binding.incomeAmount.setText("$" + value));

        homeViewModel.getSavings().observe(getViewLifecycleOwner(),
                value -> binding.savingsAmount.setText("$" + value));

        // Handle Add Income button click
        binding.btnAddIncome.setOnClickListener(v -> {
            AddIncomeBottomSheet bottomSheet = new AddIncomeBottomSheet();
            bottomSheet.setIncomeListener((amount, note) -> {
                homeViewModel.addIncome(amount, note);
                Toast.makeText(getContext(), "Income added: $" + amount, Toast.LENGTH_SHORT).show();
            });
            bottomSheet.show(getChildFragmentManager(), "AddIncomeBottomSheet");
        });
        binding.btnAddExpense.setOnClickListener(v -> {
            AddExpenseBottomSheet bottomSheet = new AddExpenseBottomSheet();
            bottomSheet.setExpenseListener((amount, note) -> {
                homeViewModel.addExpense(amount, note);
                Toast.makeText(getContext(), "Expense added: $" + amount, Toast.LENGTH_SHORT).show();
            });
            bottomSheet.show(getChildFragmentManager(), "AddExpenseBottomSheet");
        });

        int mincome = 10;
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        List<recentTransactions> transactions = new ArrayList<recentTransactions>();
        transactions.add(new recentTransactions(mincome, "Income", "Salary", System.currentTimeMillis()));
        transactions.add(new recentTransactions(50.0, "Expense", "Groceries", System.currentTimeMillis() - 86400000)); // 1 day ago
        transactions.add(new recentTransactions(200.0, "Income", "Freelancing", System.currentTimeMillis() - 2 * 86400000)); // 2 days ago
        transactions.add(new recentTransactions(75.5, "Expense", "Electricity Bill", System.currentTimeMillis() - 3 * 86400000));
        transactions.add(new recentTransactions(30.0, "Expense", "Mobile Recharge", System.currentTimeMillis() - 4 * 86400000));
        transactions.add(new recentTransactions(150.0, "Income", "Gift", System.currentTimeMillis() - 5 * 86400000));
        transactions.add(new recentTransactions(120.0, "Expense", "Restaurant", System.currentTimeMillis() - 6 * 86400000));
        transactions.add(new recentTransactions(90.0, "Income", "Cashback", System.currentTimeMillis() - 7 * 86400000));
        transactions.add(new recentTransactions(40.0, "Expense", "Travel", System.currentTimeMillis() - 8 * 86400000));
        transactions.add(new recentTransactions(300.0, "Income", "Part-time Job", System.currentTimeMillis() - 9 * 86400000));
        transactions.add(new recentTransactions(60.0, "Expense", "Stationery", System.currentTimeMillis() - 10 * 86400000));


        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new TransactionAdapter(transactions,this.getContext()));

        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
