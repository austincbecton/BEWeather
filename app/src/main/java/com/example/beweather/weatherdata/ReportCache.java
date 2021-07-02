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
        if (recentReports.size()>4) {
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
        syncReportCacheWithSharedPreferences();
        try {
            nextLocation_report = recentReports.get(0);

        } catch (Exception e) {
            System.out.println("recentReports.get(0) produced an error.");
            return null;
        }

        return nextLocation_report;
    }



    public WeatherReport getNextLocation() {
        WeatherReport nextLocation_report;

        //Will rotate existing reports by adding first one to the end, and so forth.
        try {
            nextLocation_report = recentReports.get(0);
            recentReports.add(recentReports.get(0));
            recentReports.remove(0);

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


    //To see how data is saved in sharedpreferences, see WeatherReport.java
    public void syncReportCacheWithSharedPreferences() {

        //first, clear the cache
        getReportCache(context).clearCache();

        for (int i = 0; i < 6; i++) {
            if (sharedPref.getString("report"+ i, null) != null) {
                WeatherReport temporaryReport = new WeatherReport(null, null);
                temporaryReport.matchWithSharedPreferences(context, String.valueOf(i));
                getReportCache(context).addReport(temporaryReport);

            }
        }
    }



}
