package com.example.oguz.bilgisayarToplulugu;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.vansuita.materialabout.builder.AboutBuilder;

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
        final MembersInfo swb=dataList.get(i);
        membersViewHolder.name_surname.setText(wb.nameSurname);
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
            Glide.with(((Activity) context).getApplication().getApplicationContext())
                    .load(R.mipmap.logomate32)
                    .centerCrop()
                    .fitCenter()
                    .into(membersViewHolder.imgSrc);
        }else{
            Glide.with(((Activity) context).getApplication().getApplicationContext())
                    .load(wb.imgSrc)
                    .centerCrop()
                    .fitCenter()
                    .into(membersViewHolder.imgSrc);
        }
        membersViewHolder.imgSrc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                final ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage("Loading");
                dialog.show();
                AboutPage myAbout=new AboutPage(context,swb);
                dialog.dismiss();
                return true;
            }
        });
        membersViewHolder.imgSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ImageView img=new ImageView(context);
                if(swb.imgSrc==null){
                    Glide.with(((Activity) context).getApplication().getApplicationContext())
                            .load(R.mipmap.logomate32)
                            .centerCrop()
                            .fitCenter()
                            .into(img);

                }else{
                    Glide.with(((Activity) context).getApplication().getApplicationContext())
                            .load(swb.imgSrc)
                            .centerCrop()
                            .fitCenter()
                            .into(img);
                }
                final MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                        .theme(Theme.LIGHT)
                        .customView(img,false);
                dialog.show();

            }
        });


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
        protected View view;
        public MembersViewHolder(View v) {
            super(v);
            name_surname =  (TextView) v.findViewById(R.id.right_name_surname);
            status = (TextView)  v.findViewById(R.id.right_status);
            online = (ImageView) v.findViewById(R.id.right_online);
            imgSrc=(ImageView)v.findViewById(R.id.right_profile_picture);
            view=v;
        }

    }
}
