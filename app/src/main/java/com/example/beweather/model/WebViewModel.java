package com.example.beweather.model;
import android.content.Context;
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


    public void addWeatherReport(WeatherReport newReport) {
        reportCache.addReport(newReport);
    }








}
