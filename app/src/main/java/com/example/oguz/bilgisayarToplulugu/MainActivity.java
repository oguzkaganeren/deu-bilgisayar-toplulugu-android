package com.example.oguz.bilgisayarToplulugu;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codemybrainsout.ratingdialog.RatingDialog;
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
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private MaterialViewPager mViewPager;
    private NavigationView navigationViewRight;
    private FirebaseStorage myStorage;
    private StorageReference storageRef;
    private ArrayList<MembersInfo> membersList;
    private MembersAdapter ourMembersAdapter;
    private RecyclerView rcMembers;
    private DatabaseReference mDatabase;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Fragment fragment = null;
    private FloatingActionButton fab;
    private Class fragmentClass = null;
    private DrawerLayout rightDrawer;
    private DrawerLayout drawerLayout;
    private AccountHeader headerResult;
    private Menu rightMenu;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
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
        rightDrawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .session(7)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();
        navigationViewRight=(NavigationView) findViewById(R.id.nav_viewTwo);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = mViewPager.getViewPager();
        loadTabs();
        //account builder'ın ierisinde image loader olmadığı için aşağıdaki init'i ekledik
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Glide.with(getApplicationContext()).load(uri).placeholder(placeholder).into(imageView);
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
                return super.placeholder(ctx, tag);
            }
        });
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.menu_header_back)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        MainActivity.this.startActivity(intent);
                        return false;
                    }
                })
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Starter").withIcon(R.drawable.starter_24dp);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Account").withIcon(R.drawable.account_24dp);
        //SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(2).withName("Event History").withIcon(R.drawable.event_24dp);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(2).withName("Sign Out").withIcon(R.drawable.exit_to_app_black_24dp);
        //SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(2).withName("Settings").withIcon(R.drawable.setting_24dp);
        PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(2).withName("About Us").withIcon(R.drawable.about_24dp);
        PrimaryDrawerItem item7 = new PrimaryDrawerItem().withIdentifier(2).withName("Privacy Policy").withIcon(R.drawable.privacy_24dp);
