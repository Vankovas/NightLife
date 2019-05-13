package nl.fontys.ivan.valentin.nightlife;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceDetails;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.Manifest.*;
import static java.util.Locale.GERMAN;

import com.google.maps.model.OpeningHours.Period;


enum DaysOfTheWeek {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
    THURSDAY, FRIDAY, SATURDAY
}

public class MainActivity extends AppCompatActivity implements Serializable {
    //Authentication
    private User userInfo = new User(); //Needed in every class
    private FirebaseUser user; //Needed in every class
    private FirebaseAuth mAuth; //Needed in every class
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users"); //Needed in every class;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle = null;

    // time picker
    static final int TIME_DIALOG_ID = 1111;
    private TextView view;
    private TextView viewEnd;
    public TextView startTimeClick;
    public TextView endTimeClick;
    private int hr, hrEnd;
    private int min, minEnd;

    // Swipe gesture

    private GestureDetectorCompat mDetector;

    // long/lat
    private double longitude;
    private double latitude;


    // compare ratings of a place
    public static Comparator<PlaceDetail> placeRating = new Comparator<PlaceDetail>() {

        public int compare(PlaceDetail s1, PlaceDetail s2) {

            Double rating = s1.rating;
            Double rating2 = s2.rating;

            /*For descending order*/
            return (int) Math.round(rating2 - rating);

        }
    };


    // Main screen switch

    private boolean isTimeToParty = false;

