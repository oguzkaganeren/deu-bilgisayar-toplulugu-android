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
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

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
    private String saveLocation;
    private TimePicker timePicker;
    private FirebaseAuth mAuth;
    private LatLng latLng;
    private DatabaseReference mDatabase;
    private GoogleMap mMap;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth!=null){

            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbarInnerEvent);
            mActionBarToolbar.setTitleTextColor(getResources().getColor(R.color.colorTabSelected));
            mActionBarToolbar.setTitle("Add Event");
            setSupportActionBar(mActionBarToolbar);
            mDatabase= FirebaseDatabase.getInstance().getReference();
            title=(EditText)findViewById(R.id.title_event);
            description=(EditText)findViewById(R.id.description_event);
            address=(EditText)findViewById(R.id.address_event);
            date=(Button) findViewById(R.id.date_event);
            address.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if(address.getText()!=null){
                            if(address.getText().toString().trim().toLowerCase().length()>0){
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

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAuth.getCurrentUser() != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.done_addevent, menu);
            changeActionMenuItemsBackground(getResources().getColor(R.color.green));
        }
        return true;
    }
    public void changeActionMenuItemsBackground(int color) {
        for (int i = 0; i < mActionBarToolbar.getChildCount(); i++) {
            final View v = mActionBarToolbar.getChildAt(i);
            if (v instanceof ActionMenuView) {
                v.setBackgroundColor(color);
            }
        }
    }
    public void done(MenuItem item) {
        String key = mDatabase.child("events").push().getKey();
        String sTitle=title.getText().toString();
        String sDesc=description.getText().toString();
        String sDate=date.getText().toString();
        if(sTitle.length()>0&&sTitle.length()<100&&sDesc.length()>0
                &&sDesc.length()<800&&sDate.length()>0&&sDate.length()<20){
            if(address.getText().toString().trim().toLowerCase().length()>0){
                getLocationFromAddress(address.getText().toString().trim().toLowerCase());
            }
            if(saveLocation!=null&&!saveLocation.isEmpty()){
                Event newEvent=new Event();
                newEvent.title=sTitle.trim();
                newEvent.description=sDesc.trim();
                newEvent.date=sDate.trim();
                newEvent.location=saveLocation;
                newEvent.uid=mAuth.getCurrentUser().getUid();
                Toast.makeText(AddEventActivity.this, key,
                        Toast.LENGTH_SHORT).show();
                mDatabase.child("events").child(key).setValue(newEvent);
                mDatabase.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        finish();
                    }
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(AddEventActivity.this, "Adding Event failed."+firebaseError.getDetails().toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                mMap.clear();
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
                        address.setText("");
                        for (int i = 0; i < maddress.getMaxAddressLineIndex(); i++) {
                            sb.append(maddress.getAddressLine(i) + "\n");
                        }
                    }
                    if(addresses.get(0).getFeatureName()!=null){
                        address.setText(addresses.get(0).getFeatureName() + ", ");
                    }
                    if(addresses.get(0).getLocality()!=null){
                        address.setText(address.getText().toString() + addresses.get(0).getLocality() +", ");
                    }
                    if(addresses.get(0).getAdminArea()!=null){
                        address.setText(address.getText().toString()+ addresses.get(0).getAdminArea() + ", ");
                    }
                    if(addresses.get(0).getCountryName()!=null){
                        address.setText(address.getText().toString() + addresses.get(0).getCountryName());
                    }
                    //place marker where user just clicked
                    Marker marker = mMap.addMarker(new MarkerOptions().position(point).title("Event"));
                    saveLocation=String.valueOf(latLng.latitude)+"-"+String.valueOf(latLng.longitude);
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
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

            //Put marker on map on that LatLng
            mMap.clear();
            Marker srchMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Event"));
            saveLocation=String.valueOf(latLng.latitude)+"-"+String.valueOf(latLng.longitude);
            //Animate and Zoon on that map location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
class Event {

    public String title;
    public String description;
    public String date;
    public String location;
    public String uid;
    Object createdTimestamp;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Event() {
    }

    public Event(String title, String description,String date,String location,String uid) {
        this.title=title;
        this.description=description;
        this.date=date;
        this.location=location;
        this.uid=uid;
        createdTimestamp = ServerValue.TIMESTAMP;
    }
    @Exclude
    public long getCreatedTimestampLong(){
        return (long)createdTimestamp;
    }
}