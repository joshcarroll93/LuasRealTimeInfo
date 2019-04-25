package project.josh.carroll.luasrealtimeinfo.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import project.josh.carroll.luasrealtimeinfo.Adapter.LuasStopsAdapter;
import project.josh.carroll.luasrealtimeinfo.AsyncTask.FetchAllStops;
import project.josh.carroll.luasrealtimeinfo.Data.LuasStop;
import project.josh.carroll.luasrealtimeinfo.R;
import project.josh.carroll.luasrealtimeinfo.location.LocationService;

public class MainActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();
    private Button redLine;
    private Button greenLine;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerViewAllStops;
    private ProgressBar progressBar;
    public LuasStopsAdapter adapter;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    public Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.action_bar_home);
        }

        permissionGranted();

        progressBar = findViewById(R.id.luas_stops_progress_bar);

        recyclerViewAllStops = findViewById(R.id.recycler_view_all_stops);
        layoutManager = new LinearLayoutManager(getApplication());
        recyclerViewAllStops.setLayoutManager(layoutManager);

        adapter = new LuasStopsAdapter(getApplicationContext(), new ArrayList<LuasStop>());
        recyclerViewAllStops.setAdapter(adapter);

        redLine = findViewById(R.id.button_red_line);
        greenLine = findViewById(R.id.button_green_line);
        redLine.setBackgroundColor(Color.parseColor("#D3D3D3"));
        greenLine.setBackgroundColor(Color.parseColor("#D3D3D3"));

        new FetchAllStops(this).execute();

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        mLocation = location;
                    }
                }
            });
        }
        catch (SecurityException se){
            se.printStackTrace();
        }

        ImageButton imageButton = findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (isMyServiceRunning(LocationService.class))
                getNearestStop();

            else
                Snackbar.make(getCurrentFocus().getRootView(), "Permission not granted.", Snackbar.LENGTH_SHORT).show();

            }
        });

        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(getCurrentFocus().getRootView(),"Find nearest Luas stop." ,Snackbar.LENGTH_LONG).show();
                return true;
            }
        });
    }

    public void setButtonClicks(){

        redLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setLuasLine(0);

                redLine.setBackgroundColor(Color.parseColor("#FF0000"));
                greenLine.setBackgroundColor(Color.parseColor("#D3D3D3"));
                redLine.setTextColor(Color.parseColor("#ffffff"));
                greenLine.setTextColor(Color.parseColor("#000000"));
                layoutManager.scrollToPosition(0);
            }
        });

        greenLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setLuasLine(1);
                redLine.setBackgroundColor(Color.parseColor("#D3D3D3"));
                greenLine.setBackgroundColor(Color.parseColor("#008000"));
                redLine.setTextColor(Color.parseColor("#000000"));
                greenLine.setTextColor(Color.parseColor("#ffffff"));
                layoutManager.scrollToPosition(0);
            }
        });
    }

    public void displayRedLine(){
        redLine.callOnClick();
    }


    public void preLoad(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewAllStops.setVisibility(View.INVISIBLE);
    }

    public void loadCompleted(){
        progressBar.setVisibility(View.GONE);
        recyclerViewAllStops.setVisibility(View.VISIBLE);
    }

    private void getNearestStop(){

        if(LocationService.getNearestStop() == null && mLocation != null)
            LocationService.setNearestStop(mLocation);
        else
            Snackbar.make(getCurrentFocus().getRootView(), "Cant find location, try again soon", Snackbar.LENGTH_LONG);

        if(LocationService.getNearestStop() != null){
            LuasStop luasStop = LocationService.getNearestStop();

            Intent intent = new Intent(this, RealTimeInfo.class);
            intent.putExtra("stop", luasStop.getAbbreviation());
            intent.putExtra("stop name", luasStop.getPronunciation());
            intent.putExtra("latitude", luasStop.getLatitude());
            intent.putExtra("longitude", luasStop.getLongitude());
            startActivity(intent);
        }
        else{
            Snackbar.make(getCurrentFocus().getRootView(), "Cant find closest stop, try again soon.", Snackbar.LENGTH_LONG).show();
        }
    }

    public void permissionGranted(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
//                Log.d(TAG, "should show rationale");
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                Log.d(TAG, "request perm");
                // MY_PERMISSIONS_REQUEST_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            if (!isMyServiceRunning(LocationService.class))
                startService(new Intent(getApplicationContext(), LocationService.class));

//            getNearestStop();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (!isMyServiceRunning(LocationService.class))
                        startService(new Intent(getApplicationContext(), LocationService.class));

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
