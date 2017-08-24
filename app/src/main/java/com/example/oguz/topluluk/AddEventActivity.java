package com.example.oguz.topluluk;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Created by Oguz on 23-Aug-17.
 */

public class AddEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText title;
    private EditText description;
    private EditText address;
    private GoogleMap mMap;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);
        title=(EditText)findViewById(R.id.title_event);
        address=(EditText)findViewById(R.id.address_event);

        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                   if(address.getText()!=null){
                    if(address.getText().toString().trim().toLowerCase().length()>0){
                        Log.d("a", "onFocusChange: "+address.getText().toString().trim().toLowerCase());
                        getLocationFromAddress(address.getText().toString().trim().toLowerCase());
                    }

                }
                }
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
        fm.beginTransaction().replace(R.id.map_frame, supportMapFragment).commit();
        supportMapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point));
            }
        });

        // Add a marker in Sydney, Australia, and move the camera.
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
    public void getLocationFromAddress(String strAddress)
    {
        //Create coder with Activity context - this
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress,5);

            //check for null
            if (address == null) {
                return;
            }

            //Lets take first possibility from the all possibilities.
            Address location=address.get(0);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //Put marker on map on that LatLng
            Marker srchMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.event_24dp)));

            //Animate and Zoon on that map location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
