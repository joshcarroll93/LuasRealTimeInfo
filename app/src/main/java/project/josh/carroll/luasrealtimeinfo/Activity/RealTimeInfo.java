package project.josh.carroll.luasrealtimeinfo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;

import project.josh.carroll.luasrealtimeinfo.Adapter.InboundAdapter;
import project.josh.carroll.luasrealtimeinfo.Adapter.OutboundAdapter;
import project.josh.carroll.luasrealtimeinfo.AsyncTask.FetchRealTimeInfo;
import project.josh.carroll.luasrealtimeinfo.Data.Tram;
import project.josh.carroll.luasrealtimeinfo.R;

public class RealTimeInfo extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnMapReadyCallback {
    public InboundAdapter inboundAdapter;
    public OutboundAdapter outboundAdapter;
    private String stopAbr;
    private SwipeRefreshLayout swipeRefreshLayout;
//    private ProgressBar progressBar;
    private RecyclerView inboundRecyclerView;
    private RecyclerView outboundRecyclerView;
    private String latitude;
    private String longitude;
    private String stopName;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_info);

        Intent intent = getIntent();
        stopName = intent.getStringExtra("stop name");
        stopAbr = intent.getStringExtra("stop");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        TextView textView = findViewById(R.id.tvTitle);
        textView.setText(stopName);

        inboundRecyclerView = findViewById(R.id.recycler_view_real_time_info);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplication());
        inboundRecyclerView.setLayoutManager(layoutManager);

        outboundRecyclerView = findViewById(R.id.recycler_view_real_time_inf);
        RecyclerView.LayoutManager greenLineLayoutManager = new LinearLayoutManager(getApplicationContext());
        outboundRecyclerView.setLayoutManager(greenLineLayoutManager);

        inboundAdapter = new InboundAdapter(new ArrayList<Tram>());
        inboundRecyclerView.setAdapter(inboundAdapter);

        outboundAdapter = new OutboundAdapter(new ArrayList<Tram>());
        outboundRecyclerView.setAdapter(outboundAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);


        onRefresh();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_frag);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        googleMap.addMarker(new MarkerOptions().position(location)
                .title(stopName));

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    @Override
    public void onRefresh() {swipeRefreshLayout.setRefreshing(false);
        new FetchRealTimeInfo(this, stopAbr).execute();
    }

    public void preLoad(){
//        progressBar.setVisibility(View.VISIBLE);
        inboundRecyclerView.setVisibility(View.INVISIBLE);
        outboundRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void loadCompleted(){
//        progressBar.setVisibility(View.GONE);
        inboundRecyclerView.setVisibility(View.VISIBLE);
        outboundRecyclerView.setVisibility(View.VISIBLE);
    }
}
