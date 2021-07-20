package com.be.beweather.model;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.be.beweather.accounts.StormAccount;
import com.be.beweather.weatherdata.WeatherReport;

import java.util.ArrayList;


public class WebViewModel extends ViewModel {

    private MutableLiveData<String> recentWeatherSearch_location;
    private MutableLiveData<String> recentWeatherSearch_temperature;
    private MutableLiveData<String> recentWeatherSearch_humidity;
    private MutableLiveData<String> recentWeatherSearch_skyConditions;
    private MutableLiveData<String> recentWeatherSearch_countryName;
    private MutableLiveData<String> recentWeatherSearch_tonight;
    public MutableLiveData<String> currentAccount;


    public SharedPreferences sharedPref;
    public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";
    private String CURRENT_ACCOUNT = "current_account";
    private static WebViewModel thisInstance;
    private StormRepository stormRepository;


    public WebViewModel(Context context, Activity activity) {
        //reportCache = ReportCache.getReportCache(context);
        this.recentWeatherSearch_location = new MutableLiveData<>();
        this.recentWeatherSearch_countryName = new MutableLiveData<>();
        this.recentWeatherSearch_humidity = new MutableLiveData<>();
        this.recentWeatherSearch_skyConditions = new MutableLiveData<>();
        this.recentWeatherSearch_temperature = new MutableLiveData<>();
        this.recentWeatherSearch_tonight = new MutableLiveData<>();
        this.currentAccount = new MutableLiveData<>();
        this.sharedPref = context.getSharedPreferences(GLOBAL_SHARED_PREFERENCES,
                                                                    Context.MODE_PRIVATE);

        currentAccount.setValue(sharedPref.getString(CURRENT_ACCOUNT, ""));
        this.stormRepository = new StormRepository(activity.getApplication());

    }

    public static WebViewModel getWebViewModel(Context context, Activity activity) {
        if (thisInstance == null) {
            thisInstance = new WebViewModel(context, activity);

        }

        return thisInstance;
    }



    public void addWeatherReport(WeatherReport report, Context context) {
        report.saveToSharedPreferences(context, "cache");
        recentWeatherSearch_countryName.setValue(report.getLocationName_country());
        recentWeatherSearch_humidity.setValue(report.getHumidity());
        recentWeatherSearch_skyConditions.setValue(report.getSkyCondition());
        recentWeatherSearch_temperature.setValue(report.getTemperature());
        recentWeatherSearch_tonight.setValue(report.getTonight());


        //City name must be last because our observer will watch for changes here,
        //so we need to alter other elements before this one to make sure it's complete.
        recentWeatherSearch_location.setValue(report.getLocationName_city());

        System.out.println("***ADDED DATA***");
        System.out.println(recentWeatherSearch_location.getValue());
        System.out.println(recentWeatherSearch_temperature.getValue());
        System.out.println(recentWeatherSearch_skyConditions.getValue());
        System.out.println(recentWeatherSearch_humidity.getValue());
        System.out.println(recentWeatherSearch_tonight.getValue());

    }

    public WeatherReport getRecentReport() {

        WeatherReport dummy = new WeatherReport(recentWeatherSearch_location.getValue(),
                recentWeatherSearch_countryName.getValue());
        dummy.updateWeatherReport("", recentWeatherSearch_temperature.getValue(),
                recentWeatherSearch_skyConditions.getValue(),
                recentWeatherSearch_humidity.getValue(), "", "", "",
                recentWeatherSearch_tonight.getValue());
        return dummy;
    }

    //Useful for a WeatherBox to check if current report is relevant to its own data
    //By using observers, WeatherBox will be able to update itself as new data comes in
    public LiveData<String> getRecentReport_location() {
        return recentWeatherSearch_location;
    }


    /*
    private void setDefaultWeather() {
        recentWeatherSearch_location.setValue("SAMPLE");
        recentWeatherSearch_countryName.setValue("..");
        recentWeatherSearch_humidity.setValue("..");
        recentWeatherSearch_skyConditions.setValue("..");
        recentWeatherSearch_temperature.setValue("..");
    }

     */


    public void setCurrentAccount(String firebaseId) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CURRENT_ACCOUNT, firebaseId);
        editor.apply();
        this.currentAccount.setValue(firebaseId);

    }

    public String getCurrentAccountFromModel() {
        return this.currentAccount.getValue();
    }


    public void saveAccountInStormDatabase(StormAccount stormAccount) {

        stormRepository.insert(stormAccount);

    }

    public StormAccount getAccountFromDatabase(String localId) {
        return stormRepository.getAccount(localId).getValue();
    }

    public void updateAccount(StormAccount stormAccount) {
        stormRepository.update(stormAccount);
    }

    public void deleteAccount(StormAccount stormAccount) {
        stormRepository.deleteAccount(stormAccount);
    }




    public ArrayList<StormAccount> getAllAccounts_foruseOnBackgroundThread() {

        return new ArrayList<>(stormRepository.getAllAccounts_nonLiveData());
    }






}
