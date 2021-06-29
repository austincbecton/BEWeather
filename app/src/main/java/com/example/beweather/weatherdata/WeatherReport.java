package com.example.beweather.weatherdata;

public class WeatherReport {

    private String locationName_city;
    private String locationName_region;
    private String locationName_country;
    private String time;
    private String temperature;
    private String skyCondition;
    private String humidity;

    public WeatherReport(String city, String region, String country) {
        this.locationName_city = city;
        this.locationName_region = region;
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

    public String getLocationName_region() {
        return locationName_region;
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

}
