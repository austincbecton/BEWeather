package com.example.beweather;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;




//Used in main activity
public class WeatherBoxGestureListener extends GestureDetector.SimpleOnGestureListener  {
    private static final String DEBUG_TAG = "Gestures";
    WeatherBox weatherBox;
    public WeatherBoxGestureListener(WeatherBox weatherBox){
        this.weatherBox = weatherBox;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        weatherBox.switchToWeatherDetails();
        weatherBox.triggerAd();
        super.onLongPress(e);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        weatherBox.exitWeatherBox();
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }



}
