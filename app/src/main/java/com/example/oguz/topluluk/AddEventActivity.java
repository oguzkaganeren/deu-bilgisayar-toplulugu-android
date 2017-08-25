package com.example.oguz.topluluk;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
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
    private Button date;
    private Toolbar mActionBarToolbar;
     private DatePicker datePicker;
    private TimePicker timePicker;
    private GoogleMap mMap;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);
        title=(EditText)findViewById(R.id.title_event);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbarInnerEvent);
        mActionBarToolbar.setTitleTextColor(getResources().getColor(R.color.colorTabSelected));
        mActionBarToolbar.setTitle("Add Event");
        address=(EditText)findViewById(R.id.address_event);
        date=(Button) findViewById(R.id.date_event);
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

        final Dialog dialog = new Dialog(AddEventActivity.this);

        dialog.setContentView(R.layout.custom_date_time);
        dialog.setTitle("Custom Dialog");
        date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.show();
            }
        });
        datePicker = (DatePicker) dialog.findViewById(R.id.datePickerEvent);
        timePicker = (TimePicker) dialog.findViewById(R.id.timePickerEvent);
        timePicker.setIs24HourView(true);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                int hour=timePicker.getCurrentHour();
                int minute=timePicker.getCurrentMinute();
                date.setText(day+"/"+month+"/"+year+" "+hour+":"+minute);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub

                Geocoder geocoder = new Geocoder(AddEventActivity.this);
                List<Address> addresses = new ArrayList<Address>();
                try {
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() != 0) {
                    android.location.Address maddress = addresses.get(0);

                    if (maddress != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < maddress.getMaxAddressLineIndex(); i++) {
                            sb.append(maddress.getAddressLine(i) + "\n");
                        }
                        address.setText(sb.toString());
                    }

                    //place marker where user just clicked
                    Marker marker = mMap.addMarker(new MarkerOptions().position(point).title("Marker")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                }
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
        List<Address> maddress;

        try {
            //Get latLng from String
            maddress = coder.getFromLocationName(strAddress,4);

            //check for null
            if (maddress == null) {
                return;
            }

            //Lets take first possibility from the all possibilities.
            Address location=maddress.get(0);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //Put marker on map on that LatLng
            Marker srchMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));

            //Animate and Zoon on that map location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
