package com.be.beweather;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;


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
        setPreventCornerOverlap(true);
        setRadius(50);
        setElevation(10);

    }

    public void alterViewLayout_detailView(Context context) {
        inflate(context, R.layout.weather_details_layout, this);
        setPreventCornerOverlap(true);
        setRadius(50);
        setElevation(10);
    }

    public void alterViewLayout_standardView(Context context) {

        inflate(context, R.layout.weather_standard_layout, this);
        setPreventCornerOverlap(true);
        setRadius(50);
        setElevation(10);


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

    public RelativeLayout getStandardViewLayoutBackground() {
        return findViewById(R.id.cardViewBackgroundLocation);
    }

    public ConstraintLayout getDetailsViewLayoutBackground() {
        return findViewById(R.id.relativeLayoutInCardView3_detailsView);
    }

    public void hideStandardView() {
        try {
            getWeatherIcon().setVisibility(View.INVISIBLE);
            getWeatherViews_temperature().setVisibility(View.INVISIBLE);
            getWeatherViews_location().setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            System.out.println("Error hiding standard view items");
        }

    }


    public void hideDetailsView() {
        try {

            getWeatherViews_cityName().setVisibility(View.INVISIBLE);
            getWeatherViews_humidity().setVisibility(View.INVISIBLE);
            getWeatherViews_tonight().setVisibility(View.INVISIBLE);
            getWeatherViews_conditions().setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            System.out.println("Error hiding detail view items.");
        }



    }

}
