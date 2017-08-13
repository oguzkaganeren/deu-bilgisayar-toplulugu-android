package com.example.oguz.topluluk;

import android.content.Context;
import android.content.Intent;
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
        membersViewHolder.date.setText(wb.date);
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
        protected TextView date;
        protected ImageView imgSrc;
        public MembersViewHolder(View v) {
            super(v);
            name_surname =  (TextView) v.findViewById(R.id.right_name_surname);
            status = (TextView)  v.findViewById(R.id.right_status);
            date = (TextView) v.findViewById(R.id.right_last_time);
            imgSrc=(ImageView)v.findViewById(R.id.right_profile_picture);

            //image üzerine uzun süre basıldığında yapılacaklar...
        }

    }
}
