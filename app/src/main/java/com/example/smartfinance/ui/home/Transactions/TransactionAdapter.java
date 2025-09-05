package com.example.smartfinance.ui.home.Transactions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.home.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

    Context context;
    List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        // Format amount
        double amount = transaction.getAmount();
        holder.amountText.setText(String.format("$%.2f", amount));

        // Set type with proper formatting
        holder.typeText.setText(transaction.getType());

        // Handle note (could be null for old transactions)
        String note = transaction.getNote();
        holder.noteText.setText(note != null ? note : "No description");

        // Handle category (could be null for old transactions)
        String category = transaction.getCategory();
        if (holder.categoryText != null) {
            holder.categoryText.setText(category != null ? category : "Uncategorized");
        }

        // Handle payment method (could be null for old transactions)
        String paymentMethod = transaction.getPaymentMethod();
        if (holder.paymentMethodText != null) {
            holder.paymentMethodText.setText(paymentMethod != null ? paymentMethod : "Not specified");
        }

        // Format date (could be null for old transactions)
        String date = transaction.getDate();
        if (holder.dateText != null) {
            if (date != null) {
                holder.dateText.setText(date);
            } else {
                // Fallback: format from timestamp
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(new Date(transaction.getTimestamp()));
                    holder.dateText.setText(formattedDate);
                } catch (Exception e) {
                    holder.dateText.setText("Unknown date");
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }
}