package com.example.beweather.weatherdata;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.beweather.MainActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ReportCache {

    private static ReportCache thisInstance;
    private ArrayList<WeatherReport> recentReports;
    private int nextLocation;
    public SharedPreferences sharedPref;
    public Context context;

    public ExecutorService backgroundThread = Executors.newSingleThreadExecutor();

    private ReportCache(Context context) {
        this.context = context;
        recentReports = new ArrayList<>();
        nextLocation = 0;

        sharedPref = context.getSharedPreferences(MainActivity.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);

    }

    public static ReportCache getReportCache(Context context){
        if (thisInstance == null) {
            thisInstance = new ReportCache(context);
        }
        return thisInstance;
    }

    public void addReport(WeatherReport newReport) {
        if (recentReports.size()>5) {
            recentReports.add(0, newReport);
            recentReports.remove(4);
        } else {
            recentReports.add(newReport);
        }

    }

    public void clearCache() {
        recentReports.clear();
    }


    public WeatherReport getCurrentLocation() {
        WeatherReport nextLocation_report;
        try {
            nextLocation_report = recentReports.get(nextLocation);
        } catch (Exception e) {
            return null;
        }
        return nextLocation_report;
    }



    public WeatherReport getNextLocation() {
        WeatherReport nextLocation_report;
        try {
            nextLocation_report = recentReports.get(nextLocation);
            nextLocation = nextLocation + 1;

        }catch (Exception e) {
            return null;
        }
        return nextLocation_report;

    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sharedPreferences_handler_addAllReports() {
        Integer i = 1;
        for (WeatherReport report : recentReports) {
            report.saveToSharedPreferences(context,i.toString());
            i++;
        }
    }


    public void sharedPreferences_handler_addSingleReport(String reportNum, WeatherReport report) {
        report.saveToSharedPreferences(context, reportNum);
    }



}
