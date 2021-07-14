package com.be.beweather;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

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
import com.be.beweather.accounts.AccountFragment;
import com.be.beweather.accounts.AccountManager;
import com.be.beweather.model.WebViewModel;
import com.be.beweather.weathercontroller.Controller;
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
    private Boolean ACCOUNT_FRAG_ON;
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
        model = WebViewModel.getWebViewModel(this, this);
        ACCOUNT_FRAG_ON = false;
        sharedPref = getSharedPreferences(GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        controller = Controller.getController(this);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.standard_text_color));
        accountManager = new AccountManager(this, model);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        setContentView(R.layout.activity_main);


        //Set up views
        newWeatherBoxButton_1 = findViewById(R.id.newWeatherBoxButton1);
        newWeatherBoxButton_2 = findViewById(R.id.newWeatherBoxButton2);
        newWeatherBoxButton_3 = findViewById(R.id.newWeatherBoxButton3);


        addButton_1 = findViewById(R.id.addButton);
        addButton_2 = findViewById(R.id.addButton2);
        addButton_3 = findViewById(R.id.addButton3);


        weatherSearchBar = findViewById(R.id.currentLocationName_searchbar);
        enterWeatherButton = findViewById(R.id.enter_weather_button);
        enterWeatherButton.setOnClickListener(View -> {
            String location = weatherSearchBar.getText().toString();
            controller.submitRequest(location, model);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(View.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            refreshWeatherBoxes();
        });

        //The views/layouts that we'll pass to WeatherBox objects.
        weatherBoxView1 = findViewById(R.id.weatherBoxView1);
        weatherBoxView2 = findViewById(R.id.weatherBoxView2);
        weatherBoxView3 = findViewById(R.id.weatherBoxView3);

        String wBoxId_1;
        String wBoxId_2;
        String wBoxId_3;

        try {

            wBoxId_1 = "wBox1"+model.getCurrentAccountFromModel();
            wBoxId_2 = "wBox2"+model.getCurrentAccountFromModel();
            wBoxId_3 = "wBox3"+model.getCurrentAccountFromModel();

        } catch (Exception e) {

            wBoxId_1 = "wBox1"+"temporary";
            wBoxId_2 = "wBox2"+"temporary";
            wBoxId_3 = "wBox3"+"temporary";
        }

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

            openAccountFragment();

        });

        findViewById(R.id.menu_option_weather).setOnClickListener(View -> {

            closeAccountFragment();

        });




        if (user == null) {
            System.out.println("user is null, so opening account fragment");
            openAccountFragment();

        }


        //Already logged in
        if (model.getCurrentAccountFromModel() != null) {

            try {
                System.out.println("user seems to be signed in, so not opening account fragment");

            } catch (Exception e) {
                System.out.println("no current account in model, so opening account fragment");
                openAccountFragment();

            }

        }   //Not logged in
            else {

            System.out.println("account info not reached, so opening account fragment");
            openAccountFragment();


            }


            


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

    public void openAccountFragment() {
        if (!ACCOUNT_FRAG_ON) {
            ACCOUNT_FRAG_ON = true;
            weatherBox1.setWeatherBoxDisplay(WeatherBox.MODE_INVISIBLE);
            weatherBox2.setWeatherBoxDisplay(WeatherBox.MODE_INVISIBLE);
            weatherBox3.setWeatherBoxDisplay(WeatherBox.MODE_INVISIBLE);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            transaction.setReorderingAllowed(true);
            transaction.add(R.id.parentLayout, AccountFragment.class, null).commit();

        }
    }

    public void closeAccountFragment() {
        if (ACCOUNT_FRAG_ON) {
            ACCOUNT_FRAG_ON = false;
            weatherBox1.alternateDisplayType();
            weatherBox2.alternateDisplayType();
            weatherBox3.alternateDisplayType();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            transaction.addToBackStack(null);
            transaction.remove(fragmentManager.findFragmentById(R.id.parentLayout)).commit();
        }
    }


    //Sometimes the views show through on the accountfragment.
    //This will check to make sure the weather views are not visible.
    public void checkForBugsWithView() {
        if (ACCOUNT_FRAG_ON) {


        }
    }



    public void refreshWeatherBoxes() {
        String wBoxId_1;
        String wBoxId_2;
        String wBoxId_3;

        try {

            wBoxId_1 = "wBox1"+model.getCurrentAccountFromModel();
            wBoxId_2 = "wBox2"+model.getCurrentAccountFromModel();
            wBoxId_3 = "wBox3"+model.getCurrentAccountFromModel();

        } catch (Exception e) {

            wBoxId_1 = "wBox1"+"temporary";
            wBoxId_2 = "wBox2"+"temporary";
            wBoxId_3 = "wBox3"+"temporary";
        }


        weatherBox1 = new WeatherBox(this, this, controller, model, weatherSearchBar, weatherBoxView1, newWeatherBoxButton_1, addButton_1, wBoxId_1);
        weatherBox2 = new WeatherBox(this, this, controller, model, weatherSearchBar, weatherBoxView2, newWeatherBoxButton_2, addButton_2, wBoxId_2);
        weatherBox3 = new WeatherBox(this, this, controller, model, weatherSearchBar, weatherBoxView3, newWeatherBoxButton_3, addButton_3, wBoxId_3);
    }


}
