package com.example.oguz.bilgisayarToplulugu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private DatabaseReference mDatabase;
    private GoogleMap mMap;
    private String sAddress;
    Bundle extras;
    private ProgressBar spinner;
    private MenuItem doneMenu;

    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);
        mAuth = FirebaseAuth.getInstance();
        //kullanıcı giriş yapmadıysa bu sayfayı görememeli
        if (mAuth.getCurrentUser() != null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbarInnerEvent);
            mActionBarToolbar.setTitleTextColor(getResources().getColor(R.color.colorTabSelected));
            mActionBarToolbar.setTitle("Add Event");
            setSupportActionBar(mActionBarToolbar);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            title = (EditText) findViewById(R.id.title_event);
            spinner = (ProgressBar)findViewById(R.id.addevent_progressbar);
            spinner.setVisibility(View.GONE);
            description = (EditText) findViewById(R.id.description_event);
            date = (Button) findViewById(R.id.date_event);
            //--------------------Google maps auto complete'ın eklenmesi
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
            address=((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input));
            //-----------------------------------------
            extras = getIntent().getExtras();

            if (extras!=null){
                if(extras.get("title")!=null){
                    title.setText(extras.get("title").toString());
                }
                if(extras.get("description")!=null){
                    description.setText(extras.get("description").toString());
                }
                if(extras.get("location")!=null){
                    saveLocation=extras.get("location").toString();
                }
                if(extras.get("address")!=null){
                    address.setText(extras.get("address").toString());
                    sAddress=extras.get("address").toString();
                }
                if(extras.get("date")!=null){
                    date.setText(extras.get("date").toString());
                }
            }
            //--------------Google maps'in ilave edilme kısmı
            FragmentManager fm = getSupportFragmentManager();
            SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_frame, supportMapFragment).commit();
            supportMapFragment.getMapAsync(this);
            //-----------------------------------------
            //--------------Tarih ve saat dialog kısmı
            final Dialog dialog = new Dialog(AddEventActivity.this);
            dialog.setContentView(R.layout.custom_date_time);
            dialog.setTitle("Custom Dialog");
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    int hour = timePicker.getCurrentHour();
                    int minute = timePicker.getCurrentMinute();
                    date.setText(day + "/" + month + "/" + year + " " + hour + ":" + minute);
                }
            });
            //-----------------------------------------

            //------------------Addrese göre kameranın oynaması ve marker koyulması
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    mMap.clear();
                    Marker srchMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Event"));
                    saveLocation=String.valueOf(place.getLatLng().latitude)+"-"+String.valueOf(place.getLatLng().longitude);
                    //Animate and Zoon on that map location
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    if(place.getAddress()!=null){
                        sAddress=place.getAddress().toString();
                        Log.d("yazdir", "onPlaceSelected: "+sAddress);
                    }


                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i("all", "An error occurred: " + status);
                }
            });
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        //sağ üste yer alan tamam butonun oluşturulma kısmı
        if (mAuth.getCurrentUser() != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.done_addevent, menu);
            doneMenu=menu.getItem(0);
            changeActionMenuItemsBackground(getResources().getColor(R.color.green));
        }
        return true;
    }

    public void changeActionMenuItemsBackground(int color) {
        //Action bar üzerindeki butonun rengini değiştirmek için bu method kullanıldı
        for (int i = 0; i < mActionBarToolbar.getChildCount(); i++) {
            final View v = mActionBarToolbar.getChildAt(i);
            if (v instanceof ActionMenuView) {
                v.setBackgroundColor(color);
            }
        }
    }

    public void done(MenuItem item) {
        //tekrar done butona basması engelleniyor
        spinner.setVisibility(View.VISIBLE);
        doneMenu.setVisible(false);
        //-----------------------------------------
        String key;
        if(extras!=null){
            key = extras.get("eventkey").toString();
        }else{
            key = mDatabase.child("events").push().getKey();
        }

        String sTitle = title.getText().toString();
        String sDesc = description.getText().toString();
        String sDate = date.getText().toString();
        if(sTitle!=null&&sDesc!=null&&sDate!=null&&sAddress!=null) {

            if (sTitle.length() > 0 && sTitle.length() < 100 && sDesc.length() > 0
                    && sDesc.length() < 800 && sDate.length() > 0 && sDate.length() < 35 && sAddress.length() > 3) {
                if (saveLocation != null && !saveLocation.isEmpty()) {
                    Event newEvent = new Event();
                    newEvent.title = sTitle.trim();
                    newEvent.description = sDesc.trim();
                    newEvent.createdTimestamp = ServerValue.TIMESTAMP;
                    Log.d("aaaa", "done: "+sDate);
                    try {
                        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
                        Date dt = format.parse(sDate);
                        newEvent.date = dt;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    newEvent.address = sAddress;
                    newEvent.location = saveLocation;
                    if(extras!=null){
                        newEvent.uid=extras.get("uid").toString();
                    }else{
                        newEvent.uid = mAuth.getCurrentUser().getUid();
                    }
                    mDatabase.child("events").child(key).setValue(newEvent);
                    mDatabase.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Toast.makeText(AddEventActivity.this, "Adding Event succeed.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        public void onCancelled(DatabaseError firebaseError) {
                            Toast.makeText(AddEventActivity.this, "Adding Event failed." + firebaseError.getDetails().toString(),
                                    Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                            doneMenu.setEnabled(true);
                        }
                    });
                } else {
                    spinner.setVisibility(View.GONE);
                    doneMenu.setVisible(true);
                    address.setError("Please select an address");
                }

            } else {
                spinner.setVisibility(View.GONE);
                doneMenu.setVisible(true);
                Toast.makeText(AddEventActivity.this, "Please do not leave empty area and be careful character limit", Toast.LENGTH_SHORT).show();
            }
        }else{
            spinner.setVisibility(View.GONE);
            doneMenu.setVisible(true);
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //map üzerine tıklama işleminde address labeline addresin yazılması ve location'ın alınması ile ilgili kısım
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
                    saveLocation=String.valueOf(point.latitude)+"-"+String.valueOf(point.longitude);
                    if(address.getText()!=null&&address.getText().toString().length()>3){
                        sAddress=address.getText().toString();
                        Log.d("yazdir", "onClick: "+sAddress);
                    }
                }
            }
        });
    }

 }
     class Event {

        public String title;
        public String description;
        public Date date;
        public String location;
        public String uid;
        public String address;
        public Object createdTimestamp;

        // Default constructor required for calls to
        // DataSnapshot.getValue(User.class)
        public Event() {
        }

        public Event(String title, String description, Date date, String location, String uid,String address) {
            this.title = title;
            this.description = description;
            this.date = date;
            this.location = location;
            this.uid = uid;
            this.address=address;
            this.createdTimestamp = ServerValue.TIMESTAMP;
        }

        @Exclude
        public long getCreatedTimestampLong() {
            return (long) createdTimestamp;
        }
    }
