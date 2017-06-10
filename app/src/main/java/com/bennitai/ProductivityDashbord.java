package com.bennitai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bennitai.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ProductivityDashbord extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productivity_dashbord);
        ImageButton goBack = (ImageButton)findViewById(R.id.dasbord_goback);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gps = new GPSTracker(ProductivityDashbord.this);
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            mMap = googleMap;
            LatLng current_location = new LatLng(latitude, longitude );
            LatLng lcn1 = new LatLng(18.4, 73.8);
            LatLng lcn2 = new LatLng(18.3, 73.81);
            LatLng lcn3 = new LatLng(18.42, 73.69);
            LatLng lcn4 = new LatLng(18.38, 73.75);
            LatLng lcn5 = new LatLng(18.39, 73.7);
            LatLng lcn6 = new LatLng(18.39, 73.5);
            LatLng lcn7 = new LatLng(18.33, 73.9);

            mMap.addMarker(new MarkerOptions().position(current_location).title("Time spent here 78 hours"));
            mMap.addMarker(new MarkerOptions().position(lcn1).title("Time spent here 0.34 hours"));
            mMap.addMarker(new MarkerOptions().position(lcn1).title("Time spent here 2 hours"));
            mMap.addMarker(new MarkerOptions().position(lcn2).title("Time spent here 1.24 hours"));
            mMap.addMarker(new MarkerOptions().position(lcn3).title("Time spent here 0.431 hours"));
            mMap.addMarker(new MarkerOptions().position(lcn4).title("Time spent here 0.387 hours"));
            mMap.addMarker(new MarkerOptions().position(lcn5).title("Time spent here 0.221 hurs"));
            mMap.addMarker(new MarkerOptions().position(lcn6).title("Time spent here 1.56 hours"));
            mMap.addMarker(new MarkerOptions().position(lcn7).title("Time spent here 0.31 hours"));


            mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));


            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }


    }
}
