package com.example.beweather.weatherdata;

import android.app.Activity;
import android.content.Context;

import com.example.beweather.R;

public class WeatherIconProvider {
    String iconType;
    public WeatherIconProvider() {

        iconType = "";


    }

    public int getWeatherIconId(WeatherReport weatherReport) {
        iconType = weatherReport.getSkyCondition();

        if (iconType.equals("rain")) {
            return R.drawable.rain;
        } else if (iconType.equals("sun")) {
            return R.drawable.sun;
        } else if (iconType.equals("snow")) {
            return R.drawable.snow;
        } else if (iconType.equals("wind")) {
            return R.drawable.wind;
        } else if (iconType.equals("cloud") || iconType.equals("cloudy")) {
            return R.drawable.cloudy;
        } else if (iconType.equals("partlycloudy")) {
            return R.drawable.partlycloudy;
        } else if (iconType.equals("storm") || iconType.equals("stormy") || iconType.equals("storms")) {
            return R.drawable.storm;
        } else {return R.drawable.sun;}
    }


}
