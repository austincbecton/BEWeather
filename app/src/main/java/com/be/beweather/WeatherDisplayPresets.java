package com.be.beweather;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import com.be.beweather.weatherdata.WeatherReport;


public class WeatherDisplayPresets  {

    private WeatherBoxDetailsView cardView;
    private ObjectAnimator objectAnimator;
    private Button newWeatherBoxButton;
    private ImageView addButton;
    public Context context;
    GestureDetector gestureDetector;
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;


    @SuppressLint("ClickableViewAccessibility")
    public WeatherDisplayPresets(Context context, WeatherBoxDetailsView cardView, Button newWeatherBoxButton, ImageView addButtonImageView) {
        this.cardView = cardView;
        this.newWeatherBoxButton = newWeatherBoxButton;
        this.addButton = addButtonImageView;
        this.context = context;


    }



    public void exitAnimation(

    ) {


        objectAnimator = ObjectAnimator.ofFloat(cardView, "x", -600);
        objectAnimator.setDuration(1000);
        objectAnimator.start();

        Animation fade = new AlphaAnimation(1.f, 0.f);
        fade.setDuration(1000);
        cardView.startAnimation(fade);
        cardView.postDelayed(new Runnable() {
            @Override
            public void run() {

                cardView.setVisibility(android.view.View.INVISIBLE);
                cardView.removeAllViews();
            }
        }, 1000);

        Animation appear = new AlphaAnimation(0.f, 1.f);
        appear.setDuration(100);
        cardView.postDelayed(new Runnable() {

            @Override
            public void run() {
                newWeatherBoxButton.startAnimation(appear);
                newWeatherBoxButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                addButton.startAnimation(appear);

                cardView.setX(55);


            }
        }, 1000);

    }

    public void exitAnimationOnlyAddButton(

    ) {

        Animation appear = new AlphaAnimation(0.f, 1.f);
        appear.setDuration(100);
        cardView.postDelayed(new Runnable() {

            @Override
            public void run() {
                newWeatherBoxButton.startAnimation(appear);
                newWeatherBoxButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                addButton.startAnimation(appear);

            }
        }, 1000);

    }



    public void newWeatherBox(WeatherReport newReport) {

        //Animation for new box appearing.
        Animation appear = new AlphaAnimation(0.f, 1.f);
        appear.setDuration(1500);

        //Animation to remove newWeatherBoxButton
        Animation fade = new AlphaAnimation(1.f, 0.f);
        fade.setDuration(500);

        newWeatherBoxButton.startAnimation(fade);
        addButton.startAnimation(fade);
        newWeatherBoxButton.postDelayed(new Runnable() {

            @Override
            public void run() {
                //cardView.setX(55);

                newWeatherBoxButton.setVisibility(View.INVISIBLE);
                addButton.setVisibility(View.INVISIBLE);
                cardView.startAnimation(appear);

            }
        }, 500);


        newWeatherBoxButton.postDelayed(new Runnable() {

            @Override
            public void run() {

                //cardView.setVisibility(View.VISIBLE);
            }
        }, 2000);


        cardView.getWeatherViews_location().setText(newReport.getLocationName_city());
        cardView.getWeatherViews_temperature().setText(newReport.getTemperature());
        //Set up gesture detector

    }

}
