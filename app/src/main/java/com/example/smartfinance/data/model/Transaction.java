package com.example.smartfinance.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String type;
    public String category;
    public double amount;
    public String date;
    public String paymentMethod;
    public String note;
    public long timestamp;

    // Firestore document ID (not stored in Room)

    private String firestoreId;

    // User ID for Firestore security
    @Ignore
    private String userId;

    public Transaction(@NonNull String type, String category, double amount, String date,
                       String paymentMethod, String note, long timestamp) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.timestamp = timestamp;
    }

    // Required empty constructor for Firestore
    public Transaction() {
    }

    // Getters
    public int getId() { return id; }
    @NonNull public String getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getNote() { return note; }
    public long getTimestamp() { return timestamp; }

    // Firestore ID methods
    @Exclude
    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    // User ID methods
    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Convert to Map for Firestore
    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("category", category);
        map.put("amount", amount);
        map.put("date", date);
        map.put("paymentMethod", paymentMethod);
        map.put("note", note);
        map.put("timestamp", timestamp);
        map.put("userId", userId);
        return map;
    }

    // Create from Firestore document
    public static Transaction fromMap(Map<String, Object> map) {
        Transaction transaction = new Transaction();
        transaction.type = (String) map.get("type");
        transaction.category = (String) map.get("category");
        transaction.amount = ((Number) map.get("amount")).doubleValue();
        transaction.date = (String) map.get("date");
        transaction.paymentMethod = (String) map.get("paymentMethod");
        transaction.note = (String) map.get("note");
        transaction.timestamp = ((Number) map.get("timestamp")).longValue();
        transaction.userId = (String) map.get("userId");
        return transaction;
    }

    // Setters for Room (required for some operations)
    public void setId(int id) { this.id = id; }
    public void setType(@NonNull String type) { this.type = type; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(String date) { this.date = date; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setNote(String note) { this.note = note; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}