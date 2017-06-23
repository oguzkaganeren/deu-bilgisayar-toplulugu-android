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

    public ContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_content, null , false);
        RecyclerView myRc=(RecyclerView)v.findViewById(R.id.rcview);
        myRc.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myRc.setLayoutManager(llm);
        WebDataAdapter myAdap=new WebDataAdapter(createContent());
        myRc.setAdapter(myAdap);
    }
    private List<WebDataInfo> createContent(){
        List<WebDataInfo> ls=new ArrayList<WebDataInfo>();
        for (int i=0;i<10;i++)
        {
            WebDataInfo wb=new WebDataInfo();
            wb.title="Deneme";
            wb.link="link";
            wb.description="desp";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            wb.date=currentDateandTime;
            ls.add(wb);
        }

        return ls;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

}