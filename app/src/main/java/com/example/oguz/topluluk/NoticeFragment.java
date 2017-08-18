package com.example.oguz.topluluk;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
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
import java.util.Date;

/**
 * Created by Oguz on 12-Aug-17.
 */

public class NoticeFragment  extends Fragment {
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    private NoticeAdapter ourNotificationsAdapter;
    private ArrayList<String> notifList;
    public NoticeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            notifList=new ArrayList<String>();
            loadNotifications();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //bu kısım fragmenlere özel

        View v = inflater.inflate(R.layout.fragment_notice, container, false);

        // 2.
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // 3.
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rcview_notice);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(ourNotificationsAdapter);

        return v;
    }
    public void loadNotifications(){
            SharedPreferences preferences = getActivity().getSharedPreferences("notificationDEU", 0);
            String descriptions = preferences.getString("notificationDEU", "");
        Log.d("a",String.valueOf(descriptions.isEmpty()));
            ArrayList<String> desArray=new ArrayList<String>();
        if(descriptions!=null){
            for (int i=descriptions.split("~").length-1;i>=0;i--)
            {
                notifList.add(descriptions.split("~")[i].split("é")[0]);
            }
        }
        ourNotificationsAdapter=new NoticeAdapter(getActivity(),notifList);

    }
    }