//create the drawer and remember the `Drawer` result object
       final Drawer left = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2,
                       // item3,
                        item4,
                        new DividerDrawerItem(),
                        //item5,
                        item6,
                        item7
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position){
                            case 3:
                                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                MainActivity.this.startActivity(intent);
                                break;
                            case 4:
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").child("online").setValue(false);
                                mAuth.signOut();
                                loadTabs();
                                setDrawerState(false);
                                Toast.makeText(MainActivity.this, "Signout successful", Toast.LENGTH_SHORT).show();
                                break;
                            case 6:
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                dialogBuilder.setTitle("DEU Bilgisayar Topluluğu");
                                View about =  getLayoutInflater().inflate(R.layout.view_aboutus, null);
                                ((TextView)about.findViewById(R.id.txt_aboutus))
                                        .setText(" Bu hayatta iki tip insan olduğuna inanıyoruz :" +
                                                " Yükselmek için etrafındakileri bastırıp yüksekleri " +
                                                "hedefleyenler, etrafındakilerle birlikte yükselmek için" +
                                                " emek harcayanlar. Eğer sen de bu ikinci kategoride kendini " +
                                                "görüyorsan ve aramıza katılmak istiyorsan lütfen kayıt ol." +
                                                " \n\nSeni aramızda görmek istiyoruz Öncelikle kafanda " +
                                                "\"Ya ben fazla bir şey bilmiyorum nasıl yardımcı olabilirim ki? \"" +
                                                " gibi bir düşünce varsa lütfen onlardan kurtul. Dünya çapındaki büyük," +
                                                " özgür yazılım projelerine destek olmayı, içimizdeki bilgi paylaşımını " +
                                                "doruklara ulaştırmayı planlıyoruz. Çünkü biliyoruz ki dünya çapında \"zilyon\"" +
                                                " programcı, mühendis var. Bir fark yaratmak istiyoruz.");
                                dialogBuilder.setIcon(R.mipmap.logomate32);
                                dialogBuilder.setView(about);
                                dialogBuilder.show();
                                break;
                            case 7:
                                LinearLayout ln = new LinearLayout(MainActivity.this);
                                ln.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                                ln.setOrientation(LinearLayout.VERTICAL);
                                WebView web=new WebView(getApplicationContext());
                                web.loadUrl("file:///android_asset/privacy_policy.html");
                                ln.addView(web);
                                AlertDialog.Builder dialogBuilderP = new AlertDialog.Builder(MainActivity.this);
                                dialogBuilderP.setView(ln);
                                dialogBuilderP.show();
                                break;
                            case 9:
                                break;

                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        drawerLayout = left.getDrawerLayout();
        actionBarDrawerToggle=  new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.material_drawer_open, R.string.material_drawer_close);
        if (mAuth.getCurrentUser() != null) {
            // User is logged in
            mDatabase = FirebaseDatabase.getInstance().getReference();
            loadDataOnFirebase();
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").child("last-online-date").setValue(ServerValue.TIMESTAMP);
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").child("online").setValue(true);
            setDrawerState(true);
        }else {
            setDrawerState(false);
        }
        //if the user did not login, login page is showed
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                MainActivity.this.startActivity(intent);
                 }
            }
        });

        rightDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Called when a drawer's position changes.
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Called when a drawer has settled in a completely closed state.
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if(mAuth.getCurrentUser()!=null) {

                    //members kısmı
                    if (fragmentClass == null && fragment == null) {
                        fragmentClass = MembersFragment.class;
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.members_frame, fragment).commit();
                    }
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
                            return myEvents;
                        }else {
                            return null;
                        }

                    case 2:
                        if(mAuth.getCurrentUser() != null){
                            return myNotices;
                        }else{
                            return null;
                        }
                    case 3:
                        if(mAuth.getCurrentUser() != null){
                            //not complete
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
                    return 3;
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
                            return "Events";
                        }else{
                            return null;
                        }
                    case 2:
                        if(mAuth.getCurrentUser() != null) {
                            return "Notices";
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
                        fabStatusAccordingToRole();
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.events_add_white));
                        fab.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        });
                        return HeaderDesign.fromColorAndDrawable(
                                getResources().getColor(R.color.event_bg),getResources().getDrawable(R.drawable.event_back));

                    case 2:
                        fab.setVisibility(View.GONE);
                        return HeaderDesign.fromColorAndDrawable(
                                getResources().getColor( R.color.notice_bg),getResources().getDrawable(R.drawable.notice_back));
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    @Override
    public void onResume(){
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
            // User is logged in
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").child("online").setValue(true);
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser() != null) {
            // User is logged in
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").child("online").setValue(false);
        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.left_exit);
    }
    //kullanıcı girişi yoksa menuleri görmemesi için kullanılan method
    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            rightDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            actionBarDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_SETTLING);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.syncState();

        }
        else {
            if(rightMenu!=null){
                rightMenu.setGroupVisible(R.id.main_menu_group,false);
                navigationViewRight.setEnabled(false);
            }
            fab.setVisibility(View.GONE);
            actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.login_white_24dp);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            rightDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            actionBarDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_SETTLING);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarDrawerToggle.syncState();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        rightMenu=menu;
        if (mAuth.getCurrentUser() != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.right, menu);
        }
        return true;
    }
    public void rightMenu(MenuItem item) {
        if(mAuth.getCurrentUser()!=null) {

            //members kısmı
            if (fragmentClass == null && fragment == null) {
                fragmentClass = RightDrawerFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.members_frame, fragment).commit();
            }


            //-----------------------------------
            drawerLayout.openDrawer(navigationViewRight);
        }
    }
    public void loadDataOnFirebase() {
        final IProfile profile =  new ProfileDrawerItem();
        profile.withIdentifier(0);
        profile.withEmail(mAuth.getCurrentUser().getEmail().toString());
        headerResult.addProfile(profile,0);
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild("name-surname")) {
                        profile.withName(snapshot.child("name-surname").getValue().toString());
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
                profile.withIcon(R.mipmap.logomate32);
                headerResult.updateProfile(profile);
            }
        });
    }
    public void fabStatusAccordingToRole(){
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("private").addValueEventListener(new ValueEventListener() {
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
