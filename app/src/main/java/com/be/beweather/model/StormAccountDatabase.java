package com.be.beweather.model;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.be.beweather.accounts.StormAccount;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {StormAccount.class}, version = 3, exportSchema = false)
public abstract class StormAccountDatabase extends RoomDatabase{

    private static volatile StormAccountDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final RoomDatabase.Callback roomCallback
            = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(()-> {
                AccountDao accountDao = INSTANCE.accountDao();
                accountDao.deleteAll();// clean slate!
            });

        }
    };
    static StormAccountDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StormAccountDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StormAccountDatabase.class, "word_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }




    public abstract AccountDao accountDao();


}
