package com.example.beweather.weatherdata;

import android.content.Context;

import java.util.ArrayList;

public class ReportCache {

    private static ReportCache thisInstance;
    private ArrayList<WeatherReport> recentReports;
    private int nextLocation;

    private ReportCache(Context context) {
        recentReports = new ArrayList<>();
        nextLocation = 0;
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
        }

    }

    public void clearCache() {
        recentReports.clear();
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




}
