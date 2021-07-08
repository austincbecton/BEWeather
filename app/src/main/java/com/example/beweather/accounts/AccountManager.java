package com.example.beweather.accounts;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.beweather.model.WebViewModel;

public class AccountManager {

    private String recentUser;
    private Boolean recentUserIsLoggedIn;
    public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";
    public SharedPreferences sharedPref;
    public Context context;
    WebViewModel model;


    public AccountManager(Context context, WebViewModel model) {
        this.model = model;
        this.context = context;
        this.sharedPref = context.getSharedPreferences(GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.recentUserIsLoggedIn = false;
        this.recentUser = null;
        if (sharedPref.getString("accountManagerRecentUser", null) != null) {
            syncAccountManager();
        }


    }

    public void setRecentUser(String recentUser) {
        this.recentUser = recentUser;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("accountManagerRecentUser");
        editor.putString("accountManagerRecentUser", recentUser);
        editor.commit();
        setRecentUserIsLoggedIn(true);
        model.setCurrentAccount(recentUser);
    }

    private void setRecentUserIsLoggedIn(Boolean status) {
        this.recentUserIsLoggedIn = status;
    }

    public Boolean checkIfLoggedIn() {
        return this.recentUserIsLoggedIn;
    }

    private String getRecentUser() {
        return this.recentUser;
    }

    private void syncAccountManager() {
        if (sharedPref.getString("accountManagerRecentUser", null) != null) {
            String name = sharedPref.getString("accountManagerRecentUser", null);
            setRecentUser(name);
            setRecentUserIsLoggedIn(true);
            model.setCurrentAccount(name);
        } else {
            setRecentUserIsLoggedIn(false);
        }
    }


    public StormAccount getCurrentAccount() {
        if (recentUserIsLoggedIn) {
            StormAccount thisAccount = new StormAccount();
            //thisAccount.saveToDatabase(model);
            return thisAccount;
        } else {
            return null;}
    }

    public Boolean accountIdExists(String thisId) {
        if (sharedPref.getString(thisId+"accountId", null).equals(thisId)) {
            return true;
        } else {
            return false;
        }
    }

    public void  signOut() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("accountManagerRecentUser");
        editor.commit();
        setRecentUserIsLoggedIn(false);
        model.setCurrentAccount("");
    }






}
