package com.example.smartfinance.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartfinance.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

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
                homeViewModel.addIncome(amount, note); // Implement this method in ViewModel
                Toast.makeText(getContext(), "Income added: $" + amount, Toast.LENGTH_SHORT).show();
            });
            bottomSheet.show(getChildFragmentManager(), "AddIncomeBottomSheet");
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
