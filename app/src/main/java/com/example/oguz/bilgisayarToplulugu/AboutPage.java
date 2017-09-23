package com.example.oguz.bilgisayarToplulugu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.view.View;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.vansuita.materialabout.builder.AboutBuilder;

import java.util.List;

/**
 * Created by Oguz on 23-Sep-17.
 */

public class AboutPage {
    private Context context;
    public AboutPage(final Context context,final MembersInfo mi){
        this.context=context;
        final AboutBuilder ab = AboutBuilder.with(context) .setLinksAnimated(true)
                .setShowAsCard(false);
        ab.setIconColor(R.color.md_deep_orange_800);
        ab.setCover(R.drawable.profilebackground);
        ab.addEmailLink(mi.email);
        if(mi.nameSurname!=null){
            ab.setName(mi.nameSurname);
        }
        if(mi.status!=null){
            ab.setSubTitle(mi.status);
        }
        if(mi.github!=null&&!mi.github.trim().isEmpty()){
            ab.addGitHubLink(mi.github);
        }
        if(mi.website!=null&&!mi.website.trim().isEmpty()){
            ab.addWebsiteLink(mi.website);
        }
        if(mi.linkedin!=null&&!mi.linkedin.trim().isEmpty()){
            ab.addLinkedInLink(mi.linkedin);
        }
        if(mi.facebook!=null&&!mi.facebook.trim().isEmpty()){
            ab.addFacebookLink(mi.facebook);
        }
        if(mi.googlePlay!=null&&!mi.googlePlay.trim().isEmpty()){
            ab.addGooglePlayStoreLink(mi.googlePlay);
        }
        if(mi.instagram!=null&&!mi.instagram.trim().isEmpty()){
            ab.addInstagramLink(mi.instagram);
        }
        if(mi.skype!=null&&!mi.skype.trim().isEmpty()){
            ab.addSkypeLink(mi.skype);
        }
        if(mi.twitter!=null&&!mi.twitter.trim().isEmpty()){
            ab.addTwitterLink(mi.twitter);
        }
        if(mi.whatsapp!=null&&!mi.whatsapp.trim().isEmpty()){
            ab.addWhatsappLink(mi.nameSurname,mi.whatsapp);
        }
        if(mi.youtube!=null&&!mi.youtube.trim().isEmpty()){
            ab.addYoutubeChannelLink(mi.youtube);
        }
        if(mi.slack!=null&&!mi.slack.trim().isEmpty()){
            ab.addLink(R.mipmap.slackmini,"Slack",new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!isPackageExisted("com.slack.android")){
                        final MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                                .negativeText("Cancel")
                                .theme(Theme.LIGHT)
                                .titleColor(context.getResources().getColor(R.color.md_dark_dialogs))
                                .content(mi.slack).contentGravity(GravityEnum.CENTER);
                        dialog.show();
                    }else{
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("*/*");
                        intent.setPackage("com.slack.android");
                        context.startActivity(Intent.createChooser(intent, "Open Slack"));
                    }

                }
            });
        }
        if(mi.snap!=null&&!mi.snap.trim().isEmpty()){

            ab.addLink(R.mipmap.snapchatmini,"Snapchat",new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!isPackageExisted("com.snapchat.android")){
                        final MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                                .negativeText("Cancel")
                                .theme(Theme.LIGHT)
                                .titleColor(context.getResources().getColor(R.color.md_dark_dialogs))
                                .content(mi.snap).contentGravity(GravityEnum.CENTER);
                        dialog.show();
                    }else{
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("*/*");
                        intent.setPackage("com.snapchat.android");
                        context.startActivity(Intent.createChooser(intent, "Open Snapchat"));
                    }

                }
            });
        }
        if(mi.appStore!=null&&!mi.appStore.trim().isEmpty()){
            ab.addLink(R.mipmap.appstoremini,"Appstore","https://itunes.apple.com/us/developer/"+mi.appStore);
        }

        if(mi.imgSrc==null){
            ab.setPhoto(R.mipmap.logo);
            View view=ab.build();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setView(view);
            dialogBuilder.show();
        }else{
            Glide
                    .with(((Activity) context).getApplication().getApplicationContext())
                    .load(mi.imgSrc)
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

    }
    public boolean isPackageExisted(String targetPackage){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm =  context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

}
