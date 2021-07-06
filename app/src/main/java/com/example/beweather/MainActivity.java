package com.example.beweather;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MotionEventCompat;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.beweather.model.WebViewModel;
import com.example.beweather.weathercontroller.Controller;
import com.example.beweather.weatherdata.WeatherReport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    public Controller controller;
    SharedPreferences sharedPref;
    WebViewModel model;
    Button update_weather_button;
    Button update_screen_button;
    EditText weatherSearchBar;
    Button enterWeatherButton;

    Button newWeatherBoxButton_1;
    Button newWeatherBoxButton_2;
    Button newWeatherBoxButton_3;

    ImageView addButton_1;
    ImageView addButton_2;
    ImageView addButton_3;

    WeatherBoxDetailsView weatherBoxView1;
    WeatherBoxDetailsView weatherBoxView2;
    WeatherBoxDetailsView weatherBoxView3;

    WeatherBox weatherBox1;
    WeatherBox weatherBox2;
    WeatherBox weatherBox3;

    //WeatherIconView weatherIconView;


    private ExecutorService backgroundThread = Executors.newSingleThreadExecutor();
    public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";



    @SuppressLint({"ClickableViewAccessibility", "NewApi"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //get utilities set up
        model = new WebViewModel(this);
        sharedPref = getSharedPreferences(GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);


        controller = Controller.getController(this);
        //model.syncReportCache();
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.standard_text_color));
        /*
        WindowInsetsController windowControls = window.getInsetsController();
        windowControls.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH);

         */



/*
        //get the most recently searched-for weather, if it exists
        if (model.getCurrentWeatherReport() != null) {
            //Nothing, just set the weather display items
        } else if (sharedPref.getString("report1",null) != null) {
            model.syncReportCache();
        } else {
            controller.submitRequest("Dayton, OH", model);
        }

 */



        //Set up views
        newWeatherBoxButton_1 = findViewById(R.id.newWeatherBoxButton1);
        newWeatherBoxButton_2 = findViewById(R.id.newWeatherBoxButton2);
        newWeatherBoxButton_3 = findViewById(R.id.newWeatherBoxButton3);

        newWeatherBoxButton_1.setVisibility(View.INVISIBLE);
        newWeatherBoxButton_2.setVisibility(View.INVISIBLE);
        newWeatherBoxButton_3.setVisibility(View.INVISIBLE);

        addButton_1 = findViewById(R.id.addButton);
        addButton_1.setVisibility(View.INVISIBLE);
        addButton_2 = findViewById(R.id.addButton2);
        addButton_2.setVisibility(View.INVISIBLE);
        addButton_3 = findViewById(R.id.addButton3);
        addButton_3.setVisibility(View.INVISIBLE);


        weatherSearchBar = findViewById(R.id.currentLocationName_searchbar);
        enterWeatherButton = findViewById(R.id.enter_weather_button);
        enterWeatherButton.setOnClickListener(View -> {
            String location = weatherSearchBar.getText().toString();
            controller.submitRequest(location, model);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(View.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        });


        //The views/layouts that we'll pass to WeatherBox objects.
        weatherBoxView1 = findViewById(R.id.weatherBoxView1);
        weatherBoxView2 = findViewById(R.id.weatherBoxView2);
        weatherBoxView3 = findViewById(R.id.weatherBoxView3);

        //weatherBoxView1.setUpView(this);
        //weatherBoxView2.setUpView(this);
        //weatherBoxView3.setUpView(this);

        //Each weatherbox needs an ID, which will help it access its unique shared preferences file
        String wBoxId_1 = "wBox1";
        String wBoxId_2 = "wBox2";
        String wBoxId_3 = "wBox3";

        //Pass to views to WeatherBox
        weatherBox1 = new WeatherBox(this, controller, model, weatherSearchBar, weatherBoxView1, newWeatherBoxButton_1, addButton_1, wBoxId_1);
        weatherBox2 = new WeatherBox(this, controller, model, weatherSearchBar, weatherBoxView2, newWeatherBoxButton_2, addButton_2, wBoxId_2);
        weatherBox3 = new WeatherBox(this, controller, model, weatherSearchBar, weatherBoxView3, newWeatherBoxButton_3, addButton_3, wBoxId_3);

        weatherBoxView1.setLongClickable(true);
        weatherBoxView1.setOnTouchListener(new View.OnTouchListener() {

            private GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getApplicationContext(), new WeatherBoxGestureListener(weatherBox1));

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                this.gestureDetector.onTouchEvent(event);
                return false;
            }

        });

        weatherBoxView2.setLongClickable(true);
        weatherBoxView2.setOnTouchListener(new View.OnTouchListener() {


            private GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getApplicationContext(), new WeatherBoxGestureListener(weatherBox2));

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                this.gestureDetector.onTouchEvent(event);
                return false;
            }


        });

        weatherBoxView3.setLongClickable(true);
        weatherBoxView3.setOnTouchListener(new View.OnTouchListener() {

            private GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getApplicationContext(), new WeatherBoxGestureListener(weatherBox3));

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                this.gestureDetector.onTouchEvent(event);
                return false;
            }
        });

    }






    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

        }
    }






}
