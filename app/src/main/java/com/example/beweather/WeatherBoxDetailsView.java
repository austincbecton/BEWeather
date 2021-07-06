package com.example.beweather;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import com.example.beweather.weatherdata.WeatherReport;


public class WeatherBoxDetailsView extends CardView {

    OnTouchListener touchListener;
    ImageView weatherIconView;

    public WeatherBoxDetailsView(@NonNull Context context) {
        super(context);
    }

    public WeatherBoxDetailsView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherBoxDetailsView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }





    public void setUpView(Context context) {
        inflate(context, R.layout.weather_standard_layout, this);

    }

    public void alterViewLayout_detailView(Context context) {


        inflate(context, R.layout.weather_details_layout, this);
    }

    public void alterViewLayout_standardView(Context context) {

        inflate(context, R.layout.weather_standard_layout, this);

    }

    //STANDARD VIEW


    /*
    public ImageView getWeatherViews_weatherIcon() {
        return findViewById(R.id.weatherIcon3);
    }
    
     */

    public TextView getWeatherViews_temperature() {
        return findViewById(R.id.currentLocation_temperature3);
    }

    public TextView getWeatherViews_location() {
        return findViewById(R.id.currentLocationName_cardView3);
    }



    //DETAILS VIEW
    public TextView getWeatherViews_humidity(){
        return findViewById(R.id.detailView_humidity);
    }

    public TextView getWeatherViews_conditions(){
        return findViewById(R.id.detailView_conditions);
    }

    public TextView getWeatherViews_tonight() {
        return findViewById(R.id.detailView_tonight);
    }

    public TextView getWeatherViews_cityName() {
        return findViewById(R.id.detailView_cityName);
    }

    public ImageView getWeatherIcon() {
        return findViewById(R.id.weatherIcon3_image);
    }


    public void startLoadingScreen(Context context) {
        removeAllViews();
        inflate(context, R.layout.weather_loading_layout, this);

    }




}
