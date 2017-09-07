package com.example.oguz.bilgisayarToplulugu;

/**
 * Created by Oguz on 08-Jun-17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class EventFragment extends Fragment{
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    private EventAdapter eventAdapter;
    private ArrayList<EventsInfo> eventList;
    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
       if (mAuth.getCurrentUser() != null) {
            eventList=new ArrayList<EventsInfo>();
            loadMembers();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //bu kısım fragmenlere özel

        View v = inflater.inflate(R.layout.fragment_event, container, false);

        // 2.
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // 3.
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rcview_event_frag);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        return v;
    }
    public void loadMembers(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        eventAdapter=new EventAdapter(getActivity(),eventList);

        ValueEventListener getList= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                mRecyclerView.setAdapter(null);
                eventList.clear();
                eventAdapter.notifyItemRangeRemoved(0, eventAdapter.getItemCount());
                if (dataSnapshot.exists()) {
                    for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                        if (mAuth.getCurrentUser() != null && memberSnapshot.getKey() != null) {
                            final EventsInfo event = new EventsInfo();
                            if (memberSnapshot.hasChild("address") && mAuth != null) {
                                String address = memberSnapshot.child("address").getValue(String.class);
                                event.address = address;
                            }
                            if (memberSnapshot.hasChild("title") && mAuth != null) {
                                String title = memberSnapshot.child("title").getValue(String.class);
                                event.title = title;
                            }
                            if (memberSnapshot.hasChild("description") && mAuth != null) {
                                String desc = memberSnapshot.child("description").getValue(String.class);
                                event.description = desc;
                            }
                            if (memberSnapshot.hasChild("date") && mAuth != null) {
                                Date date = memberSnapshot.child("date").getValue(Date.class);
                                event.date = date;
                            }
                            if (memberSnapshot.hasChild("location") && mAuth != null) {
                                String loc = memberSnapshot.child("location").getValue(String.class);
                                event.location = loc;
                            }
                            if (memberSnapshot.hasChild("uid") && mAuth != null) {
                                String uid = memberSnapshot.child("uid").getValue(String.class);
                                event.uid = uid;
                            }
                            if (memberSnapshot.hasChild("createdTimestamp") && mAuth != null) {
                                Long addate = memberSnapshot.child("createdTimestamp").getValue(Long.class);
                                event.addingDate = addate;
                            }
                            event.eventKey=memberSnapshot.getKey();
                            eventList.add(event);
                            Collections.sort(eventList, new Comparator<EventsInfo>() {
                                public int compare(EventsInfo o1, EventsInfo o2) {
                                    return o1.getAddingDate().compareTo(o2.getAddingDate());
                                }
                            });
                        }

                    }
                    Collections.reverse(eventList);

                    mRecyclerView.setAdapter(eventAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Toast.makeText(getActivity(), "Firebase problem", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.child("events").orderByChild("createdTimestamp").limitToLast(20).addValueEventListener(getList);
    }

}
