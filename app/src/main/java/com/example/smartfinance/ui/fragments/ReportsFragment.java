package com.example.smartfinance.ui.fragments;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartfinance.R;
import com.example.smartfinance.data.local.database.AppDatabase;
import com.example.smartfinance.ui.viewmodels.ReportsViewModel;
import com.example.smartfinance.ui.viewmodels.ReportsViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment {

    private ReportsViewModel mViewModel;
    private AppDatabase database;
    private CheckBox cbIncome, cbExpense, cbBudget, cbSubscription, cbGoal;
    private Button btnStartDate, btnEndDate, btnExport;
    private RadioGroup rgExportFormat;
    private long startTime = 0, endTime = 0;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    // Fields for pending export parameters
    private List<String> pendingTypes;
    private long pendingStartTime, pendingEndTime;
    private String pendingFormat;

    private ActivityResultLauncher<String> saveFileLauncher;
    private Uri selectedUri; // To store the selected URI for sharing if needed

    public static ReportsFragment newInstance() {
        return new ReportsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = AppDatabase.getDatabase(requireContext());
        setupViewModel();
        setupUI(view);
        setupSaveLauncher();
        observeViewModel();
    }

    private void setupViewModel() {
        ReportsViewModelFactory factory = new ReportsViewModelFactory(requireActivity().getApplication(), database);
        mViewModel = new ViewModelProvider(this, factory).get(ReportsViewModel.class);
    }

    private void setupUI(View view) {
        cbIncome = view.findViewById(R.id.cbIncome);
        cbExpense = view.findViewById(R.id.cbExpense);
        cbBudget = view.findViewById(R.id.cbBudget);
        cbSubscription = view.findViewById(R.id.cbSubscription);
        cbGoal = view.findViewById(R.id.cbGoal);
        btnStartDate = view.findViewById(R.id.btnStartDate);
        btnEndDate = view.findViewById(R.id.btnEndDate);
        rgExportFormat = view.findViewById(R.id.rgExportFormat);
        btnExport = view.findViewById(R.id.btnExport);

        // Default dates to last 30 days
        Calendar today = Calendar.getInstance();
        endTime = today.getTimeInMillis();
        btnEndDate.setText(dateFormat.format(new Date(endTime)));
        Calendar startCal = (Calendar) today.clone();
        startCal.add(Calendar.DAY_OF_MONTH, -30);
        startTime = startCal.getTimeInMillis();
        btnStartDate.setText(dateFormat.format(new Date(startTime)));

        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));
        btnExport.setOnClickListener(v -> handleExport());
    }

    private void setupSaveLauncher() {
        saveFileLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/octet-stream"),
                uri -> {
                    selectedUri = uri;
                    if (uri != null) {
                        mViewModel.exportReports(pendingTypes, pendingStartTime, pendingEndTime, pendingFormat, uri);
                    }
                });
    }

    private void showDatePicker(boolean isStart) {
        Calendar current = Calendar.getInstance();
        if (isStart) current.setTimeInMillis(startTime);
        else current.setTimeInMillis(endTime);

        new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    current.set(year, month, day);
                    long selectedTime = current.getTimeInMillis();
                    if (isStart) startTime = selectedTime;
                    else endTime = selectedTime;
                    if (isStart) btnStartDate.setText(dateFormat.format(current.getTime()));
                    else btnEndDate.setText(dateFormat.format(current.getTime()));
                },
                current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void handleExport() {
        List<String> types = new ArrayList<>();
        if (cbIncome.isChecked()) types.add("income");
        if (cbExpense.isChecked()) types.add("expense");
        if (cbBudget.isChecked()) types.add("budget");
        if (cbSubscription.isChecked()) types.add("subscription");
        if (cbGoal.isChecked()) types.add("goal");

        if (types.isEmpty()) {
            Toast.makeText(requireContext(), "Select at least one data type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startTime == 0 || endTime == 0) {
            Toast.makeText(requireContext(), "Select date range", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endTime < startTime) {
            Toast.makeText(requireContext(), "End date must be after start date", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = rgExportFormat.getCheckedRadioButtonId();
        pendingFormat = checkedId == R.id.rbPdf ? "PDF" : "CSV";
        pendingTypes = types;
        pendingStartTime = startTime;
        pendingEndTime = endTime;

        String dateRange = fileDateFormat.format(new Date(pendingStartTime)) + "_" + fileDateFormat.format(new Date(pendingEndTime));
        String suggestedName = "smartfinance_reports_" + pendingFormat.toLowerCase() + "_" + dateRange + "." + pendingFormat.toLowerCase();
        saveFileLauncher.launch(suggestedName);
    }

    private void observeViewModel() {
        mViewModel.getExportPathLiveData().observe(getViewLifecycleOwner(), path -> {
            if (path != null) {
                Toast.makeText(requireContext(), "Report exported successfully to selected location", Toast.LENGTH_LONG).show();
                // Optional: Share via Intent using the Uri
                // Intent shareIntent = new Intent(Intent.ACTION_SEND)
                //     .setType(pendingFormat.equals("PDF") ? "application/pdf" : "text/csv")
                //     .putExtra(Intent.EXTRA_STREAM, selectedUri);
                // startActivity(Intent.createChooser(shareIntent, "Share Report"));
            } else {
                Toast.makeText(requireContext(), "Export failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}