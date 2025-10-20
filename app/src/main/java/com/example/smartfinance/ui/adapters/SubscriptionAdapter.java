
package com.example.smartfinance.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.data.model.Subscription;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubscriptionAdapter extends ListAdapter<Subscription, SubscriptionAdapter.SubscriptionViewHolder> {
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Subscription subscription);
    }

    public SubscriptionAdapter(OnDeleteClickListener listener) {
        super(DIFF_CALLBACK);
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false);
        return new SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder holder, int position) {
        Subscription current = getItem(position);
        holder.bind(current);
    }

    public static class SubscriptionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvCost;
        private final TextView tvBillingCycle;
        private final TextView tvNextBilling;
        private final ImageView ivCancel;

        public SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSubscriptionName);
            tvCost = itemView.findViewById(R.id.tvCost);
            tvBillingCycle = itemView.findViewById(R.id.tvBillingCycle);
            tvNextBilling = itemView.findViewById(R.id.tvNextBilling);
            ivCancel = itemView.findViewById(R.id.ivCancel);
        }

        public void bind(Subscription subscription) {
            tvName.setText(subscription.getName());
            tvCost.setText(String.format("₹%.2f", subscription.getMonthlyCost()));
            tvBillingCycle.setText(subscription.getBillingCycle());

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
            tvNextBilling.setText("Next: " + sdf.format(subscription.getNextBillingDate()));

            ivCancel.setOnClickListener(v -> {
                if (subscription != null) {
                    // Assuming listener is accessible; in practice, pass via constructor or use interface
                    // For simplicity, we'll handle in fragment
                }
            });
        }
    }

    private static final DiffUtil.ItemCallback<Subscription> DIFF_CALLBACK = new DiffUtil.ItemCallback<Subscription>() {
        @Override
        public boolean areItemsTheSame(@NonNull Subscription oldItem, @NonNull Subscription newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subscription oldItem, @NonNull Subscription newItem) {
            return oldItem.equals(newItem);
        }
    };
}