package com.example.smartfinance.ui.insights;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartfinance.R;
import com.example.smartfinance.ui.home.income.AppDatabase;
import com.example.smartfinance.ui.home.income.MonthlyTotal;
import com.example.smartfinance.ui.home.income.TransactionDao;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;

public class InsightsFragment extends Fragment {

    private InsightsViewModel mViewModel;
    private TextView incomeSummaryText;
    private TextView expenseSummaryText;

    public static InsightsFragment newInstance() {
        return new InsightsFragment();
    }

    private void setupPieChart(PieChart pieChart, float income, float expense) {
        // Clear any existing data
        pieChart.clear();

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        List<LegendEntry> legendEntries = new ArrayList<>();

        // Add Income
        if (income > 0) {
            entries.add(new PieEntry(income, "Income"));
            colors.add(Color.parseColor("#4CAF50")); // Green
            legendEntries.add(new LegendEntry("Income", Legend.LegendForm.CIRCLE, 12f, 12f, null, Color.parseColor("#4CAF50")));
        }

        // Add Expense
        if (expense > 0) {
            entries.add(new PieEntry(expense, "Expense"));
            colors.add(Color.parseColor("#FF5722")); // Orange
            legendEntries.add(new LegendEntry("Expense", Legend.LegendForm.CIRCLE, 12f, 12f, null, Color.parseColor("#FF5722")));
        }

        // If no data, show a message
        if (entries.isEmpty()) {
            pieChart.setNoDataText("No chart data available.");
            pieChart.setNoDataTextColor(Color.WHITE);
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(16f);
        dataSet.setDrawValues(true);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));
        dataSet.setFormSize(12f);
        dataSet.setFormLineWidth(2f);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.argb(100, 0, 0, 0));
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setHoleRadius(60f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setEntryLabelColor(Color.TRANSPARENT);
        pieChart.setCenterText("Income vs Expense");
        pieChart.setCenterTextColor(Color.argb(200, 255, 255, 255));
        pieChart.setCenterTextSize(18f);
        pieChart.setRotationEnabled(true);
        pieChart.animateXY(1200, 1200, Easing.EaseInOutQuad);
        pieChart.setExtraOffsets(10f, 10f, 10f, 10f);

        // Configure Legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(14f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(12f);
        legend.setXEntrySpace(20f);
        legend.setYEntrySpace(10f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
        legend.setCustom(legendEntries);

        pieChart.invalidate();
    }

    private void setupIncomeLineChart(LineChart chart, List<MonthlyTotal> incomeList) {
        // Clear any existing data
        chart.clear();

        List<Entry> incomeEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // If no data, show a message
        if (incomeList == null || incomeList.isEmpty()) {
            chart.setNoDataText("No chart data available.");
            chart.setNoDataTextColor(Color.WHITE);
            chart.invalidate();
            return;
        }

        // Create map for income
        Map<String, Float> incomeMap = new HashMap<>();
        for (MonthlyTotal mt : incomeList) incomeMap.put(mt.month, mt.total);

        // Sort months
        Set<String> allMonths = new TreeSet<>(incomeMap.keySet());

        // Populate entries
        int index = 0;
        for (String month : allMonths) {
            labels.add(month);
            float income = incomeMap.getOrDefault(month, 0f);
            incomeEntries.add(new Entry(index, income));
            index++;
        }

        // Configure income dataset
        LineDataSet incomeSet = new LineDataSet(incomeEntries, "Income");
        incomeSet.setColor(Color.rgb(76, 175, 80)); // Material Green
        incomeSet.setCircleColor(Color.rgb(76, 175, 80));
        incomeSet.setLineWidth(2.5f);
        incomeSet.setCircleRadius(4f);
        incomeSet.setDrawCircleHole(true);
        incomeSet.setCircleHoleColor(Color.WHITE);
        incomeSet.setValueTextSize(10f);
        incomeSet.setValueTextColor(Color.WHITE);
        incomeSet.setHighLightColor(Color.rgb(255, 255, 255));
        incomeSet.setDrawValues(false);
        incomeSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        incomeSet.setCubicIntensity(0.2f);

        // Add fill with color resource
        incomeSet.setDrawFilled(true);
        incomeSet.setFillColor(Color.argb(100, 76, 175, 80));

        // Set data
        LineData lineData = new LineData(incomeSet);
        chart.setData(lineData);

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(45f);
        xAxis.setAvoidFirstLastClipping(true);

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.argb(50, 255, 255, 255));
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "$" + Math.round(value);
            }
        });

        chart.getAxisRight().setEnabled(false);

        // Chart Styling
        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setTextSize(12f);
        chart.getLegend().setForm(Legend.LegendForm.LINE);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        // Additional chart settings
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.rgb(30, 30, 30));
        chart.setBorderColor(Color.WHITE);
        chart.setBorderWidth(1f);

        // Enable touch interactions
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        // Add animations
        chart.animateXY(1000, 1000, Easing.EaseInOutQuad);

        chart.invalidate();
    }

    private void setupSavingsChart(LineChart chart, List<MonthlyTotal> incomeList, List<MonthlyTotal> expenseList) {
        // Clear any existing data
        chart.clear();

        List<Entry> savingsEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // If no data, show a message
        if ((incomeList == null || incomeList.isEmpty()) && (expenseList == null || expenseList.isEmpty())) {
            chart.setNoDataText("No chart data available.");
            chart.setNoDataTextColor(Color.WHITE);
            chart.invalidate();
            return;
        }

        // Create maps for income and expenses
        Map<String, Float> incomeMap = new HashMap<>();
        if (incomeList != null) {
            for (MonthlyTotal mt : incomeList) incomeMap.put(mt.month, mt.total);
        }

        Map<String, Float> expenseMap = new HashMap<>();
        if (expenseList != null) {
            for (MonthlyTotal mt : expenseList) expenseMap.put(mt.month, mt.total);
        }

        // Sort months
        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(incomeMap.keySet());
        allMonths.addAll(expenseMap.keySet());

        // Populate entries
        int index = 0;
        for (String month : allMonths) {
            labels.add(month);
            float income = incomeMap.getOrDefault(month, 0f);
            float expense = expenseMap.getOrDefault(month, 0f);
            float savings = income - expense;
            savingsEntries.add(new Entry(index, savings));
            index++;
        }

        // Configure savings dataset
        LineDataSet savingsSet = new LineDataSet(savingsEntries, "Savings");
        savingsSet.setColor(Color.rgb(33, 150, 243)); // Material Blue
        savingsSet.setCircleColor(Color.rgb(33, 150, 243));
        savingsSet.setLineWidth(2.5f);
        savingsSet.setCircleRadius(4f);
        savingsSet.setDrawCircleHole(true);
        savingsSet.setCircleHoleColor(Color.WHITE);
        savingsSet.setValueTextSize(10f);
        savingsSet.setValueTextColor(Color.WHITE);
        savingsSet.setHighLightColor(Color.rgb(255, 255, 255));
        savingsSet.setDrawValues(false);
        savingsSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        savingsSet.setCubicIntensity(0.2f);

        // Add fill with color resource
        savingsSet.setDrawFilled(true);
        savingsSet.setFillColor(Color.argb(100, 33, 150, 243));

        // Set data
        LineData lineData = new LineData(savingsSet);
        chart.setData(lineData);

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(45f);
        xAxis.setAvoidFirstLastClipping(true);

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.argb(50, 255, 255, 255));
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "$" + Math.round(value);
            }
        });

        chart.getAxisRight().setEnabled(false);

        // Chart Styling
        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setTextSize(12f);
        chart.getLegend().setForm(Legend.LegendForm.LINE);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        // Additional chart settings
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.rgb(45, 45, 45));
        chart.setBorderColor(Color.WHITE);
        chart.setBorderWidth(1f);

        // Enable touch interactions
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        // Add animations
        chart.animateXY(1000, 1000, Easing.EaseInOutQuad);

        chart.invalidate();
    }

    private void setupComparisonChart(BarChart chart, List<MonthlyTotal> incomeList, List<MonthlyTotal> expenseList) {
        // Clear any existing data
        chart.clear();

        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // If no data, show a message
        if ((incomeList == null || incomeList.isEmpty()) && (expenseList == null || expenseList.isEmpty())) {
            chart.setNoDataText("No chart data available.");
            chart.setNoDataTextColor(Color.WHITE);
            chart.invalidate();
            return;
        }

        // Create maps for income and expenses
        Map<String, Float> incomeMap = new HashMap<>();
        if (incomeList != null) {
            for (MonthlyTotal mt : incomeList) incomeMap.put(mt.month, mt.total);
        }

        Map<String, Float> expenseMap = new HashMap<>();
        if (expenseList != null) {
            for (MonthlyTotal mt : expenseList) expenseMap.put(mt.month, mt.total);
        }

        // Get the two most recent months
        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(incomeMap.keySet());
        allMonths.addAll(expenseMap.keySet());

        List<String> recentMonths = new ArrayList<>(allMonths);
        if (recentMonths.size() > 2) {
            recentMonths = recentMonths.subList(recentMonths.size() - 2, recentMonths.size());
        }

        // Populate entries
        for (int i = 0; i < recentMonths.size(); i++) {
            String month = recentMonths.get(i);
            labels.add(month);

            float income = incomeMap.getOrDefault(month, 0f);
            float expense = expenseMap.getOrDefault(month, 0f);

            incomeEntries.add(new BarEntry(i, income));
            expenseEntries.add(new BarEntry(i, expense));
        }

        // Configure datasets
        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Income");
        incomeSet.setColor(Color.rgb(76, 175, 80));

        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Expense");
        expenseSet.setColor(Color.rgb(255, 87, 34));

        // Set data
        float groupSpace = 0.08f;
        float barSpace = 0.03f;
        float barWidth = 0.4f;

        BarData data = new BarData(incomeSet, expenseSet);
        data.setBarWidth(barWidth);
        chart.setData(data);

        if (recentMonths.size() > 0) {
            chart.groupBars(0, groupSpace, barSpace);
        }

        // X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return index >= 0 && index < labels.size() ? labels.get(index) : "";
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.argb(50, 255, 255, 255));
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "$" + Math.round(value);
            }
        });

        chart.getAxisRight().setEnabled(false);

        // Chart Styling
        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setTextSize(12f);
        chart.getLegend().setForm(Legend.LegendForm.SQUARE);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        // Additional chart settings
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.rgb(45, 45, 45));
        chart.setBorderColor(Color.WHITE);
        chart.setBorderWidth(1f);
        chart.setDrawValueAboveBar(true);

        // Enable touch interactions
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false);

        // Add animations
        chart.animateY(1000, Easing.EaseInOutQuad);

        chart.invalidate();
    }

    private void updateSummaryTexts(float totalIncome, float totalExpense, List<MonthlyTotal> incomeList, List<MonthlyTotal> expenseList) {
        if (incomeSummaryText != null) {
            if (incomeList == null || incomeList.isEmpty()) {
                incomeSummaryText.setText("No income data available.");
            } else {
                // Get current month data (assuming the last entry is the current month)
                MonthlyTotal currentMonth = incomeList.get(incomeList.size() - 1);
                incomeSummaryText.setText(String.format("Total income this month: $%.2f", currentMonth.total));

                // You could add more detailed analysis here
                // For example, find the category with highest income
            }
        }

        if (expenseSummaryText != null) {
            if (expenseList == null || expenseList.isEmpty()) {
                expenseSummaryText.setText("No expense data available.");
            } else {
                // Get current month data
                MonthlyTotal currentMonth = expenseList.get(expenseList.size() - 1);
                expenseSummaryText.setText(String.format("Total expenses this month: $%.2f", currentMonth.total));
            }
        }
    }

    private void updateSuggestions(float income, float expense, List<MonthlyTotal> incomeHistory, List<MonthlyTotal> expenseHistory) {
        View view = getView();
        if (view == null) return;

        TextView suggestionsText = view.findViewById(R.id.suggestionsText);
        if (suggestionsText == null) return;

        StringBuilder suggestions = new StringBuilder();

        // Calculate savings rate
        float savings = income - expense;
        float savingsRate = income > 0 ? (savings / income) * 100 : 0;

        // Basic suggestions based on savings rate
        if (savingsRate < 10) {
            suggestions.append("• Try to save at least 10% of your income\n");
        } else if (savingsRate > 30) {
            suggestions.append("• Great job! You're saving over 30% of your income\n");
        }

        // Compare with previous month if available
        if (incomeHistory != null && expenseHistory != null &&
                incomeHistory.size() >= 2 && expenseHistory.size() >= 2) {
            float currentIncome = incomeHistory.get(incomeHistory.size() - 1).total;
            float previousIncome = incomeHistory.get(incomeHistory.size() - 2).total;
            float currentExpense = expenseHistory.get(expenseHistory.size() - 1).total;
            float previousExpense = expenseHistory.get(expenseHistory.size() - 2).total;

            if (currentExpense > previousExpense * 1.1) {
                suggestions.append("• Your expenses increased by ").append(Math.round((currentExpense/previousExpense - 1) * 100)).append("% compared to last month\n");
            }

            if (currentIncome < previousIncome * 0.9) {
                suggestions.append("• Your income decreased by ").append(Math.round((1 - currentIncome/previousIncome) * 100)).append("% compared to last month\n");
            }
        }

        // If no specific suggestions, provide general advice
        if (suggestions.length() == 0) {
            suggestions.append("• Consider creating a budget for next month\n");
            suggestions.append("• Review your recurring subscriptions\n");
            suggestions.append("• Set aside some money for emergencies\n");
        }

        suggestionsText.setText(suggestions.toString());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize text views
        incomeSummaryText = view.findViewById(R.id.incomeSummaryText);
        expenseSummaryText = view.findViewById(R.id.expenseSummaryText);

        TransactionDao dao = AppDatabase.getDatabase(requireContext()).transactionDao();

        PieChart pieChart = view.findViewById(R.id.expenseChart);
        LineChart incomeChart = view.findViewById(R.id.incomeChart);
        LineChart savingsChart = view.findViewById(R.id.savingsChart);
        BarChart comparisonChart = view.findViewById(R.id.comparisonChart);

        // Set no data text for all charts initially
        pieChart.setNoDataText("No chart data available.");
        pieChart.setNoDataTextColor(Color.WHITE);

        incomeChart.setNoDataText("No chart data available.");
        incomeChart.setNoDataTextColor(Color.WHITE);

        savingsChart.setNoDataText("No chart data available.");
        savingsChart.setNoDataTextColor(Color.WHITE);

        comparisonChart.setNoDataText("No chart data available.");
        comparisonChart.setNoDataTextColor(Color.WHITE);

        dao.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            dao.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
                setupPieChart(pieChart,
                        income == null ? 0f : income,
                        expense == null ? 0f : expense
                );
            });
        });

        dao.getMonthlyTotals("Income").observe(getViewLifecycleOwner(), incomeList -> {
            setupIncomeLineChart(incomeChart, incomeList);

            dao.getMonthlyTotals("Expense").observe(getViewLifecycleOwner(), expenseList -> {
                setupSavingsChart(savingsChart, incomeList, expenseList);
                setupComparisonChart(comparisonChart, incomeList, expenseList);

                // Update summary texts
                float totalIncome = 0;
                float totalExpense = 0;

                if (incomeList != null) {
                    for (MonthlyTotal mt : incomeList) totalIncome += mt.total;
                }

                if (expenseList != null) {
                    for (MonthlyTotal mt : expenseList) totalExpense += mt.total;
                }

                updateSummaryTexts(totalIncome, totalExpense, incomeList, expenseList);
                updateSuggestions(totalIncome, totalExpense, incomeList, expenseList);
            });
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insights, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(InsightsViewModel.class);
    }
}