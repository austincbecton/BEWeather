package com.example.beweather.weathercontroller;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.beweather.model.WebViewModel;
import com.example.beweather.weatherdata.WeatherReport;

import org.json.JSONException;
import org.json.JSONObject;

public class Controller  {

    public static final String TAG = Controller.class.getSimpleName();
    private static Controller thisInstance;
    private RequestQueue currentRequestQueue;
    private Context context;
    private String cityName_holder;
    private String countryName_holder;


    private Controller(Context context) {
        this.context = context;
        currentRequestQueue = getRequestQueue();
        cityName_holder = "";
        countryName_holder = "";
    }

    public static synchronized Controller getController(Context context) {
        if (thisInstance == null) {
            thisInstance = new Controller(context);
        }
        return thisInstance;
    }


    public RequestQueue getRequestQueue() {
        if (currentRequestQueue == null) {
            currentRequestQueue = Volley.newRequestQueue(context);
        }
        return currentRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG: tag);
        getRequestQueue().add(req);
    }

    //In this case, the tag was not provided, so we'll use our default
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelCurrentRequests(Object tag) {
        if (currentRequestQueue != null) {
            currentRequestQueue.cancelAll(tag);
        }
    }



    public void submitRequest(String location, WebViewModel vmodel) {
        String weatherUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + location + "?key=QZ2CJDXT7CYASXM6598KXSPDX";

        //URL below is free API testing
        //String weatherUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/%22%20+%20location%20+%20%22?key=QZ2CJDXT7CYASXM6598KXSPDX";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, weatherUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String cityName = "";
                        String countryName = "";

                        String time = "";
                        String temperature = "";
                        String skyCondition = "";
                        String humidity = "";
                        try {
                            location_splicer(response.getString("resolvedAddress"));
                        } catch(JSONException e) {
                            System.out.println("Error gathering location");
                        }
                        try {
                            cityName = cityName + cityName_holder;
                        } catch (Exception e) {
                            System.out.println("Error gathering cityName");
                        }
                        try {
                            countryName = countryName + countryName_holder;
                        } catch (Exception e) {
                            System.out.println("Error gathering countryName");
                        }
                        try {
                            time = response.getJSONObject("currentConditions").getString("datetime");
                            //time = response.getJSONArray("days").getJSONObject(0).getString("datetime");
                        }catch (JSONException e) {
                            System.out.println("Error gathering time");
                        }
                        try {
                            temperature = response.getJSONObject("currentConditions").getString("temp");
                            if (temperature.length() > 2) {
                                temperature = temperature.substring(0,2);
                            }
                            //temperature = response.getJSONArray("days").getJSONObject(0).getString("temp");
                        }catch (JSONException e) {
                            System.out.println("Error gathering temperature");
                        }
                        try {
                            skyCondition = response.getJSONObject("currentConditions").getString("conditions");
                        } catch (JSONException e) {
                            System.out.println("Error gathering conditions");
                        }
                        try {
                            humidity = response.getJSONObject("currentConditions").getString("humidity");
                        } catch (JSONException e) {
                            System.out.println("Error gathering humidity");
                        }

                        WeatherReport newReport = new WeatherReport(cityName, countryName);
                        newReport.updateWeatherReport(time, temperature, skyCondition, humidity);
                        vmodel.addWeatherReport(newReport);

                    }
                }, error -> System.out.println("ERROR GETTING WEATHER"));


        // Access the RequestQueue through your singleton class.
        this.addToRequestQueue(jsonObjectRequest);

    }

    private void location_splicer(String fullLocation) {
        boolean city = true;

        // loop through each element using for-each loop
        for(char c : fullLocation.toCharArray()) {

            if (city & c != ',') {
                cityName_holder = cityName_holder + String.valueOf(c);
            } else if (!city & c != ',') {
                countryName_holder = countryName_holder + String.valueOf(c);
            } else  {
                city = false;
            }

        }



    }


}
