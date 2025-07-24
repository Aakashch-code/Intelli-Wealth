package com.example.smartfinance.ui.insights;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

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


    private void setupLineChart(LineChart chart, List<MonthlyTotal> incomeList, List<MonthlyTotal> expenseList) {
        List<Entry> incomeEntries = new ArrayList<>();
        List<Entry> expenseEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Map<String, Float> incomeMap = new HashMap<>();
        for (MonthlyTotal mt : incomeList) incomeMap.put(mt.month, mt.total);

        Map<String, Float> expenseMap = new HashMap<>();
        for (MonthlyTotal mt : expenseList) expenseMap.put(mt.month, mt.total);

        // Combine and sort unique months
        Set<String> allMonths = new TreeSet<>(incomeMap.keySet());
        allMonths.addAll(expenseMap.keySet());

        int index = 0;
        for (String month : allMonths) {
            labels.add(month);
            float income = incomeMap.getOrDefault(month, 0f);
            float expense = expenseMap.getOrDefault(month, 0f);

            incomeEntries.add(new Entry(index, income));
            expenseEntries.add(new Entry(index, expense));
            index++;
        }

        LineDataSet incomeSet = new LineDataSet(incomeEntries, "Income");
        incomeSet.setColor(Color.GREEN);
        incomeSet.setCircleColor(Color.GREEN);
        incomeSet.setLineWidth(2f);

        LineDataSet expenseSet = new LineDataSet(expenseEntries, "Expense");
        expenseSet.setColor(Color.RED);
        expenseSet.setCircleColor(Color.RED);
        expenseSet.setLineWidth(2f);

        LineData lineData = new LineData(incomeSet, expenseSet);
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

        // Y Axis
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);

        // Chart Styling
        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.invalidate(); // Refresh chart
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LineChart lineChart = view.findViewById(R.id.incomeChart);

        TransactionDao dao = AppDatabase.getDatabase(requireContext()).transactionDao();

        dao.getMonthlyTotals("Income").observe(getViewLifecycleOwner(), incomeList -> {
            dao.getMonthlyTotals("Expense").observe(getViewLifecycleOwner(), expenseList -> {
                setupLineChart(lineChart, incomeList, expenseList);
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
        // TODO: Use the ViewModel
    }

}