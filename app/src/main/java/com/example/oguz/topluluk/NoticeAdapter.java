package com.example.oguz.topluluk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tyrantgit.explosionfield.ExplosionField;

/**
 * Created by Oguz on 18-Aug-17.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>{
    private Context context;
    private List<String> dataList;
    public NoticeAdapter(Context context, List<String> dataList) {
        this.context=context;
        this.dataList = dataList;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final NoticeAdapter.NoticeViewHolder noticeViewHolder, int i) {
        String wb = dataList.get(i);
        noticeViewHolder.description.setText(wb);
        noticeViewHolder.myContext=context;
    }

    @Override
    public NoticeAdapter.NoticeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_notice, viewGroup, false);
        return new NoticeAdapter.NoticeViewHolder(itemView);
    }
    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        protected TextView description;
        protected ImageButton descButton;
        protected CardView cardV;
        protected Context myContext;
        public NoticeViewHolder(View v) {
            super(v);
            description =  (TextView) v.findViewById(R.id.descript_notice);
            descButton=(ImageButton)v.findViewById(R.id.notice_button);
            cardV=(CardView)v.findViewById(R.id.card_view);
            cardV.setVisibility(View.VISIBLE);
            descButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = v.getContext().getSharedPreferences("notificationDEU", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    String notif=preferences.getString("notificationDEU",null);
                    String halfStartNotif=notif.substring(0,preferences.getString("notificationDEU",null).indexOf(description.getText().toString()));
                    halfStartNotif+="¨";
                    Log.d("b", halfStartNotif);
                    String halfLastNotif=notif.substring(preferences.getString("notificationDEU",null).indexOf(description.getText().toString()),preferences.getString("notificationDEU",null).length());
                    halfStartNotif+=halfLastNotif;
                    editor.putString("notificationDEU",halfStartNotif);
                    editor.commit();
                    Activity activity = (Activity) myContext;
                    ExplosionField explosionField=ExplosionField.attach2Window(activity);
                    explosionField.explode(cardV);
                    //Log.d("b",String.valueOf( preferences.getString("notificationDEU",null).indexOf(description.getText().toString())));
                    Log.d("b", preferences.getString("notificationDEU",null)+"----"+halfStartNotif);
                }
            });
        }

    }
}