    public void switchMainScreen() {
        isTimeToParty = !isTimeToParty;

        if (isTimeToParty == true) {

            Button theButton = findViewById(R.id.button);
            Button surpriseMe = findViewById(R.id.surpriseMe);
            RelativeLayout r = findViewById(R.id.d);
            r.setBackgroundResource(R.color.backgroundMain2);
            theButton.setBackgroundResource(R.drawable.time_to_party_btn);
            surpriseMe.setBackgroundResource(R.color.surpriseMeTimeToParty);
            theButton.setText("TIME TO PARTY");

            LinearLayout l = findViewById(R.id.s);
            l.setBackgroundResource(R.drawable.party);

            TextView t = findViewById(R.id.mainTime);
            t.setBackgroundResource(R.color.backgroundTimeTimeToParty);
        } else {
            Button theButton = findViewById(R.id.button);
            Button surpriseMe = findViewById(R.id.surpriseMe);
            RelativeLayout r = findViewById(R.id.d);
            r.setBackgroundResource(R.color.backgroundMain);
            theButton.setBackgroundResource(R.drawable.time_to_party_btn_background);
            surpriseMe.setBackgroundResource(R.color.surpriseMeTimeToDine);
            theButton.setText("TIME TO DINE");

            LinearLayout l = findViewById(R.id.s);
            l.setBackgroundResource(R.drawable.dine);
            TextView t = findViewById(R.id.mainTime);
            t.setBackgroundResource(R.color.backgroundTime);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if logged in
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) startActivity(new Intent(MainActivity.this, StartupScreen.class));


        // Current location permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{permission.ACCESS_FINE_LOCATION},
                    1);
        }


        LocalTime timeNow = LocalTime.now();
        LocalTime timeSwitchParty = LocalTime.of(22, 0);
        LocalTime timeSwitchDine = LocalTime.of(5, 0);
        if (timeNow.isAfter(timeSwitchParty) || timeNow.equals(timeSwitchParty)) {
            if (isTimeToParty == false) {
                switchMainScreen();
            }
        }
        if (timeNow.isAfter(timeSwitchDine) || timeNow.equals(timeSwitchDine)) {
            if (isTimeToParty == true) {
                switchMainScreen();
            }
        }

        longitude = 5.4697225;
        latitude = 51.441642;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 3000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        // Places service
        final PlacesService ps = new PlacesService();

        // Check


        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        drawerToggle.syncState();
        drawerToggle.setDrawerIndicatorEnabled(true);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);


        // Check


        // Time picker
        view = findViewById(R.id.lbStartTime);
        viewEnd = findViewById(R.id.lbEndTime);
        final Calendar c = Calendar.getInstance();
        hr = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        hrEnd = hr;
        minEnd = min;

        //updates clock main screen
        final TextView mainTime = findViewById(R.id.mainTime);
        final Handler timeHandler = new Handler(getMainLooper());
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mainTime.setText("It's " + new SimpleDateFormat("HH:mm", GERMAN).format(new Date()));
                timeHandler.postDelayed(this, 1000);
            }
        }, 10);

        updateTime(hr, min);
        updateTimeEnd(hrEnd, minEnd);
        startTimeOnClick();
        endTimeOnClick();


        // Search places button
        final Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
                Toast.makeText(MainActivity.this, "No restaurants found close by.",
                        Toast.LENGTH_LONG).show();
            }
        };


        final Button theButton = findViewById(R.id.button);
        theButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {

                            ArrayList<Place> places = new ArrayList<>();
                            ArrayList<PlaceDetail> placeDetailsList = new ArrayList<>();

                            // Searching places by the current locaiton


                            if (isTimeToParty == false) {
                                Spinner aSpinner = findViewById(R.id.meansOfTransport);
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(0)) {
                                    places = ps.search(latitude, longitude, 500, "restaurant");
                                }
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(1)) {
                                    places = ps.search(latitude, longitude, 1000, "restaurant");
                                }
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(2)) {
                                    places = ps.search(latitude, longitude, 5000, "restaurant");
                                }
                            } else {
                                Spinner aSpinner = findViewById(R.id.meansOfTransport);
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(0)) {
                                    places = ps.search(latitude, longitude, 500, "night_club");
                                }
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(1)) {
                                    places = ps.search(latitude, longitude, 1000, "night_club");
                                }
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(2)) {
                                    places = ps.search(latitude, longitude, 5000, "night_club");
                                }
                            }

                            //https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&fields=name,rating,formatted_phone_number&key=AIzaSyDSbsYlKXUjSUCN1G6bfDM3npr1rcokkx0


                            GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyDSbsYlKXUjSUCN1G6bfDM3npr1rcokkx0").build();

                            LocalTime startOpenTime = LocalTime.of(hr, min);
                            LocalTime endOpenTime = LocalTime.of(hrEnd, minEnd);

                            for (Place place : places
                                    ) {

                                PlaceDetails placeDetails = PlacesApi.placeDetails(context, place.place_id).await();
                                // when the place is always open

                                if (placeDetails != null && placeDetails.openingHours != null && placeDetails.openingHours.periods != null) {

                                    if (placeDetails.openingHours.periods.length == 1) {

                                        placeDetailsList.add(ps.getDetails(place.place_id));
                                        placeDetailsList.get(placeDetailsList.size() - 1).alwaysOpen = true;

                                        if (placeDetails.photos != null) {
                                            if (placeDetails.photos.length != 0) {
                                                ArrayList<String> photoReferences = new ArrayList<>();
                                                for (Photo ph : placeDetails.photos
                                                        ) {
                                                    photoReferences.add(ph.photoReference);
                                                }
                                                placeDetailsList.get(placeDetailsList.size() - 1).photoReference = photoReferences;
                                            }
                                        }

                                    } else if (placeDetails.openingHours.periods.length == 7) {


                                        LocalTime placeOpenTime = placeDetails.openingHours.periods[c.get(Calendar.DAY_OF_WEEK) - 1].open.time;
                                        LocalTime placeCloseTime = placeDetails.openingHours.periods[c.get(Calendar.DAY_OF_WEEK) - 1].close.time;


                                        // checks if open in the selected time by the user
                                        if (placeOpenTime.isBefore(startOpenTime) &&
                                                placeCloseTime.isAfter(endOpenTime) ||
                                                placeOpenTime.equals(startOpenTime) &&
                                                        placeCloseTime.equals(endOpenTime) ||
                                                placeOpenTime.equals(startOpenTime) &&
                                                        placeCloseTime.isAfter(endOpenTime) ||
                                                placeOpenTime.isBefore(startOpenTime) &&
                                                        placeCloseTime.equals(endOpenTime)
                                                ) {

                                            placeDetailsList.add(ps.getDetails(place.place_id));
                                            placeDetailsList.get(placeDetailsList.size() - 1).alwaysOpen = false;
                                            placeDetailsList.get(placeDetailsList.size() - 1).openTime = placeOpenTime;
                                            placeDetailsList.get(placeDetailsList.size() - 1).closeTime = placeCloseTime;

                                            if (placeDetails.photos != null) {
                                                if (placeDetails.photos.length != 0) {
                                                    ArrayList<String> photoReferences = new ArrayList<>();
                                                    for (Photo ph : placeDetails.photos
                                                            ) {
                                                        photoReferences.add(ph.photoReference);
                                                    }
                                                    placeDetailsList.get(placeDetailsList.size() - 1).photoReference = photoReferences;
                                                }
                                            }

                                        }
                                    } else {

                                        for (Period p : placeDetails.openingHours.periods
                                                ) {
                                            String tempDay = "";
                                            tempDay = p.open.day.getName();


                                            LocalTime placeOpenTime = p.open.time;
                                            LocalTime placeCloseTime = p.close.time;
                                            if (tempDay.equals(DaysOfTheWeek.values()[c.get(Calendar.DAY_OF_WEEK) - 1])) {

                                                if (placeOpenTime.isBefore(startOpenTime) &&
                                                        placeCloseTime.isAfter(endOpenTime) ||
                                                        placeOpenTime.equals(startOpenTime) &&
                                                                placeCloseTime.equals(endOpenTime) ||
                                                        placeOpenTime.equals(startOpenTime) &&
                                                                placeCloseTime.isAfter(endOpenTime) ||
                                                        placeOpenTime.isBefore(startOpenTime) &&
                                                                placeCloseTime.equals(endOpenTime)
                                                        ) {

                                                    placeDetailsList.add(ps.getDetails(place.place_id));
                                                    placeDetailsList.get(placeDetailsList.size() - 1).alwaysOpen = false;
                                                    placeDetailsList.get(placeDetailsList.size() - 1).openTime = placeOpenTime;
                                                    placeDetailsList.get(placeDetailsList.size() - 1).closeTime = placeCloseTime;

                                                    if (placeDetails.photos != null) {
                                                        if (placeDetails.photos.length != 0) {
                                                            ArrayList<String> photoReferences = new ArrayList<>();
                                                            for (Photo ph : placeDetails.photos
                                                                    ) {
                                                                photoReferences.add(ph.photoReference);
                                                            }
                                                            placeDetailsList.get(placeDetailsList.size() - 1).photoReference = photoReferences;
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }

                            }


                            // Sorting restourants by rating


                            if (placeDetailsList.size() != 0) {
                                Collections.sort(placeDetailsList, placeRating);
                            } else {
                                Message message = mHandler.obtainMessage(0);
                                message.sendToTarget();
                            }

//                       //      Selecting the 3 top rated restaurants

                            if (placeDetailsList.size() > 3)
                                placeDetailsList.subList(2, placeDetailsList.size() - 1).clear();

                            if (placeDetailsList.size() >= 1 && placeDetailsList.size() <= 3 && placeDetailsList != null) {
                                Intent intent = new Intent(MainActivity.this, SuggestedPlace1.class);
                                Bundle args = new Bundle();
                                args.putSerializable("ARRAYLIST", placeDetailsList);
                                intent.putExtra("BUNDLE", args);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });


        // Surprise me button
        final Button surpriseMe = findViewById(R.id.surpriseMe);
        surpriseMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {

                            ArrayList<Place> places = new ArrayList<>();
                            ArrayList<PlaceDetail> placeDetailsList = new ArrayList<>();

                            if (isTimeToParty == false) {
                                Spinner aSpinner = findViewById(R.id.meansOfTransport);
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(0)) {
                                    places = ps.search(latitude, longitude, 500, "restaurant");
                                }
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(1)) {
                                    places = ps.search(latitude, longitude, 1000, "restaurant");
                                }
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(2)) {
                                    places = ps.search(latitude, longitude, 5000, "restaurant");
                                }
                            } else {
                                Spinner aSpinner = findViewById(R.id.meansOfTransport);
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(0)) {
                                    places = ps.search(latitude, longitude, 500, "night_club");
                                }
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(1)) {
                                    places = ps.search(latitude, longitude, 1000, "night_club");
                                }
                                if (aSpinner.getSelectedItem() == aSpinner.getItemAtPosition(2)) {
                                    places = ps.search(latitude, longitude, 5000, "night_club");
                                }
                            }
                            GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyDSbsYlKXUjSUCN1G6bfDM3npr1rcokkx0").build();

                            LocalTime startOpenTime = LocalTime.of(hr, min);
                            LocalTime endOpenTime = LocalTime.of(hrEnd, minEnd);

                            for (Place place : places
                                    ) {

                                PlaceDetails placeDetails = PlacesApi.placeDetails(context, place.place_id).await();
                                // when the place is always open

                                if (placeDetails != null && placeDetails.openingHours != null) {

                                    if (placeDetails.openingHours.periods.length == 1) {

                                        placeDetailsList.add(ps.getDetails(place.place_id));
                                        placeDetailsList.get(placeDetailsList.size() - 1).alwaysOpen = true;

                                        if (placeDetails.photos != null) {
                                            if (placeDetails.photos.length != 0) {
                                                ArrayList<String> photoReferences = new ArrayList<>();
                                                for (Photo ph : placeDetails.photos
                                                        ) {
                                                    photoReferences.add(ph.photoReference);
                                                }
                                                placeDetailsList.get(placeDetailsList.size() - 1).photoReference = photoReferences;
                                            }
                                        }

                                    } else if (placeDetails.openingHours.periods.length == 7) {


                                        LocalTime placeOpenTime = placeDetails.openingHours.periods[c.get(Calendar.DAY_OF_WEEK) - 1].open.time;
                                        LocalTime placeCloseTime = placeDetails.openingHours.periods[c.get(Calendar.DAY_OF_WEEK) - 1].close.time;


                                        // checks if open in the selected time by the user
                                        if (placeOpenTime.isBefore(startOpenTime) &&
                                                placeCloseTime.isAfter(endOpenTime) ||
                                                placeOpenTime.equals(startOpenTime) &&
                                                        placeCloseTime.equals(endOpenTime) ||
                                                placeOpenTime.equals(startOpenTime) &&
                                                        placeCloseTime.isAfter(endOpenTime) ||
                                                placeOpenTime.isBefore(startOpenTime) &&
                                                        placeCloseTime.equals(endOpenTime)
                                                ) {

                                            placeDetailsList.add(ps.getDetails(place.place_id));
                                            placeDetailsList.get(placeDetailsList.size() - 1).alwaysOpen = false;
                                            placeDetailsList.get(placeDetailsList.size() - 1).openTime = placeOpenTime;
                                            placeDetailsList.get(placeDetailsList.size() - 1).closeTime = placeCloseTime;

                                            if (placeDetails.photos != null) {
                                                if (placeDetails.photos.length != 0) {
                                                    ArrayList<String> photoReferences = new ArrayList<>();
                                                    for (Photo ph : placeDetails.photos
                                                            ) {
                                                        photoReferences.add(ph.photoReference);
                                                    }
                                                    placeDetailsList.get(placeDetailsList.size() - 1).photoReference = photoReferences;
                                                }
                                            }

                                        }
                                    } else {

                                        for (Period p : placeDetails.openingHours.periods
                                                ) {
                                            String tempDay = "";
                                            tempDay = p.open.day.getName();


                                            LocalTime placeOpenTime = p.open.time;
                                            LocalTime placeCloseTime = p.close.time;
                                            if (tempDay.equals(DaysOfTheWeek.values()[c.get(Calendar.DAY_OF_WEEK) - 1])) {

                                                if (placeOpenTime.isBefore(startOpenTime) &&
                                                        placeCloseTime.isAfter(endOpenTime) ||
                                                        placeOpenTime.equals(startOpenTime) &&
                                                                placeCloseTime.equals(endOpenTime) ||
                                                        placeOpenTime.equals(startOpenTime) &&
                                                                placeCloseTime.isAfter(endOpenTime) ||
                                                        placeOpenTime.isBefore(startOpenTime) &&
                                                                placeCloseTime.equals(endOpenTime)
                                                        ) {

                                                    placeDetailsList.add(ps.getDetails(place.place_id));
                                                    placeDetailsList.get(placeDetailsList.size() - 1).alwaysOpen = false;
                                                    placeDetailsList.get(placeDetailsList.size() - 1).openTime = placeOpenTime;
                                                    placeDetailsList.get(placeDetailsList.size() - 1).closeTime = placeCloseTime;

                                                    if (placeDetails.photos != null) {
                                                        if (placeDetails.photos.length != 0) {
                                                            ArrayList<String> photoReferences = new ArrayList<>();
                                                            for (Photo ph : placeDetails.photos
                                                                    ) {
                                                                photoReferences.add(ph.photoReference);
                                                            }
                                                            placeDetailsList.get(placeDetailsList.size() - 1).photoReference = photoReferences;
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }


                            if (placeDetailsList.size() > 1)
                                placeDetailsList.subList(0, placeDetailsList.size() - 1).clear();

                            if (placeDetailsList != null && placeDetailsList.size() != 0) {
                                Intent intent = new Intent(MainActivity.this, SuggestedPlace1.class);
                                Bundle args = new Bundle();
                                args.putSerializable("ARRAYLIST", placeDetailsList);
                                intent.putExtra("BUNDLE", args);

                                startActivity(intent);
                            } else {
                                Message message = mHandler.obtainMessage(0);
                                message.sendToTarget();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }

        });

        Button next = findViewById(R.id.button3);
        Button prev = findViewById(R.id.button2);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMainScreen();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMainScreen();
            }
        });
    }

    // Outside onCreate

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:

                boolean about_you = true;
                Intent aboutMe = new Intent(MainActivity.this, AboutYou.class);
                aboutMe.putExtra("aboutYou", about_you);
                startActivity(aboutMe);

                break;
            case R.id.nav_second_fragment:

                boolean pref = true;
                Intent prefer = new Intent(MainActivity.this, Personalize.class);
                prefer.putExtra("pref", pref);
                startActivity(prefer);
                break;
            case R.id.nav_forth_fragment:

                logout();

                break;
            default:
                break;
        }

        try {
            //fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);
        // Set action bar title
        //setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }


    // Time picker


    public void startTimeOnClick() {
        startTimeClick = findViewById(R.id.lbStartTime);
        startTimeClick.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                createdDialog(1111).show();
            }
        });
    }

    public void endTimeOnClick() {
        endTimeClick = findViewById(R.id.lbEndTime);
        endTimeClick.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                createdDialog(2222).show();
            }
        });
    }

    protected Dialog createdDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, timePickerListener, hr, min, false);
            case 2222:
                return new TimePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, timePickerListenerEnd, hrEnd, minEnd, false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
// TODO Auto-generated method stub
            hr = hourOfDay;
            min = minutes;
            updateTime(hr, min);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListenerEnd = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hrEnd = hourOfDay;
            minEnd = minutes;
            updateTimeEnd(hrEnd, minEnd);
        }
    };

    private void updateTime(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();

        view.setText(aTime);

    }

    private void updateTimeEnd(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();
        viewEnd.setText(aTime);

    }

    private String getTimeNow(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append("It's ").append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();
        return aTime;
    }

    public void logout() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }
        builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, StartupScreen.class));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

}