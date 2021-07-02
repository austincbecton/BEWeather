package com.example.beweather;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.example.beweather.WeatherDisplayPresets;
import com.example.beweather.model.WebViewModel;
import com.example.beweather.weathercontroller.Controller;
import com.example.beweather.weatherdata.WeatherReport;

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

        //constructor needs views added from the activity
        public WeatherBox(
                Context context, Controller controller, WebViewModel model, EditText weatherSearchBar, WeatherBoxDetailsView weatherBoxDetailsView, Button newWeatherBoxButton, ImageView addWeatherBoxButton
                /*
                Context context, Controller controller, WebViewModel model, EditText weatherSearchBar, CardView cardView, ImageView weatherIcon,
                Button newWeatherBoxButton, TextView currentLocation_temperature,
                TextView currentLocationName_cardView, ImageView addWeatherBoxButton)

                 */

        )
        {
            this.context = context;
            this.controller = controller;
            this.model = model;
            this.weatherSearchBar = weatherSearchBar;
            this.weatherBoxDetailsView = weatherBoxDetailsView;
            this.weatherIcon = weatherBoxDetailsView.getWeatherViews_weatherIcon();
            this.newWeatherBoxButton = newWeatherBoxButton;
            this.currentLocation_temperature = weatherBoxDetailsView.getWeatherViews_temperature();
            this.currentLocation_name = weatherBoxDetailsView.getWeatherViews_location();
            this.addWeatherBoxButton = addWeatherBoxButton;
            this.weatherDisplayPresets =
                    new WeatherDisplayPresets(context, weatherBoxDetailsView,
                            newWeatherBoxButton,
                            addWeatherBoxButton);


            try {
                WeatherReport defaultWeatherReport = model.getCurrentWeatherReport();
                this.thisWeatherBoxWeatherReport = defaultWeatherReport;
                setUp(defaultWeatherReport.getTemperature(),
                        defaultWeatherReport.getLocationName_city());
            }catch(Exception e) {
                System.out.println("Error getting weather");
            }




            this.newWeatherBoxButton.setOnClickListener(View ->{
                if (!weatherSearchBar.getText().toString().isEmpty()) {
                    controller.submitRequest(weatherSearchBar.getText().toString(), model);
                    addNewWeather(
                            model.getCurrentWeatherReport().getTemperature(),
                            model.getCurrentWeatherReport().getLocationName_city());
                            this.thisWeatherBoxWeatherReport = model.getCurrentWeatherReport();
                } else {
                    addNewWeather(
                            model.getCurrentWeatherReport().getTemperature(),
                            model.getCurrentWeatherReport().getLocationName_city()
                    );
                    this.thisWeatherBoxWeatherReport = model.getCurrentWeatherReport();
                }
            });

            this.nowDisplayingDetails = false;
        }

        private void setUp (String temperature, String location) {
            if (temperature != null) {
                currentLocation_temperature.setText(temperature);
                currentLocation_name.setText(location);
            } else {


                try {
                    System.out.println("THIS CITY IS: " +
                            model.getCurrentWeatherReport().getLocationName_city());
                    this.thisWeatherBoxWeatherReport = model.getCurrentWeatherReport();
                    String gatheredLocation = model.getCurrentWeatherReport().getLocationName_city();
                    String gaatheredTemperature = model.getCurrentWeatherReport().getTemperature();
                    currentLocation_name.setText(gatheredLocation);
                    currentLocation_temperature.setText(gaatheredTemperature);
                }catch(Exception e) {
                    System.out.println("Error getting weather.");
                }
            }
        }


        public void exitWeatherBox() {
            weatherDisplayPresets.exitAnimation();

        }

        public void addNewWeather(String temperature, String location) {
            setUp(temperature, location);
            weatherDisplayPresets.newWeatherBox();
        }

        public void displayWeatherDetails() {
            if (nowDisplayingDetails) {
                weatherBoxDetailsView.removeAllViews();
                weatherBoxDetailsView.alterViewLayout_standardView(context);
                nowDisplayingDetails = false;

                //Set weather details
                weatherBoxDetailsView.getWeatherViews_location().setText(getThisWeatherReport().getLocationName_city());
                weatherBoxDetailsView.getWeatherViews_temperature().setText(getThisWeatherReport().getTemperature());


            } else {
                weatherBoxDetailsView.removeAllViews();
                //weatherBoxDetailsView.setUpView(context);
                weatherBoxDetailsView.alterViewLayout_detailView(context);
                nowDisplayingDetails = true;

                //Set weather details
                weatherBoxDetailsView.getWeatherViews_cityName().setText(getThisWeatherReport().getLocationName_city());
                weatherBoxDetailsView.getWeatherViews_conditions().setText(getThisWeatherReport().getSkyCondition());
                weatherBoxDetailsView.getWeatherViews_humidity().setText(getThisWeatherReport().getHumidity());
                weatherBoxDetailsView.getWeatherViews_tonight().setText(getThisWeatherReport().getTemperature());

            }
        }

        public WeatherReport getThisWeatherReport() {
            return thisWeatherBoxWeatherReport;
        }




}
