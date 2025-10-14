// File: com/example/smartfinance/data/local/database/AppDatabase.java

package com.example.smartfinance.data.local.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartfinance.data.local.dao.TransactionDao;
import com.example.smartfinance.data.local.dao.GoalDao;
import com.example.smartfinance.data.model.Goal;
import com.example.smartfinance.utils.DateConverter;
import com.example.smartfinance.data.model.Transaction;

// 1. Add Goal.class to entities, increment version to 5, and add TypeConverter
@Database(entities = {Transaction.class, Goal.class}, version = 6, exportSchema = true)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // 2. Add the abstract method for GoalDao
    public abstract TransactionDao transactionDao();
    public abstract GoalDao goalDao();

    // Previous migrations remain unchanged
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

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE transactions ADD COLUMN firestoreId TEXT");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // No schema changes from version 3 to 4
        }
    };

    // 3. Create a new migration from version 4 to 5 to add the 'goals' table
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // This SQL statement creates the new table for the Goal entity.
            // Column names and types must match the Goal model class.
            database.execSQL("CREATE TABLE IF NOT EXISTS `goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `targetAmount` REAL NOT NULL, `currentAmount` REAL NOT NULL, `targetDate` INTEGER)");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "transaction_database")
                            // 4. Add the new migration to the builder
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}