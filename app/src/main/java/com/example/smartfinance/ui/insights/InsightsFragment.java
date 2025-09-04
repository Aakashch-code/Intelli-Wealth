package com.example.smartfinance.ui.insights;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InsightsFragment extends Fragment {

    private InsightsViewModel mViewModel;
    private TextView incomeSummaryText;
    private TextView expenseSummaryText;

    public static InsightsFragment newInstance() {
        return new InsightsFragment();
    }

    // Helper method to convert month string to date for proper sorting
    private Date parseMonthString(String monthString) {
        try {
            // Assuming format is "MMM yyyy" like "Jan 2023"
            SimpleDateFormat format = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
            return format.parse(monthString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(0); // Return epoch date as fallback
        }
    }

    // Helper method to sort months chronologically
    private List<String> sortMonthsChronologically(Set<String> months) {
        List<String> monthList = new ArrayList<>(months);

        // Sort by converting to dates for proper chronological order
        Collections.sort(monthList, new Comparator<String>() {
            @Override
            public int compare(String month1, String month2) {
                Date date1 = parseMonthString(month1);
                Date date2 = parseMonthString(month2);
                return date1.compareTo(date2);
            }
        });

        return monthList;
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

        // Sort months chronologically
        List<String> sortedMonths = sortMonthsChronologically(incomeMap.keySet());

        // Populate entries
        int index = 0;
        for (String month : sortedMonths) {
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

        // Get all unique months and sort them chronologically
        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(incomeMap.keySet());
        allMonths.addAll(expenseMap.keySet());

        List<String> sortedMonths = sortMonthsChronologically(allMonths);

        // Populate entries
        int index = 0;
        for (String month : sortedMonths) {
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

        // If no data, show a message
        if ((incomeList == null || incomeList.isEmpty()) && (expenseList == null || expenseList.isEmpty())) {
            chart.setNoDataText("No chart data available.");
            chart.setNoDataTextColor(Color.WHITE);
            chart.invalidate();
            return;
        }

        // Get all unique months in chronological order
        Set<String> monthSet = new TreeSet<>();
        if (incomeList != null) {
            for (MonthlyTotal mt : incomeList) monthSet.add(mt.month);
        }
        if (expenseList != null) {
            for (MonthlyTotal mt : expenseList) monthSet.add(mt.month);
        }

        List<String> sortedMonths = sortMonthsChronologically(monthSet);

        // Create lists for income, expense, and savings data
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        List<BarEntry> savingsEntries = new ArrayList<>();

        // Prepare data for each month
        for (int i = 0; i < sortedMonths.size(); i++) {
            String month = sortedMonths.get(i);

            // Find income for this month
            float income = 0;
            if (incomeList != null) {
                for (MonthlyTotal mt : incomeList) {
                    if (mt.month.equals(month)) {
                        income = mt.total;
                        break;
                    }
                }
            }

            // Find expense for this month
            float expense = 0;
            if (expenseList != null) {
                for (MonthlyTotal mt : expenseList) {
                    if (mt.month.equals(month)) {
                        expense = mt.total;
                        break;
                    }
                }
            }

            // Calculate savings
            float savings = income - expense;

            // Add entries (x position, value)
            incomeEntries.add(new BarEntry(i, income));
            expenseEntries.add(new BarEntry(i, expense));
            savingsEntries.add(new BarEntry(i, savings));
        }

        // Create datasets
        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.rgb(67, 160, 71)); // Green
        incomeDataSet.setValueTextColor(Color.WHITE);
        incomeDataSet.setValueTextSize(10f);

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expenses");
        expenseDataSet.setColor(Color.rgb(244, 67, 54)); // Red
        expenseDataSet.setValueTextColor(Color.WHITE);
        expenseDataSet.setValueTextSize(10f);

        BarDataSet savingsDataSet = new BarDataSet(savingsEntries, "Savings");
        savingsDataSet.setColor(Color.rgb(33, 150, 243)); // Blue
        savingsDataSet.setValueTextColor(Color.WHITE);
        savingsDataSet.setValueTextSize(10f);

        // Group bars together
        float groupSpace = 0.08f;
        float barSpace = 0.03f;
        float barWidth = 0.25f;

        BarData data = new BarData(incomeDataSet, expenseDataSet, savingsDataSet);
        data.setBarWidth(barWidth);

        // Apply styling
        chart.setData(data);
        chart.groupBars(0, groupSpace, barSpace);
        chart.setFitBars(true);

        // Configure X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(sortedMonths));

        // Configure Y-axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(12f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#3C3C3C"));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // Configure the chart appearance
        chart.setBackgroundColor(Color.rgb(45, 45, 45));
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setBorderColor(Color.LTGRAY);

        // Configure legend
        Legend legend = chart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(12f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        // Enable interactions
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);

        // Add animation
        chart.animateY(1400, Easing.EaseInOutQuad);

        chart.invalidate();
    }


    private void updateSummaryTexts(float totalIncome, float totalExpense, List<MonthlyTotal> incomeList, List<MonthlyTotal> expenseList) {
        if (incomeSummaryText != null) {
            if (incomeList == null || incomeList.isEmpty()) {
                incomeSummaryText.setText("No income data available.");
            } else {
                // Sort months to get the most recent
                List<String> sortedMonths = sortMonthsChronologically(new TreeSet<String>() {{
                    for (MonthlyTotal mt : incomeList) add(mt.month);
                }});

                if (!sortedMonths.isEmpty()) {
                    String currentMonth = sortedMonths.get(sortedMonths.size() - 1);
                    float currentIncome = 0;

                    for (MonthlyTotal mt : incomeList) {
                        if (mt.month.equals(currentMonth)) {
                            currentIncome = mt.total;
                            break;
                        }
                    }

                    incomeSummaryText.setText(String.format("Total income this month: $%.2f", currentIncome));
                } else {
                    incomeSummaryText.setText("No income data available.");
                }
            }
        }

        if (expenseSummaryText != null) {
            if (expenseList == null || expenseList.isEmpty()) {
                expenseSummaryText.setText("No expense data available.");
            } else {
                // Sort months to get the most recent
                List<String> sortedMonths = sortMonthsChronologically(new TreeSet<String>() {{
                    for (MonthlyTotal mt : expenseList) add(mt.month);
                }});

                if (!sortedMonths.isEmpty()) {
                    String currentMonth = sortedMonths.get(sortedMonths.size() - 1);
                    float currentExpense = 0;

                    for (MonthlyTotal mt : expenseList) {
                        if (mt.month.equals(currentMonth)) {
                            currentExpense = mt.total;
                            break;
                        }
                    }

                    expenseSummaryText.setText(String.format("Total expenses this month: $%.2f", currentExpense));
                } else {
                    expenseSummaryText.setText("No expense data available.");
                }
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

            // Sort months chronologically
            Set<String> incomeMonths = new TreeSet<>();
            for (MonthlyTotal mt : incomeHistory) incomeMonths.add(mt.month);
            List<String> sortedIncomeMonths = sortMonthsChronologically(incomeMonths);

            Set<String> expenseMonths = new TreeSet<>();
            for (MonthlyTotal mt : expenseHistory) expenseMonths.add(mt.month);
            List<String> sortedExpenseMonths = sortMonthsChronologically(expenseMonths);

            if (sortedIncomeMonths.size() >= 2 && sortedExpenseMonths.size() >= 2) {
                String currentIncomeMonth = sortedIncomeMonths.get(sortedIncomeMonths.size() - 1);
                String previousIncomeMonth = sortedIncomeMonths.get(sortedIncomeMonths.size() - 2);

                String currentExpenseMonth = sortedExpenseMonths.get(sortedExpenseMonths.size() - 1);
                String previousExpenseMonth = sortedExpenseMonths.get(sortedExpenseMonths.size() - 2);

                float currentIncome = 0, previousIncome = 0;
                float currentExpense = 0, previousExpense = 0;

                for (MonthlyTotal mt : incomeHistory) {
                    if (mt.month.equals(currentIncomeMonth)) currentIncome = mt.total;
                    if (mt.month.equals(previousIncomeMonth)) previousIncome = mt.total;
                }

                for (MonthlyTotal mt : expenseHistory) {
                    if (mt.month.equals(currentExpenseMonth)) currentExpense = mt.total;
                    if (mt.month.equals(previousExpenseMonth)) previousExpense = mt.total;
                }

                if (currentExpense > previousExpense * 1.1 && previousExpense > 0) {
                    suggestions.append("• Your expenses increased by ").append(Math.round((currentExpense/previousExpense - 1) * 100)).append("% compared to last month\n");
                }

                if (currentIncome < previousIncome * 0.9 && previousIncome > 0) {
                    suggestions.append("• Your income decreased by ").append(Math.round((1 - currentIncome/previousIncome) * 100)).append("% compared to last month\n");
                }
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
        BarChart monthlyCommparisonChart = view.findViewById(R.id.comparisonChart);

        // Set no data text for all charts initially
        pieChart.setNoDataText("No chart data available.");
        pieChart.setNoDataTextColor(Color.WHITE);

        incomeChart.setNoDataText("No chart data available.");
        incomeChart.setNoDataTextColor(Color.WHITE);

        savingsChart.setNoDataText("No chart data available.");
        savingsChart.setNoDataTextColor(Color.WHITE);

        monthlyCommparisonChart.setNoDataText("No chart data available.");
        monthlyCommparisonChart.setNoDataTextColor(Color.WHITE);

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
                setupComparisonChart(monthlyCommparisonChart, incomeList, expenseList);

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