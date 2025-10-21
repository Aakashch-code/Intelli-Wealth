package com.example.smartfinance.ui.viewmodels;

import android.app.Application;
import android.content.ContentResolver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.data.model.Budget;
import com.example.smartfinance.data.model.Goal;
import com.example.smartfinance.data.model.Subscription;
import com.example.smartfinance.data.model.Transaction;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsViewModel extends AndroidViewModel {

    private final AppDatabase database;
    private final MutableLiveData<String> exportPathLiveData = new MutableLiveData<>();

    public ReportsViewModel(@NonNull Application application, @NonNull AppDatabase database) {
        super(application);
        this.database = database;
    }

    public MutableLiveData<String> getExportPathLiveData() {
        return exportPathLiveData;
    }

    public void exportReports(List<String> types, long startTime, long endTime, String format, Uri uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, List<?>> data = fetchReportData(types, startTime, endTime);
                    generateReport(data, format, uri);
                    exportPathLiveData.postValue("success");
                } catch (Exception e) {
                    Log.e("ReportsViewModel", "Export failed", e);
                    exportPathLiveData.postValue(null);
                }
            }
        }).start();
    }

    private Map<String, List<?>> fetchReportData(List<String> types, long startTime, long endTime) {
        Map<String, List<?>> data = new HashMap<>();
        var transactionDao = database.transactionDao();
        var budgetDao = database.budgetDao();
        var goalDao = database.goalDao();
        var subscriptionDao = database.subscriptionDao();

        if (types.contains("income")) {
            List<Transaction> transactions = transactionDao.getTransactionsByTypeAndDateRange("Income", startTime, endTime);
            data.put("Income", transactions);
        }
        if (types.contains("expense")) {
            List<Transaction> transactions = transactionDao.getTransactionsByTypeAndDateRange("Expense", startTime, endTime);
            data.put("Expense", transactions);
        }
        if (types.contains("budget")) {
            List<Budget> budgets = budgetDao.getAllBudgetsSync();
            data.put("Budget", budgets);
        }
        if (types.contains("subscription")) {
            List<Subscription> subscriptions = subscriptionDao.getAllSubscriptionsSync();
            data.put("Subscription", subscriptions);
        }
        if (types.contains("goal")) {
            List<Goal> goals = goalDao.getAllGoalsSync();
            data.put("Goal", goals);
        }
        return data;
    }

    private void generateReport(Map<String, List<?>> data, String format, Uri uri) throws Exception {
        ContentResolver resolver = getApplication().getContentResolver();
        try (OutputStream out = resolver.openOutputStream(uri)) {
            if (out == null) {
                throw new IOException("Failed to open output stream");
            }
            if (format.equals("PDF")) {
                generatePdf(data, out);
            } else {
                generateCsv(data, out);
            }
        }
    }

    private void generatePdf(Map<String, List<?>> data, OutputStream out) throws Exception {
        PdfDocument document = new PdfDocument();
        int pageNum = 1;
        int yPosition = 50;
        PdfDocument.Page page = null;
        Canvas canvas = null;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        for (Map.Entry<String, List<?>> entry : data.entrySet()) {
            String section = entry.getKey();
            if (page == null || yPosition > 800) {
                if (page != null) {
                    document.finishPage(page);
                }
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                yPosition = 50;
                pageNum++;
            }

            canvas.drawText(section + " Report", 40, yPosition, paint);
            yPosition += 30;

            // Draw headers
            String[] headers = getHeadersForSection(section);
            for (int i = 0; i < headers.length; i++) {
                canvas.drawText(headers[i], 40 + (i * 150), yPosition, paint);
            }
            yPosition += 20;

            List<?> items = entry.getValue();
            for (Object item : items) {
                if (yPosition > 800) {
                    document.finishPage(page);
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();
                    yPosition = 50;
                    pageNum++;

                    // Redraw headers on new page
                    for (int i = 0; i < headers.length; i++) {
                        canvas.drawText(headers[i], 40 + (i * 150), yPosition, paint);
                    }
                    yPosition += 20;
                }

                if (item instanceof Transaction) {
                    Transaction t = (Transaction) item;
                    canvas.drawText(formatDate(t.getTimestamp()), 40, yPosition, paint);
                    canvas.drawText(String.valueOf(t.getAmount()), 40 + 150, yPosition, paint);
                    if (headers.length > 2) {
                        canvas.drawText(t.getCategory(), 40 + 300, yPosition, paint);
                    }
                } else if (item instanceof Budget) {
                    Budget b = (Budget) item;
                    canvas.drawText(b.getCategory(), 40, yPosition, paint);
                    canvas.drawText(String.valueOf(b.getAllocatedAmount()), 40 + 150, yPosition, paint);
                } else if (item instanceof Goal) {
                    Goal g = (Goal) item;
                    canvas.drawText(g.getGoalName(), 40, yPosition, paint);
                    canvas.drawText(String.valueOf(g.getTargetAmount()), 40 + 150, yPosition, paint);
                    canvas.drawText(String.valueOf(g.getSavedAmount()), 40 + 300, yPosition, paint);
                } else if (item instanceof Subscription) {
                    Subscription s = (Subscription) item;
                    canvas.drawText(s.getName(), 40, yPosition, paint);
                    canvas.drawText(String.valueOf(s.getMonthlyCost()), 40 + 150, yPosition, paint);
                }
                yPosition += 20;
            }
            yPosition += 30;
        }

        if (page != null) {
            document.finishPage(page);
        }
        document.writeTo(out);
        document.close();
    }

    private void generateCsv(Map<String, List<?>> data, OutputStream out) throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            for (Map.Entry<String, List<?>> entry : data.entrySet()) {
                writer.println(entry.getKey() + " Report");
                String[] headers = getHeadersForSection(entry.getKey());
                writer.println(String.join(",", headers));

                List<?> items = entry.getValue();
                for (Object item : items) {
                    String line = getCsvLine(item);
                    writer.println(line);
                }
                writer.println(); // Separator
            }
        }
    }

    private String[] getHeadersForSection(String section) {
        String[] headers;
        switch (section) {
            case "Income":
            case "Expense":
                headers = new String[]{"Date", "Amount", "Category"};
                break;
            case "Budget":
                headers = new String[]{"Category", "Amount"};
                break;
            case "Subscription":
                headers = new String[]{"Name", "Amount"};
                break;
            case "Goal":
                headers = new String[]{"Name", "Target Amount", "Saved Amount"};
                break;
            default:
                headers = new String[]{"Data"};
                break;
        }
        return headers;
    }

    private String getCsvLine(Object item) {
        if (item instanceof Transaction) {
            Transaction t = (Transaction) item;
            return formatDate(t.getTimestamp()) + "," + t.getAmount() + "," + quoteIfNeeded(t.getCategory());
        } else if (item instanceof Budget) {
            Budget b = (Budget) item;
            return quoteIfNeeded(b.getCategory()) + "," + b.getAllocatedAmount();
        } else if (item instanceof Goal) {
            Goal g = (Goal) item;
            return quoteIfNeeded(g.getGoalName()) + "," + g.getTargetAmount() + "," + g.getSavedAmount();
        } else if (item instanceof Subscription) {
            Subscription s = (Subscription) item;
            return quoteIfNeeded(s.getName()) + "," + s.getMonthlyCost();
        }
        return "";
    }

    private String formatDate(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(timestamp));
    }

    private String quoteIfNeeded(String value) {
        if (value == null) return "";
        return value.contains(",") || value.contains("\"") ? "\"" + value.replace("\"", "\"\"") + "\"" : value;
    }
}