package com.example.beweather;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.beweather.R;

import javax.security.auth.callback.Callback;

public class WeatherDisplayPresets  {

    CardView cardView_1;
    ObjectAnimator objectAnimator;
    TextView currentLocation_temperature;
    TextView currentLocationName_cardView;
    ImageView weatherIcon;
    RelativeLayout relativeLayoutInCardView;
    ConstraintLayout parent;
    Button newWeatherBoxButton;
    public WeatherDisplayPresets() {

    }

    public void exitAnimation(Activity activity) {
        cardView_1 = activity.findViewById(R.id.cardView);
        newWeatherBoxButton = activity.findViewById(R.id.newWeatherBoxButton);

        objectAnimator = ObjectAnimator.ofFloat(cardView_1, "x", -600);
        objectAnimator.setDuration(1500);
        objectAnimator.start();

        Animation fade = new AlphaAnimation(1.f, 0.f);
        fade.setDuration(1000);
        cardView_1.startAnimation(fade);
        cardView_1.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardView_1.setVisibility(android.view.View.INVISIBLE);
            }
        }, 1000);

        Animation appear = new AlphaAnimation(0.f, 1.f);
        appear.setDuration(200);
        cardView_1.postDelayed(new Runnable() {

            @Override
            public void run() {
                newWeatherBoxButton.startAnimation(appear);
                newWeatherBoxButton.setVisibility(View.VISIBLE);

            }
        }, 2000);




    }





}
