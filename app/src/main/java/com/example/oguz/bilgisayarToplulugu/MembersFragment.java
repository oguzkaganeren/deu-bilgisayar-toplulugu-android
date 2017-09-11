package com.example.oguz.bilgisayarToplulugu;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
        if (mRecyclerView.getAdapter()==null){
            mRecyclerView.setAdapter(ourMembersAdapter);
        }
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
                    mRecyclerView.setAdapter(null);
                    membersList.clear();
                    ourMembersAdapter.notifyItemRangeRemoved(0, ourMembersAdapter.getItemCount());
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                            if (mAuth.getCurrentUser() != null && memberSnapshot.getKey() != null) {
                                final MembersInfo member = new MembersInfo();
                                if (memberSnapshot.hasChild("name-surname") && mAuth != null) {
                                    String nameSurname = memberSnapshot.child("name-surname").getValue(String.class);
                                    member.nameSurname = nameSurname;
                                } else {
                                    member.nameSurname = "Computer Society Member";
                                }
                                if (memberSnapshot.hasChild("status") && mAuth != null) {
                                    String status = memberSnapshot.child("status").getValue(String.class);
                                    member.status = status;
                                } else {
                                    member.status = "-";
                                }
                                if(memberSnapshot.hasChild("last-online-date")){
                                    Long val = memberSnapshot.child("last-online-date").getValue(Long.class);
                                    Date date=new Date(val);
                                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm");
                                    String dateText = df2.format(date);
                                    member.last_login=dateText;
                                }else{
                                    member.last_login="-";
                                }
                                if (memberSnapshot.hasChild("github") && mAuth != null) {
                                    String git = memberSnapshot.child("github").getValue(String.class);
                                    member.github = git;
                                } else {
                                    member.github = null;
                                }
                                if (memberSnapshot.hasChild("linkedin") && mAuth != null) {
                                    String linkedin = memberSnapshot.child("linkedin").getValue(String.class);
                                    member.linkedin = linkedin;
                                } else {
                                    member.linkedin = null;
                                }
                                if (memberSnapshot.hasChild("website") && mAuth != null) {
                                    String web = memberSnapshot.child("website").getValue(String.class);
                                    member.website = web;
                                } else {
                                    member.website = null;
                                }
                                if (memberSnapshot.hasChild("online")) {
                                    Boolean val = memberSnapshot.child("online").getValue(Boolean.class);
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

                                membersList.add(member);
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
