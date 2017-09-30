package com.example.oguz.bilgisayarToplulugu;

import android.animation.Animator;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


/**
 * Created by Oguz on 12-Aug-17.
 */

public class MembersFragment  extends Fragment {
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    private MembersAdapter ourMembersAdapter;
    private ArrayList<MembersInfo> membersList;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public MembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            membersList=new ArrayList<MembersInfo>();
            loadMembers();
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_members, container, false);
        v.findViewById(R.id.right_profile_picture).setDrawingCacheEnabled(true);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rcmembers);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        return v;
    }
    public void loadMembers(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseStorage myStorage=FirebaseStorage.getInstance();
        final StorageReference storageRef= myStorage.getReference();
        ourMembersAdapter=new MembersAdapter(getActivity(),membersList);
            mDatabase.child("users")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Is better to use a List, because you don't know the size
                    // of the iterator returned by dataSnapshot.getChildren() to
                    // initialize the array
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                            if (mAuth.getCurrentUser() != null && memberSnapshot.getKey() != null) {

                                final MembersInfo member = new MembersInfo();


                                if (memberSnapshot.child("profile").hasChild("name-surname") && mAuth != null) {
                                    String nameSurname = memberSnapshot.child("profile").child("name-surname").getValue(String.class);
                                    member.nameSurname = nameSurname;
                                } else {
                                    member.nameSurname = "Computer Society Member";
                                }
                                if (memberSnapshot.child("profile").hasChild("status") && mAuth != null) {
                                    String status = memberSnapshot.child("profile").child("status").getValue(String.class);
                                    member.status = status;
                                } else {
                                    member.status = "-";
                                }
                                if(memberSnapshot.child("profile").hasChild("last-online-date")){
                                    Long val = memberSnapshot.child("profile").child("last-online-date").getValue(Long.class);
                                    Date date=new Date(val);
                                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm");
                                    String dateText = df2.format(date);
                                    member.last_login=dateText;
                                    member.uid=memberSnapshot.getKey().toString();
                                }else{
                                    member.last_login="-";
                                    member.uid=memberSnapshot.getKey().toString();
                                }
                                if (memberSnapshot.child("profile").hasChild("github") && mAuth != null) {
                                    String git = memberSnapshot.child("profile").child("github").getValue(String.class);
                                    member.github = git;
                                } else {
                                    member.github = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("google-play-developer") && mAuth != null) {
                                    String googlePlay = memberSnapshot.child("profile").child("google-play-developer").getValue(String.class);
                                    member.googlePlay = googlePlay;
                                } else {
                                    member.googlePlay = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("appstore-developer") && mAuth != null) {
                                    String appStore = memberSnapshot.child("profile").child("appstore-developer").getValue(String.class);
                                    member.appStore = appStore;
                                } else {
                                    member.appStore = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("skype") && mAuth != null) {
                                    String skype = memberSnapshot.child("profile").child("skype").getValue(String.class);
                                    member.skype = skype;
                                } else {
                                    member.skype = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("slack") && mAuth != null) {
                                    String slack = memberSnapshot.child("profile").child("slack").getValue(String.class);
                                    member.slack = slack;
                                } else {
                                    member.slack = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("snapchat") && mAuth != null) {
                                    String snapchat = memberSnapshot.child("profile").child("snapchat").getValue(String.class);
                                    member.snap = snapchat;
                                } else {
                                    member.snap = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("twitter") && mAuth != null) {
                                    String twitter = memberSnapshot.child("profile").child("twitter").getValue(String.class);
                                    member.twitter = twitter;
                                } else {
                                    member.twitter = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("facebook") && mAuth != null) {
                                    String facebook = memberSnapshot.child("profile").child("facebook").getValue(String.class);
                                    member.facebook = facebook;
                                } else {
                                    member.facebook = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("whatsapp") && mAuth != null) {
                                    String whatsapp = memberSnapshot.child("profile").child("whatsapp").getValue(String.class);
                                    member.whatsapp = whatsapp;
                                } else {
                                    member.whatsapp = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("youtube") && mAuth != null) {
                                    String youtube = memberSnapshot.child("profile").child("youtube").getValue(String.class);
                                    member.youtube = youtube;
                                } else {
                                    member.youtube = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("linkedin") && mAuth != null) {
                                    String linkedin = memberSnapshot.child("profile").child("linkedin").getValue(String.class);
                                    member.linkedin = linkedin;
                                } else {
                                    member.linkedin = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("website") && mAuth != null) {
                                    String web = memberSnapshot.child("profile").child("website").getValue(String.class);
                                    member.website = web;
                                } else {
                                    member.website = null;
                                }
                                if (memberSnapshot.child("profile").hasChild("online")) {
                                    Boolean val = memberSnapshot.child("profile").child("online").getValue(Boolean.class);
                                    if (memberSnapshot.getKey() == mAuth.getCurrentUser().getUid()) {
                                        member.online = true;
                                    } else {
                                        member.online = val;
                                    }

                                }
                               StorageReference image = storageRef.child("images/profiles/" + memberSnapshot.getKey().toString());
                                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        member.imgSrc = uri.toString();
                                        mRecyclerView.setAdapter(ourMembersAdapter);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        member.imgSrc = null;
                                        mRecyclerView.setAdapter(ourMembersAdapter);
                                    }
                                });
                                boolean isChanged=false;
                                for(int i=0;i<membersList.size();i++){
                                    if(membersList.get(i).uid.equals(member.uid)){
                                        membersList.set(i,member);
                                        isChanged=true;
                                        Log.d("he", "onDataChange: "+String.valueOf(member.uid)+"name"+member.nameSurname);
                                        break;
                                    }
                                }
                                if(!isChanged){
                                    membersList.add(member);
                                }else{
                                    isChanged=false;
                                }
                                Collections.sort(membersList);
                                Collections.reverse(membersList);
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                   // Toast.makeText(getActivity(), "Firebase problem", Toast.LENGTH_SHORT).show();
                }
            });
        }

}
