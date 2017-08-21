package com.example.oguz.topluluk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MaterialViewPager mViewPager;
    private NavigationView navigationView;
    private NavigationView navigationViewRight;
    private DrawerLayout drawer;
    private View navHeader;
    private FirebaseStorage myStorage;
    private StorageReference storageRef;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private ArrayList<MembersInfo> membersList;
    private ImageView profile_image_right;
    private MembersAdapter ourMembersAdapter;
    private RecyclerView rcMembers;
    private DatabaseReference mDatabase;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Fragment fragment = null;
    private Class fragmentClass = null;
    //Seçili olan menu indexi
    public static int navItemIndex = 0;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_EVENT="event";
    private static final String TAG_SIGNOUT="signout";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;
    private static Context mContext;

    // kullanıcı geri tuşuna basınca bir önceki fragmente geçme meselesi
    private boolean shouldLoadHomeFragOnBackPress = true;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static Context getContext() {
        return mContext;
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mViewPager=(MaterialViewPager)findViewById(R.id.materialViewPager);
        toolbar = mViewPager.getToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_recent_actors_white_24dp);//bu kısımı bir kontrol et

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }

        mContext= getApplicationContext();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationViewRight=(NavigationView) findViewById(R.id.nav_viewTwo);
        // Navigation view header
        //menu içerisindeki değerleri verme kısmı
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        profile_image_right=(ImageView)findViewById(R.id.right_profile_picture);

// load nav menu header data
        setUpNavigationView();
//if the user did not login, login page is showed
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                MainActivity.this.startActivity(intent);
                 }
            }
        });
        if (mAuth.getCurrentUser() != null) {

            // User is logged in
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("last-online-date").setValue(ServerValue.TIMESTAMP);
            navigationView.getMenu().getItem(1).setTitle("Account");
            navigationView.getMenu().getItem(1).setIcon(R.drawable.account_24dp);
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("online").setValue(true);

            setDrawerState(true);
        }else {
            setDrawerState(false);
            navigationView.getMenu().getItem(1).setTitle("Login");
        }
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
        loadNavHeader();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = mViewPager.getViewPager();
        final ContentFragment myContents=new ContentFragment();
        final EventFragment myEvents=new EventFragment();
        final MeetingFragment myMeeting=new MeetingFragment();
        final NoticeFragment myNotices=new NoticeFragment();
        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position % 4) {
                    case 0:
                        return myContents;
                    case 1:
                        if(mAuth.getCurrentUser() != null){
                            return myNotices;
                        }else{
                            return null;
                        }
                    case 2:
                        if(mAuth.getCurrentUser() != null){
                            return myEvents;
                        }else {
                            return null;
                        }
                    case 3:
                        if(mAuth.getCurrentUser() != null){
                            return myMeeting;
                        }else{
                            return null;
                        }

                    default:
                            return myContents;

                }
            }

            @Override
            public int getCount() {
                if(mAuth.getCurrentUser() != null){
                    return 4;
                }else{
                    return 1;
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 4) {
                    case 0:
                            return "Contents";
                    case 1:
                        if(mAuth.getCurrentUser() != null) {
                        return "Notices";
                        }else{
                            return null;
                        }
                    case 2:
                        if(mAuth.getCurrentUser() != null) {
                            return "Events";
                        }else{
                            return null;
                        }
                    case 3:
                        if(mAuth.getCurrentUser() != null) {
                        return "Meeting";
                        }else{
                            return null;
                        }
                }
                return "";
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorAndDrawable(
                                getResources().getColor(R.color.content_bg), getResources().getDrawable(R.drawable.content_back));
                    case 1:
                        return HeaderDesign.fromColorAndDrawable(
                               getResources().getColor( R.color.notice_bg),getResources().getDrawable(R.drawable.notice_back));
                    case 2:
                        return HeaderDesign.fromColorAndDrawable(
                               getResources().getColor(R.color.event_bg),getResources().getDrawable(R.drawable.event_back));
                    case 3:
                        return HeaderDesign.fromColorAndDrawable(
                               getResources().getColor(R.color.lime), getResources().getDrawable(R.drawable.meeting_back));
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());


    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser() != null) {
            // User is logged in
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("online").setValue(false);
        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.left_exit);
    }
    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            actionBarDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_SETTLING);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.syncState();

        }
        else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            actionBarDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_SETTLING);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarDrawerToggle.syncState();
        }
    }
    //menu ile ilgili şeyler
    private void loadNavHeader() {
        // name, website
        //kullanıcı giriş yaptıysa görünecek

        txtName.setText("Name Surname");
        txtWebsite.setText("Website");


        // kullanıcı arkaplan resmi
        Glide.with(this).load(R.mipmap.menu_header_back)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // menude nokta gözükme meselesi
        //navigationView.getMenu().getItem(4).setActionView(R.layout.menu_dot);
    }

    //seçilen menuye göre fragment döner
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
        if (navItemIndex==1){
            if (mAuth.getCurrentUser() != null) {
                Intent intent = new Intent(this, ProfileActivity.class);
                MainActivity.this.startActivity(intent);
            }else{
                Intent intent = new Intent(this, LoginActivity.class);
                MainActivity.this.startActivity(intent);
            }
        }
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_start:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_account:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ACCOUNT;
                        break;
                    case R.id.nav_event:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_EVENT;
                        break;

                    case R.id.nav_notifications:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_signout:
                        navItemIndex = 5;
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("online").setValue(false);
                        mAuth.signOut();
                        setDrawerState(false);
                        Toast.makeText(MainActivity.this, "Signout successful", Toast.LENGTH_SHORT).show();
                        CURRENT_TAG = TAG_SIGNOUT;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, NoticeFragment.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, MeetingFragment.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                if (mAuth.getCurrentUser() != null) {
                    // User is logged in
                    navigationView.getMenu().getItem(1).setTitle("Account");
                        loadDataOnFirebase();
                }else {
                    navigationView.getMenu().getItem(1).setTitle("Login");
                }
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAuth.getCurrentUser() != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.right, menu);
        }
        return true;
    }
    public void rightMenu(MenuItem item) {
        //members kısmı
        if(fragmentClass==null&&fragment==null){
            fragmentClass = MembersFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.members_frame, fragment).commit();
        }



        //-----------------------------------
        drawer.openDrawer(navigationViewRight);
    }

    public void loadDataOnFirebase() {

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("name-surname")) {
                        txtName.setText(snapshot.child("name-surname").getValue().toString());
                    }
                    if (snapshot.hasChild("website")) {
                        txtWebsite.setText(snapshot.child("website").getValue().toString());

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(mAuth==null){
                    Toast.makeText(MainActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        myStorage= FirebaseStorage.getInstance();
        storageRef= myStorage.getReference();
        StorageReference image = storageRef.child("images/profiles/"+mAuth.getCurrentUser().getUid());

        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide
                        .with(MainActivity.this)
                        .load(uri)
                        .centerCrop()
                        .into(imgProfile);
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Glide
                        .with(MainActivity.this)
                        .load(R.mipmap.logo)
                        .centerCrop()
                        .into(imgProfile);
            }
        });
    }
}
