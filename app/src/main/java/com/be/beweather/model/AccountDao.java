package com.be.beweather.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.be.beweather.accounts.StormAccount;

import java.util.List;

@Dao
public interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(StormAccount account);


    @Update
    void update(StormAccount account);


    @Delete
    void delete(StormAccount stormAccount);

    @Query ("DELETE FROM stormAccounts_table")
    void deleteAll();

    @Query ("SELECT * FROM stormAccounts_table")
    LiveData<List<StormAccount>> getAll() ;

    @Query ("SELECT * FROM stormAccounts_table WHERE firebaseId = :id")
    LiveData<StormAccount> getAccount(String id);

    @Query("SELECT * FROM stormAccounts_table ")
    List<StormAccount> getAll_nonLiveData();



}
