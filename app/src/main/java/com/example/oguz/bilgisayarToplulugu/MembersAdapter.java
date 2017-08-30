package com.example.oguz.bilgisayarToplulugu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

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
        if(wb.imgSrc==null){
            Glide.with(context)
                    .load(R.mipmap.logo)
                    .centerCrop()
                    .into(membersViewHolder.imgSrc);
        }else{
            Glide.with(context)
                    .load(wb.imgSrc)
                    .centerCrop()
                    .into(membersViewHolder.imgSrc);
        }



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
