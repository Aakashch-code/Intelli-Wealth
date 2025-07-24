package com.example.smartfinance.ui.budget;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartfinance.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BudgetFragment extends Fragment {

    private BudgetViewModel mViewModel;

    public static BudgetFragment newInstance() {
        return new BudgetFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton fabMenu = view.findViewById(R.id.fabMenu);

        FloatingActionButton fabAddBudget = view.findViewById(R.id.btnAddBudget);

        fabAddBudget.setOnClickListener(v -> {
            AddBudgetBottomSheet bottomSheet = new AddBudgetBottomSheet();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        final boolean[] isFabMenuOpen = {false};

        fabMenu.setOnClickListener(v -> {
            if (!isFabMenuOpen[0]) {
                // Show mini FABs with animation

                showFab(fabAddBudget);
                fabMenu.animate().rotation(45f).setDuration(200).start(); // rotate main FAB
            } else {
                // Hide mini FABs with animation
                hideFab(fabAddBudget);
                fabMenu.animate().rotation(0f).setDuration(200).start();
            }
            isFabMenuOpen[0] = !isFabMenuOpen[0];
        });
    }
    // Animation helpers
    private void showFab(FloatingActionButton fab) {
        fab.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);
        fab.setTranslationY(100f);
        fab.animate().alpha(1f).translationY(0f).setDuration(200).start();
    }

    private void hideFab(FloatingActionButton fab) {
        fab.animate().alpha(0f).translationY(100f).setDuration(200)
                .withEndAction(() -> fab.setVisibility(View.GONE))
                .start();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        // TODO: Use the ViewModel
    }

}