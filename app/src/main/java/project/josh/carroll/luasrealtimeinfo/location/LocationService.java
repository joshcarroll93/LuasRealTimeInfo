package project.josh.carroll.luasrealtimeinfo.location;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import java.util.ArrayList;

import project.josh.carroll.luasrealtimeinfo.Adapter.LuasStopsAdapter;
import project.josh.carroll.luasrealtimeinfo.Data.LuasStop;

public class LocationService extends Service {

    private LocationManager locationManager;
    private String TAG = getClass().getSimpleName();
    private static LuasStop nearestStop;
    private static LocationListener locationListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "IN SERVICE");

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
               locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
               locationListener = new LocationListener();

                try{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            5000, 50, locationListener);

                }catch(SecurityException se){
                    Log.d(TAG, se.toString());
                    Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static LuasStop getNearestStop(){
        return nearestStop;
    }

    public static void setNearestStop(Location location){

        float dist = Float.MAX_VALUE;
        int outer = 0;
        int inner = 0;

        ArrayList<ArrayList<LuasStop>> luasLines = LuasStopsAdapter.getLuasLines();
        float[] distanceResults = new float[1];

        for (int i = 0; i < luasLines.size(); i++){

            for (int j = 0; j < luasLines.get(i).size(); j++){

                double lat = Double.parseDouble(luasLines.get(i).get(j).getLatitude());
                double lng = Double.parseDouble(luasLines.get(i).get(j).getLongitude());

                Location.distanceBetween(lat, lng,
                        location.getLatitude(), location.getLongitude(), distanceResults);

                Log.d("DISTANCE RESULTS" , "" + distanceResults[0]);

                if(distanceResults[0] < dist) {
                    dist = distanceResults[0];
                    outer = i;
                    inner = j;
                }
            }
            nearestStop = luasLines.get(outer).get(inner);
        }
    }
    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            setNearestStop(location);
//            float dist = Float.MAX_VALUE;
//            int outer = 0;
//            int inner = 0;
//
//            ArrayList<ArrayList<LuasStop>> luasLines = LuasStopsAdapter.getLuasLines();
//            float[] distanceResults = new float[1];
//
//            Log.d(TAG, "Latitude: " +location.getLatitude());
//            Log.d(TAG, "Longitude: " +location.getLongitude());
//
//            for (int i = 0; i < luasLines.size(); i++){
//
//                for (int j = 0; j < luasLines.get(i).size(); j++){
//
//                    double lat = Double.parseDouble(luasLines.get(i).get(j).getLatitude());
//                    double lng = Double.parseDouble(luasLines.get(i).get(j).getLongitude());
//
//                    Log.d(TAG, "Lat: "+ lat);
//                    Log.d(TAG, "Lng: "+ lng);
//
//                    Location.distanceBetween(lat, lng,
//                    location.getLatitude(), location.getLongitude(), distanceResults);
//
//                    Log.d("DISTANCE RESULTS" , "" + distanceResults[0]);
//
//                   if(distanceResults[0] < dist) {
//                       dist = distanceResults[0];
//                       outer = i;
//                       inner = j;
//                   }
//                }
//                nearestStop = luasLines.get(outer).get(inner);
//            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, provider);
        }
    }
}
