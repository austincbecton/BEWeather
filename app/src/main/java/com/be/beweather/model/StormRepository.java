package com.be.beweather.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.be.beweather.accounts.StormAccount;

import java.util.List;

public class StormRepository {


    private AccountDao accountDao;
    private LiveData<List<StormAccount>> allAccounts;


    public StormRepository(Application application) {
            StormAccountDatabase db = StormAccountDatabase.getDatabase(application);
            accountDao = db.accountDao();
            allAccounts = accountDao.getAll();
    }

    LiveData<List<StormAccount>> getAllAccounts() {
        return allAccounts;
    }


    public void insert(StormAccount stormAccount) {
        StormAccountDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.insert(stormAccount);
        });


    }

    public LiveData<StormAccount> getAccount(String localId) {
        return accountDao.getAccount(localId);

    }

    public void update(StormAccount stormAccount) {
        StormAccountDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.update(stormAccount);
        });
    }

    public void deleteAccount(StormAccount stormAccount) {
        StormAccountDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.delete(stormAccount);
        });

    }

}
