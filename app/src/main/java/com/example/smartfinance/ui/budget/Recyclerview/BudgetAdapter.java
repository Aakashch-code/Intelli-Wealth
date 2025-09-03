package com.example.smartfinance.ui.budget.Recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;

import java.util.ArrayList;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private final List<Budget> budgetList = new ArrayList<>();

    public BudgetAdapter(List<Budget> budgetList) {
        this.budgetList.addAll(budgetList != null ? budgetList : new ArrayList<>());
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        if (budgetList == null || position >= budgetList.size()) {
            Log.e("BudgetAdapter", "Invalid budget list or position: " + position);
            return;
        }
        Budget budget = budgetList.get(position);
        if (budget == null) {
            Log.e("BudgetAdapter", "Budget object is null at position: " + position);
            return;
        }

        // Set category name
        if (holder.categoryName != null) {
            holder.categoryName.setText(budget.getCategory() != null ? budget.getCategory() : "N/A");
        } else {
            Log.e("BudgetAdapter", "categoryName TextView is null at position: " + position);
        }

        // Format only the allocated amount
        String allocatedAmount = String.format("₹%.2f", budget.getAllocatedAmount());
        if (holder.allocatedAmount != null) {
            holder.allocatedAmount.setText(allocatedAmount);
        } else {
            Log.e("BudgetAdapter", "allocatedAmount TextView is null at position: " + position);
        }

        // Calculate and show progress
        double progressPercentage = 0;
        if (budget.getAllocatedAmount() > 0) {
            progressPercentage = (budget.getSpentAmount() / budget.getAllocatedAmount()) * 100;
        }
        if (holder.progressBar != null) {
            holder.progressBar.setProgress((int) progressPercentage);
        } else {
            Log.e("BudgetAdapter", "progressBar is null at position: " + position);
        }

        if (holder.percentageText != null) {
            holder.percentageText.setText(String.format("%.0f%%", progressPercentage));
        } else {
            Log.e("BudgetAdapter", "percentageText TextView is null at position: " + position);
        }
    }

    public void setBudgets(List<Budget> budgets) {
        budgetList.clear();
        if (budgets != null) {
            budgetList.addAll(budgets);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return budgetList != null ? budgetList.size() : 0;
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, allocatedAmount, percentageText;
        ProgressBar progressBar;
        ImageView icon;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.text_category);
            allocatedAmount = itemView.findViewById(R.id.budgetAmount);
            icon = itemView.findViewById(R.id.image_icon);
        }
    }
}