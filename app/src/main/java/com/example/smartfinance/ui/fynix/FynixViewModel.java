package com.example.smartfinance.ui.fynix;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import com.example.smartfinance.ui.fynix.utils.FynixApiHelper;
import java.util.ArrayList;
import java.util.List;

public class FynixViewModel extends ViewModel {

    private MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isUploading = new MutableLiveData<>(false);
    private MutableLiveData<String> uploadStatus = new MutableLiveData<>("");
    private MutableLiveData<String> error = new MutableLiveData<>("");

    private String userId = FynixApiHelper.generateUserId();

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsUploading() {
        return isUploading;
    }

    public LiveData<String> getUploadStatus() {
        return uploadStatus;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void sendMessage(Context context, String message) {
        if (message == null || message.trim().isEmpty()) {
            error.setValue("Message cannot be empty");
            return;
        }

        addMessage(new ChatMessage(message, true));
        isLoading.setValue(true);

        FynixApiHelper.sendMessage(context, message, userId, new FynixApiHelper.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                isLoading.postValue(false);
                addMessage(new ChatMessage(result, false));
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                error.postValue(errorMsg);
                addMessage(new ChatMessage("Sorry, I encountered an error. Please try again.", false));
            }
        });
    }

    public void uploadTransactions(Context context) {
        isUploading.setValue(true);
        FynixApiHelper.uploadTransactions(context, new FynixApiHelper.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                isUploading.postValue(false);
                uploadStatus.postValue("✅ " + result);
                addMessage(new ChatMessage("✅ " + result + "\nYou can now ask me questions about your finances!", false));
            }

            @Override
            public void onError(String errorMsg) {
                isUploading.postValue(false);
                uploadStatus.postValue("❌ Upload failed");
                error.postValue(errorMsg);
                addMessage(new ChatMessage("❌ Upload failed: " + errorMsg, false));
            }
        });
    }

    private void addMessage(ChatMessage message) {
        List<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages != null) {
            currentMessages.add(message);
            messages.postValue(currentMessages);
        }
    }

    public void clearMessages() {
        messages.setValue(new ArrayList<>());
    }

    public static class ChatMessage {
        public String message;
        public boolean isUser;

        public ChatMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
        }
    }
}