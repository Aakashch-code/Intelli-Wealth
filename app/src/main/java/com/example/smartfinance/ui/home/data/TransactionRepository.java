package com.example.smartfinance.ui.home.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.smartfinance.ui.home.model.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {

    private static final String TAG = "TransactionRepository";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_TRANSACTIONS = "transactions";

    private final TransactionDao transactionDao;
    private final LiveData<List<Transaction>> allTransactions;
    private final ExecutorService executorService;
    private final Application application;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ListenerRegistration firestoreListener;
    private boolean isFirebaseInitialized = false;
    private boolean isInitializing = false;
    private final Set<String> processingFirestoreIds = new HashSet<>();

    public TransactionRepository(Application application) {
        this.application = application;
        AppDatabase db = AppDatabase.getDatabase(application);
        transactionDao = db.transactionDao();
        allTransactions = transactionDao.getAllTransactions();
        executorService = Executors.newSingleThreadExecutor();

        initializeFirebase();
    }

    private void initializeFirebase() {
        if (isInitializing) {
            return;
        }

        isInitializing = true;
        try {
            if (FirebaseApp.getApps(application).isEmpty()) {
                FirebaseApp.initializeApp(application);
            }
            firestore = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            isFirebaseInitialized = true;
            Log.d(TAG, "Firebase initialized successfully");

            setupFirestoreListener();
            syncLocalDataToFirestore();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase: " + e.getMessage(), e);
            isFirebaseInitialized = false;
        } finally {
            isInitializing = false;
        }
    }

    private boolean isFirebaseReady() {
        return isFirebaseInitialized && firebaseAuth != null && firestore != null;
    }

    private String getCurrentUserId() {
        if (!isFirebaseReady() || firebaseAuth.getCurrentUser() == null) {
            return null;
        }
        return firebaseAuth.getCurrentUser().getUid();
    }

    public LiveData<Double> getTotalByType(String type) {
        return transactionDao.getTotalByType(type);
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public void insertTransaction(Transaction transaction) {
        executorService.execute(() -> {
            // Check if transaction already exists by Firestore ID
            if (transaction.getFirestoreId() != null) {
                Transaction existing = transactionDao.getTransactionByFirestoreId(transaction.getFirestoreId());
                if (existing != null) {
                    Log.d(TAG, "Skipping duplicate insert for Firestore ID: " + transaction.getFirestoreId());
                    return;
                }
            }

            long localId = transactionDao.insertTransaction(transaction);
            if (isFirebaseReady() && getCurrentUserId() != null && transaction.getFirestoreId() == null) {
                syncTransactionToFirestore(transaction, localId);
            }
        });
    }

    public void updateTransaction(Transaction transaction) {
        executorService.execute(() -> {
            transactionDao.updateTransaction(transaction);
            if (isFirebaseReady() && getCurrentUserId() != null && transaction.getFirestoreId() != null) {
                updateTransactionInFirestore(transaction);
            }
        });
    }

    public void deleteTransaction(Transaction transaction) {
        executorService.execute(() -> {
            transactionDao.deleteTransaction(transaction);
            if (isFirebaseReady() && getCurrentUserId() != null && transaction.getFirestoreId() != null) {
                deleteTransactionFromFirestore(transaction.getFirestoreId());
            }
        });
    }

    private void setupFirestoreListener() {
        if (!isFirebaseReady()) {
            Log.w(TAG, "Cannot setup Firestore listener - Firebase not ready");
            return;
        }

        String userId = getCurrentUserId();
        if (userId == null) {
            Log.w(TAG, "Cannot setup Firestore listener - User not authenticated");
            return;
        }

        try {
            CollectionReference transactionsRef = firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_TRANSACTIONS);

            firestoreListener = transactionsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            processFirestoreChanges(querySnapshot);
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up Firestore listener", e);
        }
    }

    private void processFirestoreChanges(QuerySnapshot querySnapshot) {
        executorService.execute(() -> {
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                String firestoreId = document.getId();
                synchronized (processingFirestoreIds) {
                    if (processingFirestoreIds.contains(firestoreId)) {
                        Log.d(TAG, "Skipping already processed Firestore ID: " + firestoreId);
                        continue;
                    }
                    processingFirestoreIds.add(firestoreId);
                }

                try {
                    Transaction remoteTransaction = Transaction.fromMap(document.getData());
                    remoteTransaction.setFirestoreId(firestoreId);
                    remoteTransaction.setUserId(getCurrentUserId());

                    Transaction localTransaction = transactionDao.getTransactionByFirestoreId(firestoreId);
                    if (localTransaction == null) {
                        long localId = transactionDao.insertTransaction(remoteTransaction);
                        transactionDao.updateFirestoreId((int) localId, firestoreId);
                        Log.d(TAG, "Inserted new transaction from Firestore: " + firestoreId);
                    } else if (remoteTransaction.getTimestamp() > localTransaction.getTimestamp()) {
                        remoteTransaction.setId(localTransaction.getId());
                        transactionDao.updateTransaction(remoteTransaction);
                        Log.d(TAG, "Updated transaction from Firestore: " + firestoreId);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing Firestore document: " + firestoreId, e);
                } finally {
                    synchronized (processingFirestoreIds) {
                        processingFirestoreIds.remove(firestoreId);
                    }
                }
            }
        });
    }

    private void syncLocalDataToFirestore() {
        if (!isFirebaseReady()) {
            return;
        }

        executorService.execute(() -> {
            try {
                List<Transaction> unsyncedTransactions = transactionDao.getUnsyncedTransactions();
                for (Transaction transaction : unsyncedTransactions) {
                    if (transaction.getFirestoreId() == null) {
                        syncTransactionToFirestore(transaction, transaction.getId());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in syncLocalDataToFirestore", e);
            }
        });
    }

    private void syncTransactionToFirestore(Transaction transaction, long localId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return;
        }

        try {
            transaction.setUserId(userId);
            CollectionReference transactionsRef = firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_TRANSACTIONS);

            // Save transaction directly to Firestore
            transactionsRef.add(transaction.toMap())
                    .addOnSuccessListener(documentReference -> {
                        executorService.execute(() -> {
                            transactionDao.updateFirestoreId((int) localId, documentReference.getId());
                            transaction.setFirestoreId(documentReference.getId());
                            Log.d(TAG, "Transaction synced to Firestore: " + documentReference.getId());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error syncing transaction to Firestore: " + e.getMessage(), e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in syncTransactionToFirestore: " + e.getMessage(), e);
        }
    }

    private void updateTransactionInFirestore(Transaction transaction) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return;
        }

        try {
            transaction.setUserId(userId);
            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_TRANSACTIONS)
                    .document(transaction.getFirestoreId())
                    .set(transaction.toMap())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Transaction updated in Firestore: " + transaction.getFirestoreId());
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error updating transaction in Firestore: " + e.getMessage(), e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in updateTransactionInFirestore: " + e.getMessage(), e);
        }
    }

    private void deleteTransactionFromFirestore(String firestoreId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return;
        }

        try {
            firestore.collection(COLLECTION_USERS)
                    .document(userId)
                    .collection(COLLECTION_TRANSACTIONS)
                    .document(firestoreId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Transaction deleted from Firestore: " + firestoreId);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error deleting transaction from Firestore: " + e.getMessage(), e);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in deleteTransactionFromFirestore: " + e.getMessage(), e);
        }
    }

    public void shutdown() {
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
        executorService.shutdown();
    }

    public void forceSync() {
        syncLocalDataToFirestore();
    }

    public boolean isFirebaseInitialized() {
        return isFirebaseInitialized;
    }
}