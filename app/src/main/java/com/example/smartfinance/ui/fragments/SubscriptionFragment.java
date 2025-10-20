// app/src/main/java/com/example/smartfinance/ui/fragments/SubscriptionFragment.java (updated to use Java backend)
package com.example.smartfinance.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.data.model.Subscription;
import com.example.smartfinance.databinding.FragmentSubscriptionBinding;
import com.example.smartfinance.ui.adapters.SubscriptionAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SubscriptionFragment extends Fragment {
    private FragmentSubscriptionBinding binding;
    private SubscriptionViewModel viewModel;
    private SubscriptionAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSubscriptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        setupRecyclerView();
        observeViewModel();
        setupFab();
    }

    private void setupRecyclerView() {
        adapter = new SubscriptionAdapter(subscription -> showDeleteConfirmation(subscription));
        binding.rvSubscriptions.setAdapter(adapter);
        binding.rvSubscriptions.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void observeViewModel() {
        viewModel.getAllActiveSubscriptions().observe(getViewLifecycleOwner(), subscriptions -> {
            adapter.submitList(subscriptions);
            updateStats(subscriptions);
            binding.emptyStateLayout.setVisibility(subscriptions.isEmpty() ? View.VISIBLE : View.GONE);
            binding.rvSubscriptions.setVisibility(subscriptions.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void updateStats(List<Subscription> subscriptions) {
        double totalMonthly = 0;
        for (Subscription s : subscriptions) {
            totalMonthly += s.getMonthlyCost();
        }
        int activeCount = subscriptions.size();
        double totalYearly = totalMonthly * 12;

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        binding.tvTotalCost.setText(currencyFormat.format(totalMonthly));
        binding.tvActiveSubscriptions.setText(String.valueOf(activeCount));
        binding.tvYearlyCost.setText(currencyFormat.format(totalYearly));
    }

    private void setupFab() {
        FloatingActionButton fab = binding.fabAddSubscription;
        fab.setOnClickListener(v -> showAddSubscriptionDialog());
    }

    private void showAddSubscriptionDialog() {
        Dialog dialog = new Dialog(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_subscription, null);
        dialog.setContentView(dialogView);

        TextInputEditText etName = dialogView.findViewById(R.id.etSubscriptionName);
        TextInputEditText etCost = dialogView.findViewById(R.id.etMonthlyCost);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String costStr = etCost.getText().toString().trim();
            if (name.isEmpty() || costStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            double cost;
            try {
                cost = Double.parseDouble(costStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid cost", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cost <= 0) {
                Toast.makeText(getContext(), "Cost must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.insertSubscription(name, cost);
            dialog.dismiss();
            Toast.makeText(getContext(), "Subscription added", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void showDeleteConfirmation(Subscription subscription) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Subscription")
                .setMessage("Are you sure you want to delete " + subscription.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteSubscription(subscription);
                    Toast.makeText(getContext(), "Subscription deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}