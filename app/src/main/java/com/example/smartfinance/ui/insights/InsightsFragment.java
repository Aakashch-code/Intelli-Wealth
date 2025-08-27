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

import com.example.smartfinance.R;
import com.example.smartfinance.ui.home.income.AppDatabase;
import com.example.smartfinance.ui.home.income.MonthlyTotal;
import com.example.smartfinance.ui.home.income.TransactionDao;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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

    public static InsightsFragment newInstance() {
        return new InsightsFragment();
    }

    private void setupPieChart(PieChart pieChart, float income, float expense) {
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
        legend.setWordWrapEnabled(true); // Prevent overlap
        legend.setDrawInside(false);
        legend.setCustom(legendEntries);

        pieChart.invalidate(); // Refresh the chart
    }

    private void setupLineChart(LineChart chart, List<MonthlyTotal> incomeList) {
        List<Entry> incomeEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

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
        incomeSet.setDrawValues(false); // Hide values for cleaner look
        incomeSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth curves
        incomeSet.setCubicIntensity(0.2f);

        // Add fill with color resource
        incomeSet.setDrawFilled(true);
        incomeSet.setFillColor(Color.argb(100, 76, 175, 80)); // Semi-transparent green fill

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
        xAxis.setLabelRotationAngle(45f); // Rotate labels
        xAxis.setAvoidFirstLastClipping(true);

        // Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.argb(50, 255, 255, 255)); // Semi-transparent grid
        leftAxis.setAxisLineColor(Color.WHITE);

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
        chart.setBackgroundColor(Color.rgb(30, 30, 30)); // Dark background
        chart.setBorderColor(Color.WHITE);
        chart.setBorderWidth(1f);

        // Enable touch interactions
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        // Add animations
        chart.animateXY(1000, 1000, Easing.EaseInOutQuad);

        chart.invalidate(); // Refresh chart
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TransactionDao dao = AppDatabase.getDatabase(requireContext()).transactionDao();

        PieChart pieChart = view.findViewById(R.id.expenseChart);
        LineChart lineChart = view.findViewById(R.id.incomeChart);

        dao.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            dao.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
                setupPieChart(pieChart,
                        income == null ? 0f : income,
                        expense == null ? 0f : expense
                );
            });
        });

        dao.getMonthlyTotals("Income").observe(getViewLifecycleOwner(), incomeList -> {
            setupLineChart(lineChart, incomeList);
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
        // TODO: Use the ViewModel
    }

}