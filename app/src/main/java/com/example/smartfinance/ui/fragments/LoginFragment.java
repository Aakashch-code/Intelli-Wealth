package com.example.smartfinance.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.smartfinance.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient googleSignInClient;

    private MaterialButton btnGoogleSignIn, btnSignOut;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnGoogleSignIn = view.findViewById(R.id.btn_google_signin);
        btnSignOut = view.findViewById(R.id.btn_sign_out);

        // Already signed-in?
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        updateUI(account);

        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        btnSignOut.setOnClickListener(v -> signOut());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    updateUI(account);
                    Toast.makeText(requireContext(), "Welcome " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Toast.makeText(requireContext(), "Sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            updateUI(null);
            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Update both LoginFragment buttons and Navigation Drawer header
     */
    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            btnGoogleSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);

            // Update Navigation Drawer header
            NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            TextView userName = headerView.findViewById(R.id.user_name);
            TextView userEmail = headerView.findViewById(R.id.user_email);
            ImageView profileImage = headerView.findViewById(R.id.user_img);

            userName.setText(account.getDisplayName());
            userEmail.setText(account.getEmail());

            Uri photoUrl = account.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this).load(photoUrl).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.default_avatar); // fallback image
            }

        } else {
            btnGoogleSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);

            // Reset header when logged out
            NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);

            TextView userName = headerView.findViewById(R.id.user_name);
            TextView userEmail = headerView.findViewById(R.id.user_email);
            ImageView profileImage = headerView.findViewById(R.id.user_img);

            userName.setText("User Name");
            userEmail.setText("User Email");
            profileImage.setImageResource(R.drawable.default_avatar);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity());
        updateUI(account);
    }
}
