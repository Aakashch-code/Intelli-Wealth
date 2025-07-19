package com.example.smartfinance.ui.home.Transactions;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionViewHolder extends RecyclerView.ViewHolder {

    public TextView noteText;
    private TextView dateText;
    public TextView amountText;
    public TextView typeText;

    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);
        noteText = itemView.findViewById(R.id.noteText);
        dateText = itemView.findViewById(R.id.dateText);
        amountText = itemView.findViewById(R.id.amountText);
        typeText = itemView.findViewById(R.id.typeText);
    }

}
