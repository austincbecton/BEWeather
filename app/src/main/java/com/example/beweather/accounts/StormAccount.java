package com.example.beweather.accounts;

import android.content.Context;
import android.content.SharedPreferences;

public class StormAccount {

    private String nickname;
    private String firebaseId;
    private String membership;
    public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";
    public SharedPreferences sharedPref;
    public Context context;

    public StormAccount(String firebaseId, Context context) {
        this.nickname = "";
        this.firebaseId = firebaseId;
        this.membership = "basic";
        this.context = context;
        this.sharedPref = context.getSharedPreferences(GLOBAL_SHARED_PREFERENCES,
                Context.MODE_PRIVATE);

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

    public void syncAccount() {
        this.nickname = sharedPref.getString(getFirebaseId()+"accountName", null);
        this.firebaseId = sharedPref.getString(getFirebaseId()+"accountId", null);
        this.membership = sharedPref.getString(getFirebaseId()+"accountMembership", null);
    }

    public void saveToSharedPreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getFirebaseId()+"accountName", getFirebaseId());
        editor.putString(getFirebaseId()+"accountId", getFirebaseId());
        editor.putString(getFirebaseId()+"accountMembership", getFirebaseId());
        editor.apply();
    }

    public void deleteAccount() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(getFirebaseId()+"accountName");
        editor.remove(getFirebaseId()+"accountId");
        editor.remove(getFirebaseId()+"accountMembership");
        editor.commit();
    }






}
