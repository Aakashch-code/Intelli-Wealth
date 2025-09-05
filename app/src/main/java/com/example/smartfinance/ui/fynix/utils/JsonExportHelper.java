package com.example.smartfinance.ui.fynix.utils;

import android.content.Context;
import android.util.Log;

import com.example.smartfinance.ui.home.data.AppDatabase;
import com.example.smartfinance.ui.home.model.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class JsonExportHelper {
    private static final String TAG = "JsonExportHelper";
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public interface ExportCallback {
        void onExportSuccess(String filePath);
        void onExportError(String errorMessage);
    }

    public static void exportTransactionsToJson(Context context, ExportCallback callback) {
        executor.execute(() -> {
            try {
                AppDatabase database = AppDatabase.getDatabase(context);
                List<Transaction> transactions = database.transactionDao().getAllTransactionsSync();

                if (transactions == null) {
                    callback.onExportError("Database query returned null");
                    return;
                }

                if (transactions.isEmpty()) {
                    callback.onExportError("No transactions found to export");
                    return;
                }

                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .create();

                Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();
                String json = gson.toJson(transactions, transactionListType);

                String fileName = "transactions_export_" + System.currentTimeMillis() + ".json";
                File exportDir = new File(context.getExternalFilesDir(null), "exports");

                if (!exportDir.exists() && !exportDir.mkdirs()) {
                    callback.onExportError("Failed to create export directory");
                    return;
                }

                File exportFile = new File(exportDir, fileName);

                try (FileOutputStream fos = new FileOutputStream(exportFile);
                     OutputStreamWriter osw = new OutputStreamWriter(fos)) {
                    osw.write(json);
                }

                callback.onExportSuccess(exportFile.getAbsolutePath());
            } catch (Exception e) {
                Log.e(TAG, "Error exporting transactions: ", e);
                callback.onExportError("Export failed: " + e.getMessage());
            }
        });
    }

    public static String getExportDirectoryPath(Context context) {
        File exportDir = new File(context.getExternalFilesDir(null), "exports");
        return exportDir.getAbsolutePath();
    }

    public static boolean ensureExportDirectory(Context context) {
        File exportDir = new File(context.getExternalFilesDir(null), "exports");
        return exportDir.exists() || exportDir.mkdirs();
    }
}