package com.example.smartfinance.data.remote.firebase;

import android.app.Application;
import android.util.Log;

import com.example.smartfinance.BuildConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseApp.initializeApp(this);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d("MainApplication", "Firebase initialized");

            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                auth.signInAnonymously()
                        .addOnSuccessListener(a -> Log.d("MainApplication", "Anonymous auth successful"))
                        .addOnFailureListener(e -> Log.e("MainApplication", "Anonymous auth failed", e));
            }

        } catch (Exception e) {
            Log.e("MainApplication", "Firebase init error", e);
        }
    }



}