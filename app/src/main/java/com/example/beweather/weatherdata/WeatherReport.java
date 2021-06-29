package com.example.beweather.weatherdata;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.beweather.MainActivity;

import java.util.HashMap;

public class WeatherReport {

    private String locationName_city;
    private String locationName_country;
    private String time;
    private String temperature;
    private String skyCondition;
    private String humidity;

    public WeatherReport(String city, String country) {
        this.locationName_city = city;
        this.locationName_country = country;
        this.time = null;
        this.temperature = null;
        this.skyCondition = null;
        this.humidity = null;
    }


    public void updateWeatherReport(String time, String temperature, String skyCondition, String humidity) {
        this.time = time;
        this.temperature = temperature;
        this.skyCondition = skyCondition;
        this.humidity = humidity;

    }

    public String getLocationName_city() {
        return locationName_city;
    }

    public String getLocationName_country() {
        return locationName_country;
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getSkyCondition() {
        return skyCondition;
    }

    public String getHumidity() {
        return humidity;
    }




    //report_save_number will help us identify this recent save
    public void saveToSharedPreferences(Context context, String report_save_number) {
        //First, access shared preferences.
        SharedPreferences sharedPref = context.getSharedPreferences(MainActivity.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);


        // There are a limited number of slots in the cache, which we're saving as "report_id."
        // So we can later identify the city in this particular slot, to get the other information.
        String report_slot = "report"+report_save_number;


        //Now, lets clear any currently existing saved report under this save_number to prevent memory leaks.
        if (sharedPref.getString(report_slot, null) != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            String report_TAG = sharedPref.getString(report_slot, null);
            //String report_TAG  = report_save_number + sharedPref.getString(report_save_number, null);
            editor.remove(report_TAG + "locationName_city");
            editor.remove(report_TAG + "locationName_country");
            editor.remove(report_TAG + "time");
            editor.remove(report_TAG + "temperature");
            editor.remove(report_TAG + "skyCondition");
            editor.remove(report_TAG + "humidity");
            editor.apply();
        }


        //Now, save this current city. We'll create special tags to later retrieve the info we save.
        SharedPreferences.Editor editor = sharedPref.edit();


        // report_TAG will tell us the location and help us gather other details.
        // We will later retrieve this using "report_id" above.
        String report_TAG = report_save_number + getLocationName_city() + getLocationName_country();

        editor.putString(report_slot, report_TAG);

        //Now we save the other information in shared preferences, using report_TAG as the key.
        editor.putString(report_TAG +"locationName_city", getLocationName_city());
        editor.putString(report_TAG +"locationName_country", getLocationName_country());
        editor.putString(report_TAG +"time", getTime());
        editor.putString(report_TAG +"temperature", getTemperature());
        editor.putString(report_TAG +"skyCondition", getSkyCondition());
        editor.putString(report_TAG +"humidity", getHumidity());
        editor.apply();

    }

    public void matchWithSharedPreferences(Context context, String report_save_number) {
        SharedPreferences sharedPref = context.getSharedPreferences(MainActivity.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String report_id = "report"+report_save_number;
        String report_TAG = sharedPref.getString(report_id, null);
        String city = sharedPref.getString(report_TAG +"locationName_city", null);
        String country = sharedPref.getString(report_TAG +"locationName_country", null);
        String time = sharedPref.getString(report_TAG +"time", null);
        String temperature = sharedPref.getString(report_TAG +"temperature", null);
        String skyCondition = sharedPref.getString(report_TAG +"skyCondition", null);
        String humidity = sharedPref.getString(report_TAG +"humidity", null);

        this.locationName_city = city;
        this.locationName_country = country;

        updateWeatherReport(time, temperature, skyCondition, humidity);



    }




}


/*
public HashMap<String, String> getWeatherReportAsHashMap() {
        HashMap<String, String> weatherReportHashMap = new HashMap<>();
        weatherReportHashMap.put("locationName_city", getLocationName_city());
        weatherReportHashMap.put("locationName_country", getLocationName_country());
        weatherReportHashMap.put("time", getTime());
        weatherReportHashMap.put("temperature", getTemperature());
        weatherReportHashMap.put("skyCondition", getSkyCondition());
        weatherReportHashMap.put("humidity", getHumidity());

        return weatherReportHashMap;
    }
 */
