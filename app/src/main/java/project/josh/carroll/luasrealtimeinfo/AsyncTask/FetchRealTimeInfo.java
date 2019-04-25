package project.josh.carroll.luasrealtimeinfo.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import project.josh.carroll.luasrealtimeinfo.Activity.RealTimeInfo;
import project.josh.carroll.luasrealtimeinfo.Data.Tram;
import project.josh.carroll.luasrealtimeinfo.Util.Utils;

public class FetchRealTimeInfo extends AsyncTask<URL, Void, List<Tram>> {

    private String TAG = getClass().getSimpleName();
    private WeakReference<RealTimeInfo> realTimeInfoWeakReference;
    private String stop;

    public FetchRealTimeInfo(RealTimeInfo realTimeInfo, String stop){
        realTimeInfoWeakReference = new WeakReference<>(realTimeInfo);
        this.stop = stop;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        RealTimeInfo realTimeInfo = realTimeInfoWeakReference.get();
        realTimeInfo.preLoad();
    }

    @Override
    protected List<Tram> doInBackground(URL... urls) {

        Log.d(TAG, "in doInBackground");

        List<Tram> trams = new ArrayList<>();

        try {
            URL url = new URL("http://luasforecasts.rpa.ie/xml/get.ashx?action=forecast&stop=" + stop +"&encrypt=false");
            trams = new Utils().getResponseForRealTimeInfo(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return trams;
    }

    @Override
    protected void onPostExecute(List<Tram> trams) {

        for (int i = 0; i < trams.size(); i++){

            //quick fix as first outbound tram is bug
            if (trams.get(i).getDirection().equals("Outbound")) {
                trams.remove(i);
                break;
            }
        }

        ArrayList<Tram> inboundTrams = new ArrayList<>();
        ArrayList<Tram> outboundTrams = new ArrayList<>();
        for (int i = 0; i < trams.size(); i++){

            if(trams.get(i).getDirection().equals("Inbound"))
                inboundTrams.add(trams.get(i));

            else
                outboundTrams.add(trams.get(i));


        }

        RealTimeInfo realTimeInfo = realTimeInfoWeakReference.get();
        realTimeInfo.inboundAdapter.updateTrams(inboundTrams);
        realTimeInfo.outboundAdapter.updateTrams(outboundTrams);
        realTimeInfo.loadCompleted();

        Log.d(TAG, "in postExecute");
        super.onPostExecute(trams);
    }
}
