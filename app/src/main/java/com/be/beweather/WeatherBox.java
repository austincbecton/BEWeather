package com.be.beweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.be.beweather.model.WebViewModel;
import com.be.beweather.weathercontroller.Controller;
import com.be.beweather.weatherdata.WeatherImageProvider;
import com.be.beweather.weatherdata.WeatherReport;

import java.util.Random;

public class WeatherBox {

        private Context context;
        private Controller controller;
        private WeatherBoxDetailsView weatherBoxDetailsView;
        private ImageView weatherIcon;
        private Button newWeatherBoxButton;
        private TextView currentLocation_temperature;
        private TextView currentLocation_name;
        private ImageView addWeatherBoxButton;
        private WeatherDisplayPresets weatherDisplayPresets;
        private WebViewModel model;
        private EditText weatherSearchBar;
        private WeatherReport thisWeatherBoxWeatherReport;
        private String currentDisplayType;
        protected String thisWBoxId;
        private SharedPreferences sharedPref;
        public static String GLOBAL_SHARED_PREFERENCES = "global_shared_preferences";
        private String TAG_location;
        public MainActivity mainActivity;
        public static final String MODE_STANDARD = "standard_weatherBoxView";
        public static final String MODE_DETAIL = "detail_weatherBoxView";
        public static final String MODE_INVISIBLE = "no_weatherBoxView";
        public static final String MODE_ENTER = "enter_weatherBoxView";

