package com.example.beweather;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.beweather.accounts.AccountFragment;
import com.example.beweather.accounts.AccountManager;
import com.example.beweather.model.WebViewModel;
import com.example.beweather.weathercontroller.Controller;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    BottomNavigationMenuView bottomNavigationMenu;

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

    AccountManager accountManager;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


    private ExecutorService backgroundThread = Executors.newSingleThreadExecutor();
    public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";
    private InterstitialAd mInterstitialAd;


    @SuppressLint({"ClickableViewAccessibility", "NewApi"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this);

        //get utilities set up
        model = WebViewModel.getWebViewModel(this);
        sharedPref = getSharedPreferences(GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        controller = Controller.getController(this);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.standard_text_color));

        accountManager = new AccountManager(this, model);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //Already logged in
        if (accountManager.checkIfLoggedIn()) { }

        //Logged into firebase, but not the local app/account
        else if (user != null) {

        }

        else if (model.getCurrentAccountFromModel() != null) {
            accountManager.setRecentUser(model.getCurrentAccountFromModel());
        }

        //Not logged in
        else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.parentLayout, AccountFragment.class, null)
                    .commit();

        }


        setContentView(R.layout.activity_main);


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
        //We'll add current account to make sure each user sees only their own chosen weather displays
        String wBoxId_1 = "wBox1"+accountManager.getCurrentAccount().getNickname();
        String wBoxId_2 = "wBox2"+accountManager.getCurrentAccount().getNickname();
        String wBoxId_3 = "wBox3"+accountManager.getCurrentAccount().getNickname();

        //Pass to views to WeatherBox
        weatherBox1 = new WeatherBox(this, this, controller, model, weatherSearchBar, weatherBoxView1, newWeatherBoxButton_1, addButton_1, wBoxId_1);
        weatherBox2 = new WeatherBox(this, this, controller, model, weatherSearchBar, weatherBoxView2, newWeatherBoxButton_2, addButton_2, wBoxId_2);
        weatherBox3 = new WeatherBox(this, this, controller, model, weatherSearchBar, weatherBoxView3, newWeatherBoxButton_3, addButton_3, wBoxId_3);

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

        findViewById(R.id.menu_option_account).setOnClickListener(View -> {

            weatherBoxView1.removeAllViews();
            weatherBoxView2.removeAllViews();
            weatherBoxView3.removeAllViews();
            weatherSearchBar.setVisibility(android.view.View.GONE);
            enterWeatherButton.setVisibility(android.view.View.GONE);
            addButton_1.setVisibility(android.view.View.GONE);
            addButton_2.setVisibility(android.view.View.GONE);
            addButton_3.setVisibility(android.view.View.GONE);
            newWeatherBoxButton_1.setVisibility(android.view.View.GONE);
            newWeatherBoxButton_2.setVisibility(android.view.View.GONE);
            newWeatherBoxButton_3.setVisibility(android.view.View.GONE);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            transaction.setReorderingAllowed(true);
            transaction.add(R.id.parentLayout, AccountFragment.class, null).commit();

        });

        findViewById(R.id.menu_option_weather).setOnClickListener(View -> {
            weatherBox1.generateWeatherBox();
            weatherBox2.generateWeatherBox();
            weatherBox3.generateWeatherBox();
            weatherSearchBar.setVisibility(android.view.View.VISIBLE);
            enterWeatherButton.setVisibility(android.view.View.VISIBLE);
            addButton_1.setVisibility(android.view.View.VISIBLE);
            addButton_2.setVisibility(android.view.View.VISIBLE);
            addButton_3.setVisibility(android.view.View.VISIBLE);
            newWeatherBoxButton_1.setVisibility(android.view.View.VISIBLE);
            newWeatherBoxButton_2.setVisibility(android.view.View.VISIBLE);
            newWeatherBoxButton_3.setVisibility(android.view.View.VISIBLE);


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            transaction.addToBackStack(null);
            transaction.remove(fragmentManager.findFragmentById(R.id.parentLayout)).commit();

        });

    }


    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

        }
    }



    public void triggerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        MobileAds.initialize(this);

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");

                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(MainActivity.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });


    }








}
