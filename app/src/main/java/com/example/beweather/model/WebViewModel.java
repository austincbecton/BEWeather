package com.example.beweather.model;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;
import com.example.beweather.weathercontroller.Controller;
import com.example.beweather.weatherdata.ReportCache;
import com.example.beweather.weatherdata.WeatherReport;



public class WebViewModel extends ViewModel {

    ReportCache reportCache;

    public WebViewModel(Context context) {
        reportCache = ReportCache.getReportCache(context);

    }


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







}