    //constructor needs views added from the activity
        public WeatherBox(
                Context context, MainActivity mainActivity, Controller controller, WebViewModel model,
                EditText weatherSearchBar, WeatherBoxDetailsView weatherBoxDetailsView,
                Button newWeatherBoxButton, ImageView addWeatherBoxButton, String wBoxId
        )
        {
            this.context = context;
            this.mainActivity = mainActivity;
            this.controller = controller;
            this.model = model;
            this.TAG_location = "wBox" + wBoxId + "location";
            sharedPref = context.getSharedPreferences(GLOBAL_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            this.weatherSearchBar = weatherSearchBar;
            this.currentDisplayType = "none";
            this.weatherBoxDetailsView = weatherBoxDetailsView;
            this.weatherBoxDetailsView.setUpView(context);
            this.newWeatherBoxButton = newWeatherBoxButton;
            this.newWeatherBoxButton.setVisibility(View.INVISIBLE);
            this.currentLocation_temperature = weatherBoxDetailsView.getWeatherViews_temperature();
            this.currentLocation_name = weatherBoxDetailsView.getWeatherViews_location();
            this.addWeatherBoxButton = addWeatherBoxButton;
            this.weatherDisplayPresets =

                    new WeatherDisplayPresets(context, weatherBoxDetailsView,
                            newWeatherBoxButton,
                            addWeatherBoxButton);

            this.thisWBoxId = wBoxId;

            this.thisWeatherBoxWeatherReport = new WeatherReport(sharedPref.getString(TAG_location, "empty"), "");


            if (this.thisWeatherBoxWeatherReport.getLocationName_city().equals("empty")) {
                this.thisWeatherBoxWeatherReport = model.getRecentReport();
                if (this.thisWeatherBoxWeatherReport.getLocationName_city() == null) {
                    this.thisWeatherBoxWeatherReport = new WeatherReport("empty", "empty");
                }
            } else {
                controller.submitRequest(thisWeatherBoxWeatherReport.getLocationName_city(), model);
                this.thisWeatherBoxWeatherReport = model.getRecentReport();
            }


            this.newWeatherBoxButton.setOnClickListener(View ->{

                if (!weatherSearchBar.getText().toString().isEmpty()) {
                    controller.submitRequest(weatherSearchBar.getText().toString(), model);
                    this.thisWeatherBoxWeatherReport = model.getRecentReport();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(TAG_location, weatherSearchBar.getText().toString());
                    editor.apply();
                    weatherDisplayPresets.exitAnimationOnlyAddButton();
                    currentDisplayType = "exited";
                    alternateDisplayType();
                    //setWeatherBoxDisplay(MODE_STANDARD);

                } else {
                    System.out.println("New weather box button has been clicked");
                    currentDisplayType = "exited";
                    alternateDisplayType();
                    setWeatherBoxDisplay(MODE_STANDARD);

                }
            });


            setWeatherBoxDisplay(MODE_STANDARD);




        }


    public void triggerAd() {
        Random rand = new Random();
        int upperbound = 10;
        if (rand.nextInt(upperbound) < 3) {
            mainActivity.triggerAd();
        }

    }



    public void setWeatherBoxDisplay(String mode) {

            switch (mode){
                case MODE_DETAIL:
                    currentDisplayType = "details";
                    System.out.println("MODE_DETAIL SELECTED");
                    //remove standard weather views
                    try {
                        weatherBoxDetailsView.setVisibility(View.VISIBLE);
                        weatherBoxDetailsView.hideStandardView();
                        weatherBoxDetailsView.alterViewLayout_detailView(context);
                        addWeatherBoxButton.setVisibility(View.INVISIBLE);
                        newWeatherBoxButton.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        System.out.println("Error hiding standard or building detail views.");
                    }

                    break;
                case MODE_INVISIBLE:
                    currentDisplayType = "none";

                    try {

                        System.out.println("Mode_invisible being set.");
                        weatherBoxDetailsView.setVisibility(View.INVISIBLE);
                        weatherBoxDetailsView.hideDetailsView();
                        weatherBoxDetailsView.hideStandardView();
                        addWeatherBoxButton.setVisibility(View.INVISIBLE);
                        newWeatherBoxButton.setVisibility(View.INVISIBLE);


                    }catch (Exception e) {
                        System.out.println("Error hiding weather views.");
                    }
                    break;


                case MODE_STANDARD:
                    currentDisplayType = "standard";
                    if (thisWeatherBoxWeatherReport.getLocationName_city() == null ||
                            thisWeatherBoxWeatherReport.getLocationName_city().equals("null") ||
                            thisWeatherBoxWeatherReport.getLocationName_city().equals("empty") ||
                            thisWeatherBoxWeatherReport.getLocationName_city().equals("Unknown City"))
                    {
                        try {
                            System.out.println("MODE_STANDARD selected, but report is null.");
                            weatherBoxDetailsView.setVisibility(View.INVISIBLE);
                            try {weatherBoxDetailsView.hideDetailsView();
                                weatherBoxDetailsView.hideStandardView();}
                            catch (Exception e) {System.out.println("No views to close.");}
                            addWeatherBoxButton.setVisibility(View.VISIBLE);
                            newWeatherBoxButton.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            //
                        }


                    } else {
                        System.out.println("MODE_STANDARD selected, report is for: " +
                                thisWeatherBoxWeatherReport.getLocationName_city());
                        addWeatherBoxButton.setVisibility(View.INVISIBLE);
                        newWeatherBoxButton.setVisibility(View.INVISIBLE);

                        weatherBoxDetailsView.alterViewLayout_standardView(context);
                        weatherBoxDetailsView.setVisibility(View.VISIBLE);
                        weatherBoxDetailsView.getWeatherViews_location().setVisibility(View.VISIBLE);
                        weatherBoxDetailsView.getWeatherViews_temperature().setVisibility(View.VISIBLE);
                        weatherBoxDetailsView.getWeatherIcon().setVisibility(View.VISIBLE);

                        //Setting content for each view
                        WeatherImageProvider weatherImageProvider = new WeatherImageProvider();
                        Integer drawableId = weatherImageProvider.getWeatherIconId(thisWeatherBoxWeatherReport);
                        Integer backgroundId = weatherImageProvider.getWeatherBackgroundId(thisWeatherBoxWeatherReport);
                        weatherBoxDetailsView.getWeatherIcon().setBackgroundResource(drawableId);
                        weatherBoxDetailsView.getStandardViewLayoutBackground().setBackgroundResource(backgroundId);
                        weatherBoxDetailsView.getWeatherViews_location().setText(
                                thisWeatherBoxWeatherReport.getLocationName_city());
                        weatherBoxDetailsView.getWeatherViews_temperature().setText(
                                thisWeatherBoxWeatherReport.getTemperature());


                    }


                    break;
                case MODE_ENTER:
                    System.out.println("MODE_ENTER selected, report is for: " +
                            thisWeatherBoxWeatherReport.getLocationName_city());
                    addWeatherBoxButton.setVisibility(View.INVISIBLE);
                    newWeatherBoxButton.setVisibility(View.INVISIBLE);

                    weatherBoxDetailsView.alterViewLayout_standardView(context);
                    //weatherBoxDetailsView.setVisibility(View.VISIBLE);
                    //weatherBoxDetailsView.getWeatherViews_location().setVisibility(View.VISIBLE);
                    //weatherBoxDetailsView.getWeatherViews_temperature().setVisibility(View.VISIBLE);
                    //weatherBoxDetailsView.getWeatherIcon().setVisibility(View.VISIBLE);
                    weatherDisplayPresets.newWeatherBox(thisWeatherBoxWeatherReport);

                    try {
                        WeatherImageProvider weatherImageProvider = new WeatherImageProvider();
                        Integer drawableId = weatherImageProvider.getWeatherIconId(thisWeatherBoxWeatherReport);
                        Integer backgroundId = weatherImageProvider.getWeatherBackgroundId(thisWeatherBoxWeatherReport);
                        weatherBoxDetailsView.getWeatherIcon().setBackgroundResource(drawableId);
                        weatherBoxDetailsView.getStandardViewLayoutBackground().setBackgroundResource(backgroundId);

                    } catch (Exception e) {
                        System.out.println("Error getting icon");
                    }
                    //Setting content for each view
                    weatherBoxDetailsView.getWeatherViews_location().setText(
                            thisWeatherBoxWeatherReport.getLocationName_city());
                    weatherBoxDetailsView.getWeatherViews_temperature().setText(
                            thisWeatherBoxWeatherReport.getTemperature());
                    break;



                default:

            }


    }


    public void flingWeatherBox(){
        currentDisplayType = "exited";
        weatherDisplayPresets.exitAnimation();

    }

    public void alternateDisplayType() {

            if (currentDisplayType.equals("none")) {
                System.out.println("Changing display type from none to standard");
                weatherBoxDetailsView.removeAllViews();
                setWeatherBoxDisplay(MODE_STANDARD);
            } else if (currentDisplayType.equals("details")) {
                System.out.println("Changing display type from details to standard");
                weatherBoxDetailsView.removeAllViews();
                setWeatherBoxDisplay(MODE_STANDARD);
            } else if (currentDisplayType.equals("standard")) {
                System.out.println("Changing display type from standard to detail");
                weatherBoxDetailsView.removeAllViews();
                setWeatherBoxDisplay(MODE_DETAIL);
            } else if (currentDisplayType.equals("exited")) {
                System.out.println("Changing display type from exited to none");
                weatherBoxDetailsView.removeAllViews();
                setWeatherBoxDisplay(MODE_ENTER);
            } else if (currentDisplayType.equals("ready")) {
                System.out.println("Changing display type from ready to standard");
            }

    }

    public void longPressChangeViewType() {
            if (currentDisplayType.equals("ready") || currentDisplayType.equals("exited")) {
                currentDisplayType = "standard";
            }

            alternateDisplayType();
    }

}
