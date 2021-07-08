package com.example.beweather.accounts;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.beweather.model.WebViewModel;

@Entity(tableName = "stormAccounts_table")
public class StormAccount {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int localDatabaseItemId;

    @NonNull
    private String firebaseId;

    @NonNull
    private String nickname;

    @NonNull
    private String membership;

    @NonNull
    public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";

    public StormAccount() {
        this.nickname = "noName";
        this.firebaseId = "noFirebaseId";
        this.membership = "basic";

    }

    public String getNickname() {
        return nickname;
    }
    public String getFirebaseId() {
        return firebaseId;
    }
    public String getMembership() {
        return membership;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public int getLocalDatabaseItemId() {
        return localDatabaseItemId;
    }

    public void setLocalDatabaseItemId(int localDatabaseItemId) {
        this.localDatabaseItemId = localDatabaseItemId;
    }
/*
    public void saveToDatabase(WebViewModel webViewModel) {
        webViewModel.saveAccountInStormDatabase(this);
    }

 */






}
