package com.example.beweather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
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

    TextView currentLocation_temperature;
    Controller controller;
    SharedPreferences sharedPref;
    WebViewModel model;
    public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        model = new WebViewModel(this);
        sharedPref = getSharedPreferences(MainActivity.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        controller = Controller.getController(this);

        currentLocation_temperature = findViewById(R.id.currentLocation_temperature);
        submitRequest("Dayton, OH");
    }



    public void submitRequest(String location) {
        //String weatherUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + location + "?key=QZ2CJDXT7CYASXM6598KXSPDX";
        String weatherUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/%22%20+%20location%20+%20%22?key=QZ2CJDXT7CYASXM6598KXSPDX";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, weatherUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                            String cityName = "";
                            String regionName = null;
                            String countryName = null;

                            String time = "";
                            String temperature = "";
                            String skyCondition = "";
                            String humidity = "";

                        try {
                            cityName = response.getString("resolvedAddress");
                        } catch (JSONException e) {
                            System.out.println("Error gathering cityName");
                        }
                        try {
                            time = response.getJSONObject("currentConditions").getString("datetime");
                            //time = response.getJSONArray("days").getJSONObject(0).getString("datetime");
                        }catch (JSONException e) {
                            System.out.println("Error gathering time");
                        }
                        try {
                            temperature = response.getJSONObject("currentConditions").getString("temp");
                            //temperature = response.getJSONArray("days").getJSONObject(0).getString("temp");
                        }catch (JSONException e) {
                            System.out.println("Error gathering temperature");
                        }
                        try {

                        }

                        try {
                            humidity = response.getJSONObject("currentConditions").getString("temp");
                        }

                        WeatherReport newReport = new WeatherReport(cityName, regionName, countryName);
                        newReport.updateWeatherReport(time, temperature, null, null);
                        model.addWeatherReport(newReport);



                    }
                }, error -> System.out.println("ERROR GETTING WEATHER"));


        // Access the RequestQueue through your singleton class.
        controller.addToRequestQueue(jsonObjectRequest);


    }
}
