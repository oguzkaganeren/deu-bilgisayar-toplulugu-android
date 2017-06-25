package com.example.oguz.topluluk;

/**
 * Created by Oguz on 08-Jun-17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ContentFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    WebDataAdapter myAdap;
    public ContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myAdap=new WebDataAdapter(createContent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //bu kısım fragmenlere özel
        View v = inflater.inflate(R.layout.fragment_content, parent, false);

        // 2.
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // 3.
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rcview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(myAdap);

        return v;
    }
    private List<WebDataInfo> createContent(){
        List<WebDataInfo> ls=new ArrayList<WebDataInfo>();
        for (int i=0;i<10;i++)
        {
            WebDataInfo wb=new WebDataInfo();
            wb.title="Deneme";
            wb.link="link";
            wb.imgSrc=R.drawable.about_24dp;
            wb.description="Sometimes, the easiest way to get inspired is to see how others generate content.  Here are some resources that are full of examples to get the juices flowing.";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            wb.date=currentDateandTime;
            ls.add(wb);
        }

        return ls;
    }

}