package com.example.oguz.topluluk;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oguz on 07-Aug-17.
 */

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder>{
    private Context context;
    private List<MembersInfo> dataList;
    public MembersAdapter(Context context, List<MembersInfo> dataList) {
        this.context=context;
        this.dataList = dataList;
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final MembersAdapter.MembersViewHolder membersViewHolder, int i) {
        MembersInfo wb = dataList.get(i);
        membersViewHolder.name_surname.setText(wb.NameSurname);
        membersViewHolder.status.setText(wb.status);
        if(wb.online!=null){
            ImageView imgCircle = membersViewHolder.online;
            imgCircle.setBackgroundResource(R.drawable.bg_circle);
            GradientDrawable drawable = (GradientDrawable) imgCircle.getBackground();
            if(wb.online){
                drawable.setColor(Color.GREEN);
            }else{
                drawable.setColor(Color.RED);
            }
        }
        Picasso.with(context)
                .load(wb.imgSrc)
                .placeholder(R.drawable.progress_animation)
                .fit()
                .into(membersViewHolder.imgSrc);


    }

    @Override
    public MembersAdapter.MembersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_members, viewGroup, false);
        return new MembersAdapter.MembersViewHolder(itemView);
    }
    public static class MembersViewHolder extends RecyclerView.ViewHolder {
        protected TextView name_surname;
        protected TextView status;
        protected ImageView online;
        protected ImageView imgSrc;
        public MembersViewHolder(View v) {
            super(v);
            name_surname =  (TextView) v.findViewById(R.id.right_name_surname);
            status = (TextView)  v.findViewById(R.id.right_status);
            online = (ImageView) v.findViewById(R.id.right_online);
            imgSrc=(ImageView)v.findViewById(R.id.right_profile_picture);

            //image üzerine uzun süre basıldığında yapılacaklar...
        }

    }
}
