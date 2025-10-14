package com.example.smartfinance.ui.home.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartfinance.ui.home.model.Transaction;
@Database(entities = {Transaction.class}, version = 4, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract TransactionDao transactionDao();

    // Migration from version 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE transactions ADD COLUMN category TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN date TEXT");
            database.execSQL("ALTER TABLE transactions ADD COLUMN paymentMethod TEXT");
            database.execSQL("UPDATE transactions SET category = 'Uncategorized' WHERE category IS NULL");
            database.execSQL("UPDATE transactions SET paymentMethod = 'Not specified' WHERE paymentMethod IS NULL");
            database.execSQL("UPDATE transactions SET date = strftime('%d/%m/%Y', timestamp / 1000, 'unixepoch') WHERE date IS NULL");
        }
    };

    // Migration from version 2 to 3 - Add firestoreId column
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE transactions ADD COLUMN firestoreId TEXT");
        }
    };

    // Migration from version 3 to 4 - Empty migration (if needed)
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // No changes needed, just increment version
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "transaction_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                            .fallbackToDestructiveMigration()  // Add this for safety
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}