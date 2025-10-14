package com.example.smartfinance.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.smartfinance.R;
import com.example.smartfinance.data.model.Goal;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<Goal> goals;
    private final OnGoalClickListener listener;
    private final NumberFormat currencyFormat;
    private final SimpleDateFormat dateFormat;

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
        void onGoalLongClick(Goal goal);
    }

    public GoalAdapter(OnGoalClickListener listener, NumberFormat inrFormat) {
        this.listener = listener;
        this.goals = new ArrayList<>();
        this.currencyFormat = inrFormat; // Use the passed INR format
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.bind(goal, currencyFormat, dateFormat);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGoalClick(goal);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onGoalLongClick(goal);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals != null ? goals : new ArrayList<>();
        notifyDataSetChanged();
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvGoalName, tvPriority, tvSavedAmount, tvTargetAmount;
        private final TextView tvProgressPercentage, tvTargetDate;
        private final ProgressBar progressBar;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tvGoalName);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvSavedAmount = itemView.findViewById(R.id.tvSavedAmount);
            tvTargetAmount = itemView.findViewById(R.id.tvTargetAmount);
            tvProgressPercentage = itemView.findViewById(R.id.tvProgressPercentage);
            tvTargetDate = itemView.findViewById(R.id.tvTargetDate);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void bind(Goal goal, NumberFormat currencyFormat, SimpleDateFormat dateFormat) {
            tvGoalName.setText(goal.getGoalName());
            tvPriority.setText(goal.getPriority());

            // Set priority color
            switch (goal.getPriority()) {
                case "HIGH":
                    tvPriority.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                    break;
                case "MEDIUM":
                    tvPriority.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                case "LOW":
                    tvPriority.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                    break;
            }

            // Format amounts using the provided currency format
            tvSavedAmount.setText(currencyFormat.format(goal.getSavedAmount()));
            tvTargetAmount.setText( currencyFormat.format(goal.getTargetAmount()));

            int progress = goal.getProgressPercentage();
            progressBar.setProgress(progress);
            tvProgressPercentage.setText(progress + "%");

            if (goal.getTargetDate() != null) {
                tvTargetDate.setText(dateFormat.format(goal.getTargetDate()));
            } else {
                tvTargetDate.setText("");
            }
        }
    }
}