package com.be.beweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import com.be.beweather.MainActivity;
import com.be.beweather.WeatherBoxDetailsView;
import com.be.beweather.WeatherDisplayPresets;
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
        private static final String TAG = "WeatherBox";

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
            //Unique id to help identify this box's saved/persisted data
            this.thisWBoxId = wBoxId;

            //this variable stores this box's weather data, which the views will extract
            this.thisWeatherBoxWeatherReport = new WeatherReport(sharedPref.getString(TAG_location, "empty"), "");
            this.thisWeatherBoxWeatherReport.updateWeatherReport("", "",
                    "", "", "", "", "", "");

            //See if we currently have a report set up
            //checks for 'empty' because we previously set it to empty if sharedpref returns nothing
            if (this.thisWeatherBoxWeatherReport.getLocationName_city().equals("empty")) {
                this.thisWeatherBoxWeatherReport = model.getRecentReport();
                if (this.thisWeatherBoxWeatherReport.getLocationName_city() == null) {
                    this.thisWeatherBoxWeatherReport = new WeatherReport("empty", "empty");
                }

                //set up observer
                observeCurrentData();
            } else {

                //Otherwise, we'll submit a new request via volley to get the latest weather data.
                controller.submitRequest(thisWeatherBoxWeatherReport.getLocationName_city(), model);
                //Set up report as empty for now.
                this.thisWeatherBoxWeatherReport = new WeatherReport(
                        thisWeatherBoxWeatherReport.getLocationName_city(),
                        thisWeatherBoxWeatherReport.getLocationName_country());

                //avoid null pointers later on by creating empty data here.
                this.thisWeatherBoxWeatherReport.updateWeatherReport(
                        "", "", "", "", "",
                        "", "", "");

                //set up observer
                observeCurrentData();

            }

            //When currently no weather set up, users can press this button to set up a "new"
            // weather box. It's really the same weather box, but we'll get a new batch of data.
            this.newWeatherBoxButton.setOnClickListener(View ->{

                if (!weatherSearchBar.getText().toString().isEmpty()) {

                    //submit here for new weather
                    controller.submitRequest(weatherSearchBar.getText().toString(), model);
                    this.thisWeatherBoxWeatherReport = model.getRecentReport();

                    //save the current city/location into the model for later use
                    //We have to split the string bc user will be including region/country,
                    //but we only want the city.
                    String rawLocation = weatherSearchBar.getText().toString();
                    String delims = "[ ,]+";
                    String[] parsedLocation = rawLocation.split(delims);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(TAG_location, parsedLocation[0]);
                    editor.apply();

                    //Get rid of the add button so it doesn't appear through other views or frags
                    weatherDisplayPresets.exitAnimationOnlyAddButton();

                    //this must be set to 'exited' because it will impact what happens in the
                    // alternateDisplayType() method.
                    currentDisplayType = "exited";
                    alternateDisplayType();

                    //Setting an observer to keep updated
                    observeCurrentData();

                } else {

                    System.out.println("New weather box button has been clicked");
                    currentDisplayType = "exited";
                    alternateDisplayType();
                    setWeatherBoxDisplay(MODE_STANDARD);

                }
            });
            System.out.println("WEATHER BOX: "+thisWBoxId+" has previous saved location as: " +
                    thisWeatherBoxWeatherReport.getLocationName_city());
            setWeatherBoxDisplay(MODE_STANDARD);
            
            //We'll check and make sure this box has been properly updated. Since we set "" as
            //humidity by default, we know if this box has been updated. If it equals "", we'll submit
            //a request, and this wbox's observer will update the box.

            try {
                if (thisWeatherBoxWeatherReport.getHumidity().equals("")) {
                    controller.submitRequest(thisWeatherBoxWeatherReport.getLocationName_city(), model);
                }
            } catch (Exception e) {
                System.out.println("WeatherBox: thisWeatherReport may contain null data");
            }


        }


    public void triggerAd() {
        Random rand = new Random();
        int upperbound = 10;
        if (rand.nextInt(upperbound) < 2) {
            mainActivity.triggerAd();
        }

    }


    //This is called by alternate_view methods and will select
    // appropriate methods from WeatherDisplayPresets for setting views and visibility.
    public void setWeatherBoxDisplay(String mode) {

            switch (mode){
                case MODE_DETAIL:
                    currentDisplayType = "details";
                    System.out.println("MODE_DETAIL SELECTED");
                    //remove standard weather views
                    try {


                        weatherDisplayPresets.weatherViewVisibility_hideStandardShowDetails();
/*
                        weatherBoxDetailsView.setVisibility(View.VISIBLE);
                        weatherBoxDetailsView.hideStandardView();
                        weatherBoxDetailsView.alterViewLayout_detailView(context);

 */
                        weatherDisplayPresets.weatherViewVisibility_hideAddWeatherButtons();
                        /*
                        addWeatherBoxButton.setVisibility(View.INVISIBLE);
                        newWeatherBoxButton.setVisibility(View.INVISIBLE);
                         */

                    } catch (Exception e) {
                        System.out.println("Error hiding standard or building detail views.");
                    }


                    try {
                        //setting data to each view
                        weatherBoxDetailsView.getWeatherViews_cityName().setText(
                                thisWeatherBoxWeatherReport.getLocationName_city()
                        );
                        weatherBoxDetailsView.getWeatherViews_tonight().setText(
                                thisWeatherBoxWeatherReport.getTonight()
                        );
                        weatherBoxDetailsView.getWeatherViews_humidity().setText(
                                thisWeatherBoxWeatherReport.getHumidity()
                        );
                        weatherBoxDetailsView.getWeatherViews_conditions().setText(
                                thisWeatherBoxWeatherReport.getSkyCondition()
                        );

                    } catch (Exception e) {
                        System.out.println("Error while setting details view: either getting" +
                                "details from report or getting views");

                    }

                    try {
                        WeatherImageProvider weatherImageProvider = new WeatherImageProvider();
                        Integer backgroundId = weatherImageProvider.getWeatherBackgroundId(thisWeatherBoxWeatherReport);
                        weatherBoxDetailsView.getDetailsViewLayoutBackground().setBackgroundResource(backgroundId);


                    } catch (Exception e) {
                        System.out.println("Error getting background image in detail view.");
                    }



                    break;
                case MODE_INVISIBLE:
                    currentDisplayType = "none";

                    try {

                        System.out.println("Mode_invisible being set.");
                        weatherDisplayPresets.weatherViewVisibilty_hideWeatherBox();
                        weatherDisplayPresets.weatherViewVisibility_hideAddWeatherButtons();

                    }catch (Exception e) {
                        System.out.println("Error hiding weather views.");
                    }
                    break;

                case MODE_STANDARD:
                    currentDisplayType = "standard";
                    //First, see if we currently have any saved locations for this wbox.
                    //This is important when box first instantiated upon startup in main activity

                    if (thisWeatherBoxWeatherReport.getLocationName_city() == null ||
                            thisWeatherBoxWeatherReport.getLocationName_city().equals("null") ||
                            thisWeatherBoxWeatherReport.getLocationName_city().equals("empty") ||
                            thisWeatherBoxWeatherReport.getLocationName_city().equals("Unknown City"))
                    {
                        try {

                            System.out.println("MODE_STANDARD selected, but report is null.");
                            weatherDisplayPresets.weatherViewVisibilty_hideWeatherBox();
                            weatherDisplayPresets.weatherViewVisibility_showAddWeatherButtons();

                        } catch (Exception e) {
                            //
                        }

                    } else {
                        System.out.println("MODE_STANDARD selected, report is for: " +
                                thisWeatherBoxWeatherReport.getLocationName_city());

                        //Remove add buttons visibility
                        weatherDisplayPresets.weatherViewVisibility_hideAddWeatherButtons();

                        //inflate standard view layout, make sure views visible
                        weatherDisplayPresets.weatherViewVisibility_showStandardView();

                        //setting data to each view
                        weatherDisplayPresets.dataBinding_standardViewSetUp(thisWeatherBoxWeatherReport);


                    }

                    break;
                case MODE_ENTER:
                    System.out.println("MODE_ENTER selected, report is for: " +
                            thisWeatherBoxWeatherReport.getLocationName_city());

                    weatherDisplayPresets.weatherViewVisibility_hideAddWeatherButtons();
                    weatherBoxDetailsView.alterViewLayout_standardView(context);
                    weatherDisplayPresets.newWeatherBox(thisWeatherBoxWeatherReport);

                    try {
                        weatherDisplayPresets.dataBinding_standardViewSetUp(thisWeatherBoxWeatherReport);
                    } catch (Exception e) {
                        Log.e(TAG, "MODE_ENTER: Error setting views for the weatherbox.");
                    }

                    /*


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

                            */
                    break;



                default:

            }



    }


    //called by gesture detector, this lets viewer swipe away/close the weatherbox
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



    public void observeCurrentData() {
        model.getRecentReport_location().observe(mainActivity, new Observer<String>() {
            @Override
            public void onChanged(String updatedLocation) {
                System.out.println("OBSERVER CALLED, PICKED UP IN WBOX"+
                        "\nThis weather report city: " + thisWeatherBoxWeatherReport.getLocationName_city()+
                        "\nObserved city from vmodel is: " + updatedLocation);
                try {
                    if (thisWeatherBoxWeatherReport.getLocationName_city().equals(updatedLocation)) {
                        thisWeatherBoxWeatherReport = model.getRecentReport();
                        currentDisplayType = "details";
                        System.out.println("Observer(wbox): Updating weather display due to city match");
                        alternateDisplayType();
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred updating weatherbox. It may be set to null, " +
                            "so this can be ignored.");
                }

            }
        });

    }


    public String getThisWBoxId() {
        return thisWBoxId;
    }

    public void setThisWBoxId(String thisWBoxId) {
        this.thisWBoxId = thisWBoxId;

    }

    public void refresh(String newWboxId) {
        //first, removing city from the cache so we don't waste memory
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(this.TAG_location);
        editor.commit();

        //new wbox id for saving in sharedpref
        this.thisWBoxId = newWboxId;
        this.TAG_location = "wBox" + thisWBoxId + "location";
        //this variable stores this box's weather data, which the views will extract
        this.thisWeatherBoxWeatherReport = new WeatherReport(sharedPref.getString(TAG_location, "empty"), "");
        this.thisWeatherBoxWeatherReport.updateWeatherReport("", "",
                "", "", "", "", "", "");

        //See if we currently have a report set up
        //checks for 'empty' because we previously set it to empty if sharedpref returns nothing
        if (this.thisWeatherBoxWeatherReport.getLocationName_city().equals("empty")) {
            currentDisplayType = "exited";
            weatherDisplayPresets.showAddWeatherButtons();
            /*
            this.thisWeatherBoxWeatherReport = model.getRecentReport();
            if (this.thisWeatherBoxWeatherReport.getLocationName_city() == null) {
                this.thisWeatherBoxWeatherReport = new WeatherReport("empty", "empty");
            }



            //set up observer
            observeCurrentData();
            */

        } else {

            //Otherwise, we'll submit a new request via volley to get the latest weather data.
            controller.submitRequest(thisWeatherBoxWeatherReport.getLocationName_city(), model);
            //Set up report as empty for now.
            this.thisWeatherBoxWeatherReport = new WeatherReport(
                    thisWeatherBoxWeatherReport.getLocationName_city(),
                    thisWeatherBoxWeatherReport.getLocationName_country());

            //avoid null pointers later on by creating empty data here.
            this.thisWeatherBoxWeatherReport.updateWeatherReport(
                    "", "", "", "", "",
                    "", "", "");}
    }



}
