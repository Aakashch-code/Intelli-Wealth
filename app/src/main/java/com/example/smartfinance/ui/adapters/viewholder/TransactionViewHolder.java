package com.example.smartfinance.ui.adapters.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;

public class TransactionViewHolder extends RecyclerView.ViewHolder {

    public TextView noteText;
    public TextView dateText;
    public TextView amountText;
    public TextView typeText;
    public TextView categoryText;
    public TextView paymentMethodText;

    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);
        noteText = itemView.findViewById(R.id.noteText);
        dateText = itemView.findViewById(R.id.dateText);
        amountText = itemView.findViewById(R.id.amountText);
        typeText = itemView.findViewById(R.id.typeText);

        // Add these if they exist in your layout
        categoryText = itemView.findViewById(R.id.categoryText);
        paymentMethodText = itemView.findViewById(R.id.paymentMethodText);
    }
}