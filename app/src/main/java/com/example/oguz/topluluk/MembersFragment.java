package com.example.oguz.topluluk;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by Oguz on 12-Aug-17.
 */

public class MembersFragment  extends Fragment {
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private MembersAdapter ourMembersAdapter;
    private ArrayList<MembersInfo> membersList;
    public MembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        membersList=new ArrayList<MembersInfo>();
        loadMembers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //bu kısım fragmenlere özel

        View v = inflater.inflate(R.layout.fragment_members, container, false);
        v.findViewById(R.id.right_profile_picture).setDrawingCacheEnabled(true);

        // 2.
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // 3.
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rcmembers);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //final View myToolBar = getActivity().findViewById(R.id.toolbar);
        // final View myTabs = getActivity().findViewById(R.id.tabs);
        // final View myBarLa = getActivity().findViewById(R.id.myBarLayout);
        if (mRecyclerView.getAdapter()==null){
            mRecyclerView.setAdapter(ourMembersAdapter);
        }

        return v;
    }
    public void loadMembers(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final MembersInfo member = new MembersInfo();
        FirebaseStorage myStorage=FirebaseStorage.getInstance();
        final StorageReference storageRef= myStorage.getReference();
        ourMembersAdapter=new MembersAdapter(getActivity(),membersList);
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                for (DataSnapshot memberSnapshot: dataSnapshot.getChildren()) {
                    if(memberSnapshot.hasChild("name-surname")){
                        String nameSurname = memberSnapshot.child("name-surname").getValue(String.class);
                        member.NameSurname=nameSurname;
                    }else{
                        member.NameSurname="Computer Society Member";
                    }
                    if(memberSnapshot.hasChild("status")){
                        String status = memberSnapshot.child("status").getValue(String.class);
                        member.status=status;
                    }else{
                        member.status="-";
                    }
                    if(memberSnapshot.hasChild("last-login-date")){
                        String date = memberSnapshot.child("last-login-date").getValue(String.class);
                        member.date=date;
                    }else{
                        member.date="-";
                    }

                    StorageReference image = storageRef.child("images/profiles/"+memberSnapshot.getKey().toString());
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            member.imgSrc=uri.toString();
                            mRecyclerView.setAdapter(ourMembersAdapter);
                        }}).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            member.imgSrc="@drawable/ic_user.xml";
                            mRecyclerView.setAdapter(ourMembersAdapter);
                        }
                    });

                    membersList.add(member);


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
