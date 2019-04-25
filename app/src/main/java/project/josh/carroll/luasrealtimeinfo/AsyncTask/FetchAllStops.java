package project.josh.carroll.luasrealtimeinfo.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;



import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import project.josh.carroll.luasrealtimeinfo.Activity.MainActivity;
import project.josh.carroll.luasrealtimeinfo.Data.LuasStop;
import project.josh.carroll.luasrealtimeinfo.Util.Utils;


public class FetchAllStops extends AsyncTask<URL, Void, ArrayList<ArrayList<LuasStop>>> {

    private String TAG = getClass().getSimpleName();
    private WeakReference<MainActivity> mainActivityWeakReference;

    public FetchAllStops(MainActivity mainActivity){
      mainActivityWeakReference = new WeakReference<>(mainActivity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity mainActivity = mainActivityWeakReference.get();
        mainActivity.preLoad();
    }

    @Override
    protected ArrayList<ArrayList<LuasStop>> doInBackground(URL... urls) {

        Log.d(TAG, "in doInBackground");

        ArrayList<ArrayList<LuasStop>> luasStops = new ArrayList<>();
        Utils utils = new Utils();
        try {
            URL url = new URL("http://luasforecasts.rpa.ie/xml/get.ashx?action=stops&encrypt=false");
            luasStops = utils.getResponseForLuasStops(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return luasStops;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<LuasStop>> stops) {

        if(stops != null){
            MainActivity mainActivity = mainActivityWeakReference.get();
            mainActivity.adapter.setLists(stops);

            mainActivity.setButtonClicks();
            mainActivity.displayRedLine();
            mainActivity.loadCompleted();
        }
        super.onPostExecute(stops);
    }
}
