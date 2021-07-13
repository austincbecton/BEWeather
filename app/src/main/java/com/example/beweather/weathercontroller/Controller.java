package com.example.beweather.weathercontroller;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.beweather.model.WebViewModel;
import com.example.beweather.weatherdata.WeatherReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(JSONObject response) {
                        String cityName = "";
                        String countryName = "";
                        String time = "";
                        String temperature = "";
                        String maxTemp = "";
                        String minTemp = "";
                        String skyCondition = "";
                        String humidity = "";
                        String tomorrow = "";
                        String tonight = "";
                        JSONArray daysArray;
                        SimpleDateFormat dateProperFormat;

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

                        //Let's get weather objects for each day.
                        try {
                            daysArray = response.getJSONArray("days");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            daysArray = null;
                            System.out.println("ERROR DAYSARRAY");
                        }

                        try {
                            Date currentTime = Calendar.getInstance().getTime();
                            Locale locale = Locale.getDefault();
                            dateProperFormat =
                                    new SimpleDateFormat ("yyyy-MM-dd", locale);

                            time = daysArray.getJSONObject(0).getString("datetime");

                        }catch (JSONException e) {
                            System.out.println("Error gathering time");
                        }

                        try {
                            temperature = daysArray.getJSONObject(0).getString("temp");
                            //temperature = response.getJSONObject("currentConditions").getString("temp");
                            if (temperature.length() > 2) {
                                temperature = temperature.substring(0,2);
                            }
                            //temperature = response.getJSONArray("days").getJSONObject(0).getString("temp");
                        }catch (JSONException e) {
                            System.out.println("Error gathering temperature");
                        }
                        try {
                            maxTemp = daysArray.getJSONObject(0).getString("tempmax");
                        } catch (JSONException e) {
                            System.out.println("Error getting max temperature");
                        }
                        try {
                            minTemp = daysArray.getJSONObject(0).getString("tempmin");
                        } catch (JSONException e) {
                            System.out.println("Error getting max temperature");
                        }
                        try {
                            skyCondition = daysArray.getJSONObject(0).getString("icon");

                                    //response.getJSONObject("currentConditions").getString("conditions");
                        } catch (JSONException e) {
                            System.out.println("Error gathering conditions");
                        }
                        try {
                            humidity = daysArray.getJSONObject(0).getString("humidity");
                        } catch (JSONException e) {
                            System.out.println("Error gathering humidity");
                        }
                        try {
                            JSONArray hours = daysArray.getJSONObject(0).getJSONArray("hours");
                            tonight = "unknown";
                            for (int i = 0; i < hours.length(); i++) {
                                if (hours.getJSONObject(i).getString("datetime").equals("23:00:00")) {
                                    tonight = hours.getJSONObject(i).getString("temp");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            tomorrow = daysArray.getJSONObject(1).getString("tempmax");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        WeatherReport newReport = new WeatherReport(cityName, countryName);

                        newReport.updateWeatherReport(time, temperature, condition_parcer(skyCondition), humidity, maxTemp, minTemp, tomorrow, tonight);
                        System.out.println("***JSON DATA***");
                        System.out.println(time);
                        System.out.println(cityName);
                        System.out.println(countryName);
                        System.out.println(temperature);
                        System.out.println(skyCondition);
                        System.out.println(newReport.getHumidity());
                        vmodel.addWeatherReport(newReport, context);
                        System.out.println("Checking vmodel: " +
                                vmodel.getRecentReport().getLocationName_city() +
                                " is set as current city");

                    }
                }, error -> System.out.println("ERROR GETTING WEATHER"));


        // Access the RequestQueue through your singleton class.
        this.addToRequestQueue(jsonObjectRequest);

    }

    private void location_splicer(String fullLocation) {

        cityName_holder = "";
        countryName_holder = "";
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

    private String condition_parcer(String reportedConditions) {
        String parcedCondition = "";
        System.out.println("REPORTED CONDITION ARE: " + reportedConditions);
        switch (reportedConditions) {
            case  "blowing or drifting snow":
                parcedCondition = "snow";
                break;
            case "drizzle":
                parcedCondition = "rain";
                break;
            case "heavy drizzle":
                parcedCondition = "rain";
                break;
            case "light drizzle":
                parcedCondition = "rain";
                break;
            case "heavy drizzle/rain":
                parcedCondition = "rain";
                break;
            case "light drizzle/rain":
                parcedCondition = "rain";
                break;
            case "freezing drizzle/freezing rain":
                parcedCondition = "snow";
                break;
            case "heavy freezing drizzle/freezing rain":
                parcedCondition = "snow";
                break;
            case "light freezing drizzle/freezing rain":
                parcedCondition = "snow";
                break;
            case "freezing fog":
                parcedCondition = "snow";
                break;
            case "heavy freezing rain":
                parcedCondition = "snow";
                break;
            case "light freezing rain":
                parcedCondition = "snow";
                break;
            case "funnel cloud/tornado":
                parcedCondition = "storm";
                break;
            case "hail showers":
                parcedCondition = "storm";
                break;
            case "ice":
                parcedCondition = "snow";
                break;
            case "lightning without thunder":
                parcedCondition = "rain";
                break;
            case "mist":
                parcedCondition = "rain";
                break;
            case "precipitation in vicinity":
                parcedCondition = "rain";
                break;
            case "rain":
                parcedCondition = "rain";
                break;
            case "heavy rain and snow":
                parcedCondition = "snow";
                break;
            case "light rain and snow":
                parcedCondition = "snow";
                break;
            case "rain showers":
                parcedCondition = "rain";
                break;
            case "heavy rain":
                parcedCondition = "storm";
                break;
            case "light rain":
                parcedCondition = "rain";
                break;
            case "sky coverage decreasing":
                parcedCondition = "cloudy";
                break;
            case "sky unchanged":
                parcedCondition = "sun";
                break;
            case "smoke or haze":
                parcedCondition = "sun";
                break;
            case "snow":
                parcedCondition = "snow";
                break;
            case "snow and rain showers":
                parcedCondition = "snow";
                break;
            case "snow showers":
                parcedCondition = "snow";
                break;
            case "heavy snow":
                parcedCondition = "snow";
                break;
            case "light snow":
                parcedCondition = "snow";
                break;
            case "squalls":
                parcedCondition = "rain";
                break;
            case "thunderstorm":
                parcedCondition = "storm";
                break;
            case "thunderstorm without precipitation":
                parcedCondition = "storm";
                break;
            case "diamond dust":
                parcedCondition = "sun";
                break;
            case "hail":
                parcedCondition = "storm";
                break;
            case "overcast":
                parcedCondition = "cloudy";
                break;
            case "partially cloudy":
                parcedCondition = "cloudy";
                break;
            case "clear":
                parcedCondition = "sun";
                break;
            case "cloudy":
                parcedCondition = "cloudy";
            case "partly-cloudy-day":
                parcedCondition = "cloudy";
        }
        System.out.println("PARCED CONDITION IS: " + parcedCondition);
        return parcedCondition;
    }










}
