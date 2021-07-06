package com.example.beweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.beweather.model.WebViewModel;
import com.example.beweather.weathercontroller.Controller;
import com.example.beweather.weatherdata.WeatherIconProvider;
import com.example.beweather.weatherdata.WeatherReport;

import java.util.Timer;
import java.util.TimerTask;

public class WeatherBox {

        private Context context;
        private Controller controller;
        private WeatherBoxDetailsView weatherBoxDetailsView;
        private ImageView weatherIcon;
        private Button newWeatherBoxButton;
        private TextView currentLocation_temperature;
        private TextView currentLocation_name;
        private ImageView addWeatherBoxButton;
        private WeatherDisplayPresets weatherDisplayPresets;
        private WebViewModel model;
        private EditText weatherSearchBar;
        private WeatherReport thisWeatherBoxWeatherReport;
        private Boolean nowDisplayingDetails;
        protected String thisWBoxId;
        private SharedPreferences sharedPref;
        public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";
        private String TAG_location;





    //constructor needs views added from the activity
        public WeatherBox(
                Context context, Controller controller, WebViewModel model,
                EditText weatherSearchBar, WeatherBoxDetailsView weatherBoxDetailsView,
                Button newWeatherBoxButton, ImageView addWeatherBoxButton, String wBoxId
        )
        {
            this.context = context;
            this.controller = controller;
            this.model = model;
            this.TAG_location = "wBox" + wBoxId + "location";
            sharedPref = context.getSharedPreferences(GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            this.weatherSearchBar = weatherSearchBar;
            this.weatherBoxDetailsView = weatherBoxDetailsView;
            this.weatherBoxDetailsView.setUpView(context);
            this.newWeatherBoxButton = newWeatherBoxButton;
            this.currentLocation_temperature = weatherBoxDetailsView.getWeatherViews_temperature();
            this.currentLocation_name = weatherBoxDetailsView.getWeatherViews_location();
            this.addWeatherBoxButton = addWeatherBoxButton;
            this.weatherDisplayPresets =

                    new WeatherDisplayPresets(context, weatherBoxDetailsView,
                            newWeatherBoxButton,
                            addWeatherBoxButton);

            this.thisWBoxId = wBoxId;



            this.newWeatherBoxButton.setOnClickListener(View ->{
                System.out.println("NEW WEATHER PRESSED");
                if (!weatherSearchBar.getText().toString().isEmpty()) {
                    controller.submitRequest(weatherSearchBar.getText().toString(), model);
                    this.thisWeatherBoxWeatherReport = model.getRecentReport();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(TAG_location, weatherSearchBar.getText().toString());
                    editor.apply();
                    addNewWeather();


                } else {
                    this.thisWeatherBoxWeatherReport = model.getRecentReport();
                    addNewWeather();

                }
            });
            this.nowDisplayingDetails = false;
            generateWeatherBox();



        }




    public void generateWeatherBox() {


            try {
                String location = sharedPref.getString(TAG_location, null);
                System.out.println("FROM SHARED PREF: " + location);
                if (location != null) {controller.submitRequest(location, model);}
                if (location == null) {
                    thisWeatherBoxWeatherReport = model.getRecentReport();
                    if (thisWeatherBoxWeatherReport.getLocationName_city() == null) {
                        controller.submitRequest("Dayton, OH", model);
                    } else {
                        controller.submitRequest(
                                thisWeatherBoxWeatherReport.getLocationName_city(), model);
                    }
                } else {
                    thisWeatherBoxWeatherReport.matchWithSharedPreferences(context, thisWBoxId);
                }
                if (thisWeatherBoxWeatherReport.getLocationName_city() == null) {
                    thisWeatherBoxWeatherReport = model.getRecentReport();
                }
                System.out.println("try block 1 part 1 complete.");
                System.out.println(TAG_location + " is " + thisWeatherBoxWeatherReport.getLocationName_city());

                try {
                    System.out.println(this.thisWeatherBoxWeatherReport.getLocationName_city());
                    System.out.println("try block 1 part 2 complete.");
                    System.out.println(TAG_location + " is " + thisWeatherBoxWeatherReport.getLocationName_city());



                } catch (Exception e) {
                    WeatherReport dummy = new WeatherReport("??", "??");
                    dummy.updateWeatherReport("??", "??", "??", "??", "??", "??", "??", "??");
                    this.thisWeatherBoxWeatherReport = dummy;
                }

                weatherBoxDetailsView.setUpView(context);
                weatherBoxDetailsView.removeAllViews();
                weatherBoxDetailsView.alterViewLayout_standardView(context);
                weatherBoxDetailsView.getWeatherViews_location().setText(this.thisWeatherBoxWeatherReport.getLocationName_city());
                weatherBoxDetailsView.getWeatherViews_temperature().setText(this.thisWeatherBoxWeatherReport.getTemperature());
                System.out.println("weatherboxdetailsview complete.");

                WeatherIconProvider weatherIconProvider = new WeatherIconProvider();
                int drawableId = weatherIconProvider.getWeatherIconId(thisWeatherBoxWeatherReport);
                weatherBoxDetailsView.getWeatherIcon().setBackgroundResource(drawableId);

                System.out.println("icon provider complete.");

                thisWeatherBoxWeatherReport.saveToSharedPreferences(context, thisWBoxId);
                System.out.println("try block 1 part 3 complete.");
                System.out.println(TAG_location + " is " + thisWeatherBoxWeatherReport.getLocationName_city());



            } catch (Exception e) {
                //For first-time setup, we'll automatically go into add view
                WeatherReport dummy = new WeatherReport("", "");
                weatherBoxDetailsView.setUpView(context);
                weatherBoxDetailsView.removeAllViews();
                weatherBoxDetailsView.alterViewLayout_standardView(context);
                weatherBoxDetailsView.getWeatherViews_location().setText(dummy.getLocationName_city());
                weatherBoxDetailsView.getWeatherViews_temperature().setText(dummy.getTemperature());
                WeatherIconProvider weatherIconProvider = new WeatherIconProvider();
                //int drawableId = weatherIconProvider.getWeatherIconId(dummy);
                weatherBoxDetailsView.getWeatherIcon().setBackgroundResource(R.drawable.sun);
                exitWeatherBox();
            }




    }



    public void addNewWeather() {
        //Delete previous shared preferences.
        if (sharedPref.getString(TAG_location, null) != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(TAG_location);
            editor.apply();
        }

        weatherDisplayPresets.newWeatherBox();
        generateWeatherBox();




    }


    public void switchToWeatherDetails() {
        this.thisWeatherBoxWeatherReport = model.getRecentReport();
        if (nowDisplayingDetails) {
            weatherBoxDetailsView.removeAllViews();
            weatherBoxDetailsView.alterViewLayout_standardView(context);
            nowDisplayingDetails = false;
            //Set weather details
            try {
                weatherBoxDetailsView.getWeatherViews_location().setText(this.thisWeatherBoxWeatherReport.getLocationName_city());
                weatherBoxDetailsView.getWeatherViews_temperature().setText(this.thisWeatherBoxWeatherReport.getTemperature());
                WeatherIconProvider weatherIconProvider = new WeatherIconProvider();
                int drawableId = weatherIconProvider.getWeatherIconId(thisWeatherBoxWeatherReport);
                weatherBoxDetailsView.getWeatherIcon().setBackgroundResource(drawableId);}
            catch(Exception e) {
                weatherBoxDetailsView.getWeatherViews_location().setText("??");
                weatherBoxDetailsView.getWeatherViews_temperature().setText("??");
            }

        } else {
            weatherBoxDetailsView.removeAllViews();
            weatherBoxDetailsView.alterViewLayout_detailView(context);
            nowDisplayingDetails = true;

            try {
                weatherBoxDetailsView.getWeatherViews_cityName().setText(this.thisWeatherBoxWeatherReport.getLocationName_city());
                weatherBoxDetailsView.getWeatherViews_conditions().setText(this.thisWeatherBoxWeatherReport.getSkyCondition());
                weatherBoxDetailsView.getWeatherViews_humidity().setText(this.thisWeatherBoxWeatherReport.getHumidity());
                weatherBoxDetailsView.getWeatherViews_tonight().setText(this.thisWeatherBoxWeatherReport.getTemperature());
            } catch (Exception e) {
                System.out.println("Error getting weather data");
            }
        }

    }
    public void exitWeatherBox() {
        weatherDisplayPresets.exitAnimation();
        System.out.println("REMOVING SHARED PREF");
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(TAG_location);
        editor.commit();

    }



}
