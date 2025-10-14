package com.example.smartfinance.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.smartfinance.BuildConfig;
import com.example.smartfinance.data.model.ApiModels;
import com.example.smartfinance.data.remote.network.FynixApiService;
import com.example.smartfinance.data.remote.network.RetrofitClient;
import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.data.model.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FynixApiHelper {
    private static final String TAG = "FynixApiHelper";
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static String currentSessionId = UUID.randomUUID().toString();

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    // Convert local Transaction to API Transaction
    private static ApiModels.Transaction convertToApiTransaction(Transaction localTransaction) {
        return new ApiModels.Transaction(
                localTransaction.id,
                localTransaction.type,
                localTransaction.category,
                localTransaction.amount,
                localTransaction.date,
                localTransaction.paymentMethod,
                localTransaction.note,
                localTransaction.timestamp
        );
    }

    // Build comprehensive financial prompt
    private static String buildFinancialPrompt(String userMessage, List<Transaction> transactions) {
        StringBuilder prompt = new StringBuilder();

        // ===== NEW & IMPORTANT: ADD THESE LINES FOR BREVITY =====
        prompt.append("You are Fynix, a helpful financial AI assistant for a mobile app. ")
                .append("Provide clear, concise, and actionable financial advice based on the user's transaction data. ")
                .append("**CRITICAL INSTRUCTIONS:**\n")
                .append("1. Keep responses SHORT and DIRECT - aim for 2-3 sentences maximum.\n")
                .append("2. Focus on key insights and practical advice.\n")
                .append("3. Avoid long introductions, explanations, or disclaimers.\n")
                .append("4. Use bullet points only if absolutely necessary.\n\n");

        // ===== END OF NEW LINES =====
        if (transactions != null && !transactions.isEmpty()) {
            prompt.append("USER'S TRANSACTIONS:\n")
                    .append("====================\n");

            double totalIncome = 0;
            double totalExpense = 0;

            for (Transaction transaction : transactions) {
                if ("Income".equalsIgnoreCase(transaction.type)) {
                    totalIncome += transaction.amount;
                } else if ("Expense".equalsIgnoreCase(transaction.type)) {
                    totalExpense += transaction.amount;
                }

                prompt.append(String.format("- %s: %s $%.2f on %s\n",
                        transaction.type,
                        transaction.category,
                        transaction.amount,
                        transaction.date
                ));
            }

            prompt.append("\nFINANCIAL SUMMARY:\n")
                    .append("=================\n")
                    .append(String.format("Total Income: $%.2f\n", totalIncome))
                    .append(String.format("Total Expenses: $%.2f\n", totalExpense))
                    .append(String.format("Net Balance: $%.2f\n\n", totalIncome - totalExpense));
        }

        prompt.append("USER QUESTION: ")
                .append(userMessage)
                .append("\n\n")
                .append("Please provide helpful financial advice.");

        return prompt.toString();
    }

    // Send message to Gemini AI
    public static void sendMessage(Context context, String userMessage, String userId, ApiCallback<String> callback) {
        executor.execute(() -> {
            try {
                // Get transactions for context
                List<Transaction> localTransactions = AppDatabase.getDatabase(context)
                        .transactionDao()
                        .getAllTransactionsSync();

                // Build the prompt with financial context
                String promptText = buildFinancialPrompt(userMessage, localTransactions);

                Log.d(TAG, "Prompt: " + promptText);

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    try {
                        String apiKey = BuildConfig.GEMINI_API_KEY;

                        Log.d(TAG, "Loaded Gemini API Key: " + apiKey);

                        // Build Gemini request
                        ApiModels.Part part = new ApiModels.Part(promptText);
                        ApiModels.Content content = new ApiModels.Content(List.of(part), "user");

                        ApiModels.GenerationConfig config = new ApiModels.GenerationConfig();
                        config.temperature = 0.7;
                        config.maxOutputTokens = 1024;

                        ApiModels.GeminiRequest geminiRequest = new ApiModels.GeminiRequest(
                                List.of(content),
                                config
                        );

                        FynixApiService apiService = RetrofitClient.getApiService(context);
                        Call<ApiModels.GeminiResponse> call = apiService.chatWithGemini(geminiRequest);

                        Log.d(TAG, "Sending request to Gemini API");

                        call.enqueue(new Callback<ApiModels.GeminiResponse>() {
                            @Override
                            public void onResponse(Call<ApiModels.GeminiResponse> call, Response<ApiModels.GeminiResponse> response) {
                                Log.d(TAG, "Response received: " + response.code());

                                if (response.isSuccessful()) {
                                    ApiModels.GeminiResponse body = response.body();
                                    if (body != null) {
                                        String responseText = body.getResponseText();
                                        Log.d(TAG, "AI Response: " + responseText);
                                        callback.onSuccess(responseText);
                                    } else {
                                        Log.e(TAG, "Empty response body");
                                        callback.onError("Received empty response from AI assistant");
                                    }
                                } else {
                                    String errorMsg = "API Error: " + response.code();
                                    Log.e(TAG, errorMsg);

                                    try {
                                        if (response.errorBody() != null) {
                                            String errorBody = response.errorBody().string();
                                            Log.e(TAG, "Error body: " + errorBody);
                                            errorMsg += " - " + errorBody;
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error reading error body", e);
                                    }

                                    // Specific error handling
                                    if (response.code() == 400) {
                                        errorMsg = "Invalid request. Please check your API configuration.";
                                    } else if (response.code() == 401) {
                                        errorMsg = "Invalid API key. Please check your Gemini API key.";
                                    } else if (response.code() == 403) {
                                        errorMsg = "Access denied. Check your API key permissions.";
                                    } else if (response.code() == 404) {
                                        errorMsg = "API endpoint not found. Please check the base URL.";
                                    } else if (response.code() == 429) {
                                        errorMsg = "Rate limit exceeded. Please try again later.";
                                    } else if (response.code() == 500) {
                                        errorMsg = "Server error. Please try again later.";
                                    }

                                    callback.onError(errorMsg);
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiModels.GeminiResponse> call, Throwable t) {
                                Log.e(TAG, "API call failed", t);
                                String errorMsg = "Network error: " + t.getMessage();

                                if (t instanceof java.net.UnknownHostException) {
                                    errorMsg = "Cannot connect to server. Check your internet connection.";
                                } else if (t instanceof javax.net.ssl.SSLHandshakeException) {
                                    errorMsg = "Security connection error. Please try again.";
                                } else if (t instanceof java.net.SocketTimeoutException) {
                                    errorMsg = "Request timed out. Please try again.";
                                }

                                callback.onError(errorMsg);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to setup API call", e);
                        callback.onError("Failed to initialize AI assistant: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error processing message", e);
                callback.onError("Error processing your request: " + e.getMessage());
            }
        });
    }

    // Upload transactions
    public static void uploadTransactions(Context context, ApiCallback<String> callback) {
        executor.execute(() -> {
            try {
                List<Transaction> transactions = AppDatabase.getDatabase(context)
                        .transactionDao()
                        .getAllTransactionsSync();

                if (transactions == null || transactions.isEmpty()) {
                    callback.onSuccess("No transactions found. You can still ask general financial questions.");
                    return;
                }

                int count = transactions.size();
                callback.onSuccess("Ready! " + count + " transactions loaded for financial analysis.");
            } catch (Exception e) {
                Log.e(TAG, "Error loading transactions", e);
                callback.onError("Error loading transactions: " + e.getMessage());
            }
        });
    }

    // Test API connection
    public static String generateUserId() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String getCurrentSessionId() {
        return currentSessionId;
    }

    public static void resetSession() {
        currentSessionId = UUID.randomUUID().toString();
    }
}