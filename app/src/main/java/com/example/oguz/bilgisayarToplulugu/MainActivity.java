package com.example.oguz.bilgisayarToplulugu;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.util.Log;
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
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
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
    private FloatingActionButton fab;
    private Class fragmentClass = null;
    private DrawerLayout drawerLayout;
    private static Context mContext;
    private AccountHeader headerResult;
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
       // navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationViewRight=(NavigationView) findViewById(R.id.nav_viewTwo);
        // Navigation view header
        //menu içerisindeki değerleri verme kısmı
        /*
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        */
        profile_image_right=(ImageView)findViewById(R.id.right_profile_picture);

        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = mViewPager.getViewPager();
        loadTabs();
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.menu_header_back)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Starter").withIcon(R.drawable.starter_24dp);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Account").withIcon(R.drawable.account_24dp);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(2).withName("Event History").withIcon(R.drawable.event_24dp);
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(2).withName("Sign Out").withIcon(R.drawable.ic_exit_to_app_black_24dp);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(2).withName("Settings").withIcon(R.drawable.setting_24dp);
        SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(2).withName("About Us").withIcon(R.drawable.about_24dp);
        SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(2).withName("Privacy Policy").withIcon(R.drawable.privacy_24dp);
//create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                        item3,
                        item4,
                        new DividerDrawerItem(),
                        item5,
                        item6,
                        item7
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position){
                            case 3:
                                Intent intent = new Intent(getContext(), ProfileActivity.class);
                                MainActivity.this.startActivity(intent);
                                break;
                            case 4:
                                break;
                            case 5:
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("online").setValue(false);
                                mAuth.signOut();
                                loadTabs();
                                setDrawerState(false);
                                Toast.makeText(MainActivity.this, "Signout successful", Toast.LENGTH_SHORT).show();
                                break;
                            case 7:
                                break;
                            case 8:
                                break;
                            case 9:
                                break;

                        }
                        return false;
                    }
                })
                .build();
        drawerLayout = result.getDrawerLayout();
        actionBarDrawerToggle=result.getActionBarDrawerToggle();
        if (mAuth.getCurrentUser() != null) {

            // User is logged in
            mDatabase = FirebaseDatabase.getInstance().getReference();
            loadDataOnFirebase();
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("last-online-date").setValue(ServerValue.TIMESTAMP);
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("online").setValue(true);
            setDrawerState(true);
        }else {
            setDrawerState(false);
        }
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
    }
    private void loadTabs(){
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
                        fab.setVisibility(View.GONE);
                        return HeaderDesign.fromColorAndDrawable(
                                getResources().getColor(R.color.content_bg), getResources().getDrawable(R.drawable.content_back));

                    case 1:
                        fab.setVisibility(View.GONE);
                        return HeaderDesign.fromColorAndDrawable(
                                getResources().getColor( R.color.notice_bg),getResources().getDrawable(R.drawable.notice_back));
                    case 2:
                        fabStatusAccordingToRole();
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.events_add_white));
                        fab.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), AddEventActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        });
                        return HeaderDesign.fromColorAndDrawable(
                                getResources().getColor(R.color.event_bg),getResources().getDrawable(R.drawable.event_back));
                    case 3:
                        fab.setVisibility(View.GONE);
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
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            actionBarDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_SETTLING);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.syncState();

        }
        else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            actionBarDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_SETTLING);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarDrawerToggle.syncState();
        }
    }
    /*
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
                        loadTabs();
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
    }*/

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
        final IProfile profile =  new ProfileDrawerItem();
        profile.withIdentifier(0);
        headerResult.addProfile(profile,0);
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("name-surname")) {
                        profile.withName(snapshot.child("name-surname").getValue().toString());
                        headerResult.updateProfile(profile);
                    }
                    if (snapshot.hasChild("website")) {
                        profile.withEmail(snapshot.child("website").getValue().toString());
                        headerResult.updateProfile(profile);
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
                profile.withIcon(uri);
                headerResult.updateProfile(profile);
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                profile.withIcon(R.mipmap.logo);
                headerResult.updateProfile(profile);
            }
        });
    }
    public void fabStatusAccordingToRole(){
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("role")) {
                    fab.setVisibility(View.VISIBLE);
                }else{
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(mAuth==null){
                    Toast.makeText(MainActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
