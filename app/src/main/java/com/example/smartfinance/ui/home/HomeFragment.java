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

import com.example.smartfinance.R;
import com.example.smartfinance.databinding.FragmentHomeBinding;
import com.example.smartfinance.ui.home.income.TransactionViewModel;

public class HomeFragment extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize TransactionViewModel (for Room access)
        TransactionViewModel transactionViewModel = new ViewModelProvider(
                requireActivity(),
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(TransactionViewModel.class);

        // Reference to your income TextView
        TextView incomeText = view.findViewById(R.id.incomeAmount);

        // Observe total income from DB
        transactionViewModel.getTotalByType("income").observe(getViewLifecycleOwner(), income -> {
            double incomeValue = income != null ? income : 0.0;
            incomeText.setText(String.format("₹ %.2f", incomeValue));
        });
    }


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

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
