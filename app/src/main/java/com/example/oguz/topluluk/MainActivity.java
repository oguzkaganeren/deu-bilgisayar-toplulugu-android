package com.example.oguz.topluluk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private FirebaseStorage myStorage;
    private StorageReference storageRef;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    //Seçili olan menu indexi
    public static int navItemIndex = 0;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_ACCOUNT = "account";
    private static final String TAG_EVENT="event";
    private static final String TAG_PERSON="person";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;
    private static Context mContext;

    // Seçili olan menunun başlığı
    private String[] activityTitles;
    // kullanıcı geri tuşuna basınca bir önceki fragmente geçme meselesi
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_user);//bu kısımı bir kontrol et

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        mContext=getApplicationContext();
        setSupportActionBar(toolbar);
        //----------------------------
        mHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {

            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        };
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        // Navigation view header
        //menu içerisindeki değerleri verme kısmı
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);


        // load toolbar titles from string resources
        //string içerindeki başlıklardan değerleri çeker
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        // load nav menu header data
        loadNavHeader();
        setUpNavigationView();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
       setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FF9800"));
        tabLayout.setTabTextColors(Color.parseColor("#E2E2E2"), Color.parseColor("#ffffff"));
        if (mAuth.getCurrentUser() != null) {
            // User is logged in
            navigationView.getMenu().getItem(1).setTitle("Profil");
        }else {
            navigationView.getMenu().getItem(1).setTitle("Üye Girişi");
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(android.R.color.holo_orange_light));

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Fragment f =  adapter.getItem(tab.getPosition());
                if (f != null) {
                    View fragmentView = f.getView();
                    RecyclerView mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.rcview);//mine one is RecyclerView
                    if (mRecyclerView != null)
                        mRecyclerView.smoothScrollToPosition(0);
                }
            }
        });

        tabLayout.post(new Runnable() {
            @Override
            public void run() {

                tabLayout.setupWithViewPager(viewPager);

            }
        });
    }
    //pageleri ekler (tablar)
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContentFragment(), getString(R.string.tabOne));
        //kullanıcı giriş yaptıysa alt kısım gözükecek
        adapter.addFragment(new NoticeFragment(), getString(R.string.tabTwo));
        adapter.addFragment(new EventFragment(), getString(R.string.tabThree));
        adapter.addFragment(new MeetingFragment(), getString(R.string.tabFour));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    //menu ile ilgili şeyler
    private void loadNavHeader() {
        // name, website
        //kullanıcı giriş yaptıysa görünecek

        txtName.setText("Name Surname");
        txtWebsite.setText("Website");
        loadDataOnFirebase();
        // kullanıcı arkaplan resmi
        Glide.with(this).load(R.mipmap.bg_menuheadlast)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        //kullanıcı giriş yaptıysa kullanıcı fotosu burada gözükecek


        // menude nokta gözükme meselesi
        navigationView.getMenu().getItem(4).setActionView(R.layout.menu_dot);
    }

    //seçilen menuye göre fragment döner
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        // set toolbar title
        setToolbarTitle();
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
                Fragment fragment = getHomeFragment();
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

    private Fragment getHomeFragment() {
        switch (navItemIndex) {

            case 0:
                // home
                MainActivity homeFragment = new MainActivity();
                return null;

            case 1:


                // login
                return null;
            case 2:
                // movies fragment
                return null;
            case 3:
                // notifications fragment
                return null;

            case 4:
                // settings fragment
                return null;
            default:
                return null;
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
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
                    case R.id.nav_login:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_LOGIN;
                        break;
                    case R.id.nav_account:
                        navItemIndex = 2;
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
                    case R.id.nav_person:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_PERSON;
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


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

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
                    navigationView.getMenu().getItem(1).setTitle("Profil");
                }else {
                    navigationView.getMenu().getItem(1).setTitle("Üye Girişi");
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

    public void loadDataOnFirebase() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
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
                Toast.makeText(MainActivity.this, "There is a problem/n Please try again", Toast.LENGTH_SHORT).show();
            }
        });
        myStorage= FirebaseStorage.getInstance();
        storageRef= myStorage.getReference();
        StorageReference image = storageRef.child("images/profiles/"+mAuth.getCurrentUser().getUid());

        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(MainActivity.this).load(uri).fit().centerCrop().into(imgProfile);
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Picasso.with(MainActivity.this).load(R.drawable.ic_user).fit().centerCrop().into(imgProfile);
            }
        });
    }

}