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
import android.widget.EditText;
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
    EditText weatherSearchBar;
    Button enterWeatherButton;
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
        model.syncReportCache();




        //get the most recently searched-for weather, if it exists
        if (model.getCurrentWeatherReport() != null) {
            //Sets the weather display items
            setWeatherDisplay(model.getCurrentWeatherReport());
        } else {
            controller.submitRequest("Dayton, OH", model);
            setWeatherDisplay(model.getCurrentWeatherReport());
        }

        //Set up views
        weatherSearchBar = findViewById(R.id.currentLocationName_searchbar);
        enterWeatherButton = findViewById(R.id.enter_weather_button);
        enterWeatherButton.setOnClickListener(View -> {
            String location = weatherSearchBar.getText().toString();
            controller.submitRequest(location, model);
        });



        update_weather_button = findViewById(R.id.update_weather_button);
        update_weather_button.setOnClickListener(View -> {
            String persistKey = sharedPref.getString("report1", null);
            System.out.println(sharedPref.getString(persistKey, null));
            System.out.println(model.getCurrentWeatherReport().getLocationName_city());
            System.out.println(model.getRecentWeatherReport().getLocationName_city());

            String cache1 = model.getCurrentWeatherReport().getLocationName_city();
            String cache1temp = model.getCurrentWeatherReport().getTemperature();
            String cache2 = model.getRecentWeatherReport().getLocationName_city();
            String cache3 = model.getRecentWeatherReport().getLocationName_city();
            String cache4 = model.getRecentWeatherReport().getLocationName_city();
            String pref_TAG = sharedPref.getString("report1", null);
            String pref1 = sharedPref.getString(pref_TAG+"locationName_city", null);
            String pref1temp = sharedPref.getString(pref_TAG+"temperature",null);
            String pref_TAG2  = sharedPref.getString("report2", null);
            String pref2 = sharedPref.getString(pref_TAG2+"locationName_city",null);
            String pref_TAG3 = sharedPref.getString("report3", null);
            String pref3 = sharedPref.getString(pref_TAG3+"locationName_city", null);

            String cacheAnalysis =
                            "***CACHE ANALYSIS*** \n" +
                            "First city is: "+cache1+"\n"+
                            "First city temp is: "+cache1temp+"\n" +
                            "Second city is: "+cache2+"\n"+
                            "Third city is: "+cache3+"\n"+
                            "Fourth city is: "+cache4+"\n"+
                            "Prefs city is: "+pref1+"\n" +
                            "Prefs city temp is: "+pref1temp+"\n"+
                            "Prefs city 2 is: "+pref2+"\n"+
                            "Prefs city 3 is: "+pref3;


            System.out.println(cacheAnalysis);


            //Controller will automatically add weather report to the cache via viewmodel
            //model will also backup the cache into the sharedpreferences persistence file
            //controller.submitRequest("Dayton, OH", model);
            //model.saveReportCache();


        });

        update_screen_button = findViewById(R.id.update_screen_button);
        update_screen_button.setOnClickListener(View -> {

            model.saveReportCache();



            //REAL CODE BELOW:
            /*
            currentLocation_temperature.setText(model.getCurrentWeatherReport().getTemperature());
            currentLocationName_cardView.setText(model.getCurrentWeatherReport().getLocationName_city());

             */

        });

        //TESTING
        //String city_name = sharedPref.getString("report1", null);
        //System.out.println(city_name);

    }



    public void setWeatherDisplay(WeatherReport currentWeatherReport) {
        currentLocation_temperature = findViewById(R.id.currentLocation_temperature);
        currentLocationName_cardView = findViewById(R.id.currentLocationName_cardView);
        try {


            currentLocation_temperature.setText(currentWeatherReport.getTemperature());
            currentLocationName_cardView.setText(currentWeatherReport.getLocationName_city());
        } catch (Exception e) {
            System.out.println("There was an error setting the views.");
        }



    }





}
