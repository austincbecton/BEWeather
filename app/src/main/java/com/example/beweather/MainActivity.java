package com.example.beweather;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.beweather.model.WebViewModel;
import com.example.beweather.weathercontroller.Controller;
import com.example.beweather.weatherdata.WeatherReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    Controller controller;
    SharedPreferences sharedPref;
    WebViewModel model;
    Button update_weather_button;
    Button update_screen_button;
    TextView currentLocation_temperature;
    TextView currentLocationName_cardView;
    public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //get utilities set up
        model = new WebViewModel(this);
        sharedPref = getSharedPreferences(MainActivity.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        controller = Controller.getController(this);


        //Sets the weather display items
        setWeatherDisplay(model.getCurrentWeatherReport());


        //get the most recently searched-for weather, if it exists
        if (model.getCurrentWeatherReport() != null) {

        }





        update_weather_button = findViewById(R.id.update_weather_button);
        update_weather_button.setOnClickListener(View -> {

            controller.submitRequest("Dayton, OH", model);
            model.saveReportCache();


        });

        update_screen_button = findViewById(R.id.update_screen_button);
        update_screen_button.setOnClickListener(View -> {
            currentLocation_temperature.setText(model.getCurrentWeatherReport().getTemperature());
            System.out.println(model.getCurrentWeatherReport().getTemperature());
            currentLocationName_cardView.setText(model.getCurrentWeatherReport().getLocationName_city());
            System.out.println(model.getCurrentWeatherReport().getLocationName_city());

        });

    }



    public void setWeatherDisplay(WeatherReport currentWeatherReport) {
        currentLocation_temperature = findViewById(R.id.currentLocation_temperature);
        currentLocationName_cardView = findViewById(R.id.currentLocationName_cardView);

        currentLocation_temperature.setText(currentWeatherReport.getTemperature());
        currentLocationName_cardView.setText(currentWeatherReport.getLocationName_city());

    }


}
