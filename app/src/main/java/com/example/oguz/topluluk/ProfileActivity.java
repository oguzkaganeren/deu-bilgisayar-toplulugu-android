package com.example.oguz.topluluk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

/**
 * Created by Oguz on 19-Jul-17.
 */

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, ProfileActivity.class);
        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName("Your Full Name")
                .setSubTitle("Mobile Developer")
                .setBrief("I'm warmed of mobile technologies. Ideas maker, curious and nature lover.")
                .setAppIcon(R.mipmap.star)
                .setAppName(R.string.app_name)
                /*.addGitHubLink("user")
                .addFacebookLink("user")*/
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .build();
        RelativeLayout.LayoutParams lp =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ImageButton imgBtn=new ImageButton(this);
        imgBtn.setImageResource(R.drawable.account_24dp);
        lp.addRule(RelativeLayout.ALIGN_RIGHT);
        imgBtn.setLayoutParams(lp);
        imgBtn.setClickable(true);
        imgBtn.setBackgroundColor(Color.TRANSPARENT);
        addContentView(view, lp);
        addContentView(imgBtn,lp);
    }
}
