package com.example.smartfinance.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.viewmodels.FynixViewModel;
import com.example.smartfinance.ui.adapters.ChatAdapter;
import com.google.android.material.snackbar.Snackbar;

public class FynixFragment extends Fragment {
    private FynixViewModel viewModel;
    private RecyclerView messagesRecyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private ProgressBar uploadProgress;
    private TextView uploadStatus;
    private View loadingContainer;
    private ChatAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fynix, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FynixViewModel.class);
        initializeViews(view);
        setupRecyclerView();
        setupObservers();
        setupListeners();

        // Auto-upload transactions when fragment opens
        viewModel.uploadTransactions(requireContext());
    }

    private void initializeViews(View view) {
        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        uploadProgress = view.findViewById(R.id.uploadProgress);
        uploadStatus = view.findViewById(R.id.uploadStatus);
        loadingContainer = view.findViewById(R.id.loadingContainer);
    }

    private void setupRecyclerView() {
        adapter = new ChatAdapter();
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        messagesRecyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            if (!messages.isEmpty()) {
                messagesRecyclerView.smoothScrollToPosition(messages.size() - 1);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            sendButton.setEnabled(!isLoading);
            loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            sendButton.setText(isLoading ? "..." : "Send");
        });

        viewModel.getIsUploading().observe(getViewLifecycleOwner(), isUploading -> {
            uploadProgress.setVisibility(isUploading ? View.VISIBLE : View.GONE);
        });

        viewModel.getUploadStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null && !status.isEmpty()) {
                uploadStatus.setVisibility(View.VISIBLE);
                uploadStatus.setText(status);
                uploadStatus.postDelayed(() -> uploadStatus.setVisibility(View.GONE), 3000);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        sendButton.setOnClickListener(v -> sendMessage());

        messageEditText.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (!message.isEmpty()) {
            viewModel.sendMessage(requireContext(), message);
            messageEditText.setText("");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to avoid memory leaks
        messagesRecyclerView.setAdapter(null);
    }
}