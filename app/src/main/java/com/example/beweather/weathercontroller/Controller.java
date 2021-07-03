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
                        vmodel.addWeatherReport(newReport);

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
        switch (reportedConditions) {
            case  "Blowing Or Drifting Snow":
                parcedCondition = "snow";
                break;
            case "Drizzle":
                parcedCondition = "rain";
                break;
            case "Heavy Drizzle":
                parcedCondition = "rain";
                break;
            case "Light Drizzle":
                parcedCondition = "rain";
                break;
            case "Heavy Drizzle/Rain":
                parcedCondition = "rain";
                break;
            case "Light Drizzle/Rain":
                parcedCondition = "rain";
                break;
            case "Freezing Drizzle/Freezing Rain":
                parcedCondition = "snow";
                break;
            case "Heavy Freezing Drizzle/Freezing Rain":
                parcedCondition = "snow";
                break;
            case "Light Freezing Drizzle/Freezing Rain":
                parcedCondition = "snow";
                break;
            case "Freezing Fog":
                parcedCondition = "snow";
                break;
            case "Heavy Freezing Rain":
                parcedCondition = "snow";
                break;
            case "Light Freezing Rain":
                parcedCondition = "snow";
                break;
            case "Funnel Cloud/Tornado":
                parcedCondition = "storm";
                break;
            case "Hail Showers":
                parcedCondition = "storm";
                break;
            case "Ice":
                parcedCondition = "snow";
                break;
            case "Lightning Without Thunder":
                parcedCondition = "rain";
                break;
            case "Mist":
                parcedCondition = "rain";
                break;
            case "Precipitation In Vicinity":
                parcedCondition = "rain";
                break;
            case "Rain":
                parcedCondition = "rain";
                break;
            case "Heavy Rain And Snow":
                parcedCondition = "snow";
                break;
            case "Light Rain And Snow":
                parcedCondition = "snow";
                break;
            case "Rain Showers":
                parcedCondition = "rain";
                break;
            case "Heavy Rain":
                parcedCondition = "storm";
                break;
            case "Light Rain":
                parcedCondition = "rain";
                break;
            case "Sky Coverage Decreasing":
                parcedCondition = "partlycloudy";
                break;
            case "Sky Unchanged":
                parcedCondition = "sun";
                break;
            case "Smoke Or Haze":
                parcedCondition = "sun";
                break;
            case "Snow":
                parcedCondition = "snow";
                break;
            case "Snow And Rain Showers":
                parcedCondition = "snow";
                break;
            case "Snow Showers":
                parcedCondition = "snow";
                break;
            case "Heavy Snow":
                parcedCondition = "snow";
                break;
            case "Light Snow":
                parcedCondition = "snow";
                break;
            case "Squalls":
                parcedCondition = "rain";
                break;
            case "Thunderstorm":
                parcedCondition = "storm";
                break;
            case "Thunderstorm Without Precipitation":
                parcedCondition = "storm";
                break;
            case "Diamond Dust":
                parcedCondition = "sun";
                break;
            case "Hail":
                parcedCondition = "storm";
                break;
            case "Overcast":
                parcedCondition = "cloudy";
                break;
            case "Partially cloudy":
                System.out.println("partlycloudy");
                break;
            case "Clear":
                parcedCondition = "sun";
                break;

        }

        return parcedCondition;
    }










}
