package com.example.oguz.bilgisayarToplulugu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

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

        View v = inflater.inflate(R.layout.fragment_notice, container, false);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
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
        Log.d("a",String.valueOf(descriptions.isEmpty())+descriptions);
            ArrayList<String> desArray=new ArrayList<String>();
        if(descriptions!=null&&!descriptions.isEmpty()){
            for (int i=descriptions.split("~").length-1;i>=0;i--)
            {
                if(!descriptions.split("~")[i].startsWith("¨")){
                    notifList.add(descriptions.split("~")[i].split("é")[0]);
                }

            }
        }
        ourNotificationsAdapter=new NoticeAdapter(getActivity(),notifList);

    }
    }

