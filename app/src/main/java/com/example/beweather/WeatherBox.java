package com.example.beweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.example.beweather.model.WebViewModel;
import com.example.beweather.weathercontroller.Controller;
import com.example.beweather.weatherdata.WeatherImageProvider;
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
        public MainActivity mainActivity;





    //constructor needs views added from the activity
        public WeatherBox(
                Context context, MainActivity mainActivity, Controller controller, WebViewModel model,
                EditText weatherSearchBar, WeatherBoxDetailsView weatherBoxDetailsView,
                Button newWeatherBoxButton, ImageView addWeatherBoxButton, String wBoxId
        )
        {
            this.context = context;
            this.mainActivity = mainActivity;
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
        String report_slot = "report"+thisWBoxId;
        String report_TAG = sharedPref.getString(report_slot, null);

            if (sharedPref.getString(report_TAG +"locationName_city", null) != null) {
                try {

                    WeatherReport grabbedReport = new WeatherReport("", "");
                    grabbedReport.matchWithSharedPreferences(context, thisWBoxId);
                    thisWeatherBoxWeatherReport = grabbedReport;

                    try {

                        if (thisWeatherBoxWeatherReport.getLocationName_city().equals("") ||
                                thisWeatherBoxWeatherReport.getLocationName_city() == null) {
                            thisWeatherBoxWeatherReport = model.getRecentReport();
                            thisWeatherBoxWeatherReport.saveToSharedPreferences(context, thisWBoxId);
                        }} catch (Exception e2) {
                        controller.submitRequest("Dayton, OH", model);
                    }

                    System.out.println("****CITY NAME IS: "+ thisWeatherBoxWeatherReport.getLocationName_city());


                    weatherBoxDetailsView.setUpView(context);
                    weatherBoxDetailsView.removeAllViews();
                    weatherBoxDetailsView.alterViewLayout_standardView(context);
                    weatherBoxDetailsView.getWeatherViews_location().setText(this.thisWeatherBoxWeatherReport.getLocationName_city());
                    weatherBoxDetailsView.getWeatherViews_temperature().setText(this.thisWeatherBoxWeatherReport.getTemperature());


                    try {
                        WeatherImageProvider weatherImageProvider = new WeatherImageProvider();
                        Integer drawableId = weatherImageProvider.getWeatherIconId(thisWeatherBoxWeatherReport);
                        Integer backgroundId = weatherImageProvider.getWeatherBackgroundId(thisWeatherBoxWeatherReport);
                        weatherBoxDetailsView.getWeatherIcon().setBackgroundResource(drawableId);
                        weatherBoxDetailsView.getStandardViewLayoutBackground().setBackgroundResource(backgroundId);
                    } catch(Exception e) {
                        System.out.println("Error getting pictures");
                    }



                } catch (Exception e) {
                    System.out.println("ERROR... LINE 121");
                    //For first-time setup, we'll automatically go into add view
                    WeatherReport dummy = new WeatherReport("", "");
                    weatherBoxDetailsView.setUpView(context);
                    weatherBoxDetailsView.removeAllViews();
                    weatherBoxDetailsView.alterViewLayout_standardView(context);
                    weatherBoxDetailsView.getWeatherViews_location().setText(dummy.getLocationName_city());
                    weatherBoxDetailsView.getWeatherViews_temperature().setText(dummy.getTemperature());
                    WeatherImageProvider weatherImageProvider = new WeatherImageProvider();
                    //int drawableId = weatherImageProvider.getWeatherIconId(dummy);
                    weatherBoxDetailsView.getWeatherIcon().setBackgroundResource(R.drawable.sun);
                    weatherBoxDetailsView.setVisibility(View.INVISIBLE);
                    exitWeatherBoxInvisible();
                }

            } else if (this.thisWBoxId.equals("wBox1")) {
                thisWeatherBoxWeatherReport = model.getRecentReport();
                thisWeatherBoxWeatherReport.saveToSharedPreferences(context, thisWBoxId);
                if (thisWeatherBoxWeatherReport.getLocationName_city() == null || thisWeatherBoxWeatherReport.equals("")) {
                    thisWeatherBoxWeatherReport = new WeatherReport("Sample", "");
                    thisWeatherBoxWeatherReport.updateWeatherReport("", "70",
                            "rain", "60", "", "", "",
                            "");
                }
                weatherBoxDetailsView.setUpView(context);
                weatherBoxDetailsView.removeAllViews();
                weatherBoxDetailsView.alterViewLayout_standardView(context);
                weatherBoxDetailsView.getWeatherViews_location().setText(this.thisWeatherBoxWeatherReport.getLocationName_city());
                weatherBoxDetailsView.getWeatherViews_temperature().setText(this.thisWeatherBoxWeatherReport.getTemperature());


                    try {
                        WeatherImageProvider weatherImageProvider = new WeatherImageProvider();
                        Integer drawableId = weatherImageProvider.getWeatherIconId(thisWeatherBoxWeatherReport);
                        Integer backgroundId = weatherImageProvider.getWeatherBackgroundId(thisWeatherBoxWeatherReport);
                        weatherBoxDetailsView.getWeatherIcon().setBackgroundResource(drawableId);
                        weatherBoxDetailsView.getStandardViewLayoutBackground().setBackgroundResource(backgroundId);
                    } catch(Exception e) {
                        System.out.println("Error getting pictures");
                    }


            } else if (model.getRecentReport().getLocationName_city() != null){
                WeatherReport newReport = model.getRecentReport();
                newReport.saveToSharedPreferences(context, thisWBoxId);
                generateWeatherBox();
            }


            else {
                System.out.println("NO SHARED PREF, SO NO VIEWS DISPLAYED");
                exitWeatherBoxInvisible();
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
        //this.thisWeatherBoxWeatherReport = model.getRecentReport();
        if (nowDisplayingDetails) {
            weatherBoxDetailsView.removeAllViews();
            weatherBoxDetailsView.alterViewLayout_standardView(context);
            nowDisplayingDetails = false;
            //Set weather details
            try {
                weatherBoxDetailsView.getWeatherViews_location().setText(this.thisWeatherBoxWeatherReport.getLocationName_city());
                weatherBoxDetailsView.getWeatherViews_temperature().setText(this.thisWeatherBoxWeatherReport.getTemperature());
                WeatherImageProvider weatherImageProvider = new WeatherImageProvider();
                int drawableId = weatherImageProvider.getWeatherIconId(thisWeatherBoxWeatherReport);
                int backgroundId = weatherImageProvider.getWeatherBackgroundId(thisWeatherBoxWeatherReport);
                weatherBoxDetailsView.getWeatherIcon().setBackgroundResource(drawableId);
                weatherBoxDetailsView.getStandardViewLayoutBackground().setBackgroundResource(backgroundId);}
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
                System.out.println("Error line 224");
            }
            try {
                WeatherImageProvider weatherImageProvider = new WeatherImageProvider();
                int drawableId = weatherImageProvider.getWeatherIconId(thisWeatherBoxWeatherReport);
                int backgroundId = weatherImageProvider.getWeatherBackgroundId(thisWeatherBoxWeatherReport);
                weatherBoxDetailsView.getDetailsViewLayoutBackground().setBackgroundResource(backgroundId);
            } catch (Exception e) {
                System.out.println("Error getting image for detail view");
            }
        }

    }
    public void exitWeatherBox() {

            weatherDisplayPresets.exitAnimation();
        String report_slot = "report"+thisWBoxId;
        String report_TAG = sharedPref.getString(report_slot, null);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(report_TAG+"locationName_city");
        editor.commit();

    }

    public void exitWeatherBoxInvisible() {
        weatherBoxDetailsView.removeAllViews();
        weatherDisplayPresets.exitAnimationOnlyAddButton();

    }

    public void triggerAd() {

        mainActivity.triggerAd();

    }



}
