package com.example.beweather.weatherdata;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.beweather.MainActivity;

import java.util.HashMap;

public class WeatherReport {

    private final String locationName_city;
    private final String locationName_country;
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


    //report_save_number will help us identify this recent save
    public String saveToSharedPreferences(Context context, String report_save_number) {
        //First, access shared preferences.
        SharedPreferences sharedPref = context.getSharedPreferences(MainActivity.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);


        //Now, lets clear any currently existing saved report under this save_number to prevent memory leaks.
        if (sharedPref.getString(report_save_number, null) != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            String report_TAG  = report_save_number + sharedPref.getString(report_save_number, null);

            editor.remove(report_TAG + "locationName_region");
            editor.remove(report_TAG + "locationName_country");
            editor.remove(report_TAG + "time");
            editor.remove(report_TAG + "temperature");
            editor.remove(report_TAG + "skyCondition");
            editor.remove(report_TAG + "humidity");
        }


        //Now, save this current city.
        SharedPreferences.Editor editor = sharedPref.edit();
        String report_TAG = report_save_number + getLocationName_city() + getLocationName_country();
        editor.putString(report_save_number, report_TAG);
        editor.putString(report_TAG +"locationName_country", getLocationName_country());
        editor.putString(report_TAG +"time", getTime());
        editor.putString(report_TAG +"temperature", getTemperature());
        editor.putString(report_TAG +"skyCondition", getSkyCondition());
        editor.putString(report_TAG +"humidity", getHumidity());

        return getLocationName_city() + ", " + getLocationName_country();

    }

}
