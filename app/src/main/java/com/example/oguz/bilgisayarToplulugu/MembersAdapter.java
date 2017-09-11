package com.example.oguz.bilgisayarToplulugu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
                    .fitCenter()
                    .into(membersViewHolder.imgSrc);
        }else{
            Glide.with(context)
                    .load(wb.imgSrc)
                    .centerCrop()
                    .fitCenter()
                    .into(membersViewHolder.imgSrc);
        }
        membersViewHolder.imgSrc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
               final AboutBuilder ab = AboutBuilder.with(context) .setLinksAnimated(true)
                       .setShowAsCard(false).addFiveStarsAction().setWrapScrollView(true).setAppName(R.string.app_name).addShareAction(R.string.app_name);
                ab.setCover(R.drawable.profilebackground);
                if(swb.NameSurname!=null){
                    ab.setName(swb.NameSurname);
                }
                if(swb.status!=null){
                    ab.setSubTitle(swb.status);
                }
                if(swb.github!=null&&!swb.github.trim().isEmpty()){
                    ab.addGitHubLink(swb.github);
                }
                if(swb.website!=null&&!swb.website.trim().isEmpty()){
                    ab.addWebsiteLink(swb.website);
                }
                if(swb.linkedin!=null&&!swb.linkedin.trim().isEmpty()){
                    ab.addLinkedInLink(swb.linkedin);
                }
                if(swb.imgSrc==null){
                    ab.setPhoto(R.mipmap.logo);
                    View view=ab.build();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    dialogBuilder.setView(view);
                    dialogBuilder.show();
                }else{
                    Glide
                            .with(context)
                            .load(swb.imgSrc)
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(96, 96) {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                    // Do something with bitmap here.
                                    ab.setPhoto(bitmap);
                                    View view=ab.build();
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                    dialogBuilder.setView(view);
                                    dialogBuilder.show();

                                }
                            });
                }

                return true;
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
        public MembersViewHolder(View v) {
            super(v);
            name_surname =  (TextView) v.findViewById(R.id.right_name_surname);
            status = (TextView)  v.findViewById(R.id.right_status);
            online = (ImageView) v.findViewById(R.id.right_online);
            imgSrc=(ImageView)v.findViewById(R.id.right_profile_picture);

        }

    }
}
