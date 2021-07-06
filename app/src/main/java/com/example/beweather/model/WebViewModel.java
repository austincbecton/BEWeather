package com.example.beweather.model;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.beweather.weathercontroller.Controller;
import com.example.beweather.weatherdata.ReportCache;
import com.example.beweather.weatherdata.WeatherReport;



public class WebViewModel extends ViewModel {

    MutableLiveData<String> recentWeatherSearch_location;
    MutableLiveData<String> recentWeatherSearch_temperature;
    MutableLiveData<String> recentWeatherSearch_humidity;
    MutableLiveData<String> recentWeatherSearch_skyConditions;
    MutableLiveData<String> recentWeatherSearch_countryName;



    public WebViewModel(Context context) {
        //reportCache = ReportCache.getReportCache(context);
        this.recentWeatherSearch_location = new MutableLiveData<>();
        this.recentWeatherSearch_countryName = new MutableLiveData<>();
        this.recentWeatherSearch_humidity = new MutableLiveData<>();
        this.recentWeatherSearch_skyConditions = new MutableLiveData<>();
        this.recentWeatherSearch_temperature = new MutableLiveData<>();
        //setDefaultWeather();

    }

    public void addWeatherReport(WeatherReport report, Context context) {
        report.saveToSharedPreferences(context, "cache");
        recentWeatherSearch_countryName.setValue(report.getLocationName_country());
        recentWeatherSearch_humidity.setValue(report.getHumidity());
        recentWeatherSearch_skyConditions.setValue(report.getSkyCondition());
        recentWeatherSearch_temperature.setValue(report.getTemperature());

        //City name must be last because our observer will watch for changes here,
        //so we need to alter other elements before this one to make sure it's complete.
        recentWeatherSearch_location.setValue(report.getLocationName_city());

        System.out.println("***ADDED DATA***");
        System.out.println(recentWeatherSearch_location.getValue());
        System.out.println(recentWeatherSearch_temperature.getValue());
        System.out.println(recentWeatherSearch_skyConditions.getValue());
        System.out.println(recentWeatherSearch_humidity.getValue());


    }

    public WeatherReport getRecentReport() {

        WeatherReport dummy = new WeatherReport(recentWeatherSearch_location.getValue(),
                recentWeatherSearch_countryName.getValue());
        dummy.updateWeatherReport("", recentWeatherSearch_temperature.getValue(),
                recentWeatherSearch_skyConditions.getValue(),
                recentWeatherSearch_humidity.getValue(), "", "", "",
                "");
        return dummy;
    }

    private void setDefaultWeather() {
        recentWeatherSearch_location.setValue("SAMPLE");
        recentWeatherSearch_countryName.setValue("..");
        recentWeatherSearch_humidity.setValue("..");
        recentWeatherSearch_skyConditions.setValue("..");
        recentWeatherSearch_temperature.setValue("..");
    }






    /*

    public WeatherReport getRecentWeatherReport() {

        return reportCache.getNextLocation();
    }

    public WeatherReport getCurrentWeatherReport() {

        try { return reportCache.getCurrentLocation();

        } catch (Exception e) {
            System.out.println("ERROR GETTING CURRENT REPORT");

            try {
                reportCache.syncReportCacheWithSharedPreferences();
            } catch (Exception e2) {
                System.out.println("Error syncing report cache");
            }
            return null;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addWeatherReport(WeatherReport newReport) {
        reportCache.addReport(newReport);
        saveReportCache();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveReportCache() {
        reportCache.sharedPreferences_handler_addAllReports();
    }


    public void syncReportCache() {
        reportCache.syncReportCacheWithSharedPreferences();
    }



     */





}
