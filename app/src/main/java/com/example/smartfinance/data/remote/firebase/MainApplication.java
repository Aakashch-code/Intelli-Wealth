// Updated MainApplication.java - Minor update to ensure Firestore is ready
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

            Log.d("MainApplication", "Firebase initialized successfully");

            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                auth.signInAnonymously()
                        .addOnSuccessListener(authResult -> {
                            Log.d("MainApplication", "Anonymous auth successful");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("MainApplication", "Anonymous auth failed: " + e.getMessage(), e);
                        });
            } else {
                Log.d("MainApplication", "User already authenticated");
            }

            testFirebaseConnection(db);

        } catch (Exception e) {
            Log.e("MainApplication", "Firebase initialization error: " + e.getMessage(), e);
        }
    }

    private void testFirebaseConnection(FirebaseFirestore db) {
        // Existing test code remains the same...
        try {
            Map<String, Object> testData = new HashMap<>();
            testData.put("timestamp", Timestamp.now());
            testData.put("status", "connected");
            testData.put("appVersion", BuildConfig.VERSION_NAME);

            db.collection("connection_tests").document("app_start")
                    .set(testData)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Connection test successful"))
                    .addOnFailureListener(e -> Log.w("Firebase", "Connection test failed: " + e.getMessage()));

            Map<String, Object> testDataSingle = new HashMap<>();
            testDataSingle.put("message", "Test data from Android app");
            testDataSingle.put("timestamp", Timestamp.now());
            testDataSingle.put("appVersion", "1.0");

            db.collection("testCollection")
                    .add(testDataSingle)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Document written with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error writing document", e);
                    });

            db.collection("connection_tests")
                    .document("app_start")
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String appVersion = documentSnapshot.getString("appVersion");
                            String status = documentSnapshot.getString("status");
                            Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");
                            Log.d("FirestoreData", "Version: " + appVersion + ", Status: " + status + ", Time: " + timestamp);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreData", "Error getting data", e);
                    });

        } catch (Exception e) {
            Log.w("MainApplication", "Connection test error: " + e.getMessage());
        }
    }
}