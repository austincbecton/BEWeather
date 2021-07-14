package com.be.beweather;
import android.content.Context;
import android.content.SharedPreferences;

import com.be.beweather.weatherdata.WeatherReport;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class ReportCacheTest {

    //@Test
    public void cache_saves_correctly(Context context) {
        WeatherReport dummyWeather = new WeatherReport("Dayton", "United States");
        //dummyWeather.updateWeatherReport("10pm", "80", "sunny", "70");
        //WebViewModel model = new WebViewModel(context);
        //model.addWeatherReport(dummyWeather, context);

        SharedPreferences sharedPref = context.getSharedPreferences(MainActivity.GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String report_TAG = sharedPref.getString("report1", null);
        String result = sharedPref.getString(report_TAG + "locationName_city", null);
        assertSame(result, "Dayton");



    }
}
