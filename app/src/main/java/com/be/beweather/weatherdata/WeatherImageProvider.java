package com.be.beweather.weatherdata;

import com.be.beweather.R;

public class WeatherImageProvider {
    String iconType;
    String backgroundType;
    public WeatherImageProvider() {

        iconType = "";
        backgroundType = "";

    }

    public Integer getWeatherIconId(WeatherReport weatherReport) {
        try {
            iconType = weatherReport.getSkyCondition();
        } catch(Exception e) {
            System.out.println("Error getting Icon type.");
            iconType = "sun";
        }
        System.out.println("ICON TYPE IS: " + iconType);

        switch (iconType) {
            case "rain":
                return R.drawable.rain;
            case "sun":
                return R.drawable.sun;
            case "snow":
                return R.drawable.snow;
            case "wind":
                return R.drawable.wind;
            case "cloud":
                return R.drawable.cloudy;
            case "cloudy":
                return R.drawable.cloudy;
            case "partlycloudy":
                return R.drawable.partlycloudy;
            case "storm":
                return R.drawable.storm;
            case "stormy":
                return R.drawable.storm;
            case "storms":
                return R.drawable.storm;
            default:
                return R.drawable.sun;

        }


    }

    public Integer getWeatherBackgroundId(WeatherReport weatherReport) {

        try {
            backgroundType = weatherReport.getSkyCondition();
        } catch(Exception e) {
            System.out.println("Error getting Icon type.");
            backgroundType = "sun";
        }
        System.out.println("ICON TYPE IS: " + backgroundType);


        if (backgroundType.equals("rain")) {
            return R.drawable.card_background_rain;
        } else if (backgroundType.equals("sun")) {
            return R.drawable.card_background_sunny;
        } else if (backgroundType.equals("snow")) {
            return R.drawable.card_background_rain;
        } else if (backgroundType.equals("wind")) {
            return R.drawable.card_background_cloudy;
        } else if (backgroundType.equals("cloud")) {
            return R.drawable.card_background_cloudy;
        } else if (backgroundType.equals("cloudy")) {
            return R.drawable.card_background_cloudy;
        } else if (backgroundType.equals("partlycloudy")) {
            return R.drawable.card_background_cloudy;
        } else if (backgroundType.equals("storm") || iconType.equals("stormy") || iconType.equals("storms")) {
            return R.drawable.card_background_rain;
        } else {return R.drawable.card_background_sunny;}

    }


}
