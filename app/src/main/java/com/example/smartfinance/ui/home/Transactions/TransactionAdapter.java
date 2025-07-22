package com.example.smartfinance.ui.home.Transactions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.home.income.Transaction;

import java.util.List;



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
        double amount = transactions.get(position).getAmount();
        holder.amountText.setText(String.valueOf(amount));
        holder.amountText.setText(String.format("$%.2f", amount));
        holder.typeText.setText(transactions.get(position).getType());
        holder.noteText.setText(transactions.get(position).getNote());

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
