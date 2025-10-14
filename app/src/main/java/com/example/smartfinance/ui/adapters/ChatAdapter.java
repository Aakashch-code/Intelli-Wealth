package com.example.smartfinance.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.viewmodels.FynixViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<FynixViewModel.ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<FynixViewModel.ChatMessage> messages) {
        if (messages != null) {
            this.messages = new ArrayList<>(messages);
            notifyDataSetChanged();
        }
    }

    public void addMessage(FynixViewModel.ChatMessage message) {
        if (message != null) {
            messages.add(message);
            notifyItemInserted(messages.size() - 1);
        }
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        FynixViewModel.ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final View userMessageContainer;
        private final View botMessageContainer;
        private final TextView userMessageText;
        private final TextView botMessageText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageContainer = itemView.findViewById(R.id.userMessageContainer);
            botMessageContainer = itemView.findViewById(R.id.botMessageContainer);
            userMessageText = itemView.findViewById(R.id.userMessageText);
            botMessageText = itemView.findViewById(R.id.botMessageText);
        }

        void bind(FynixViewModel.ChatMessage message) {
            if (message.isUser) {
                userMessageContainer.setVisibility(View.VISIBLE);
                botMessageContainer.setVisibility(View.GONE);
                userMessageText.setText(message.message);
            } else {
                userMessageContainer.setVisibility(View.GONE);
                botMessageContainer.setVisibility(View.VISIBLE);
                botMessageText.setText(message.message);
            }
        }
    }
}