package com.example.oguz.bilgisayarToplulugu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.StackingBehavior;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.vansuita.materialabout.builder.AboutBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Oguz on 19-Jul-17.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private ShimmerTextView nameSurname;
    private ShimmerTextView status;
    private ImageView selfImg;
    private Integer[] settingImg={R.id.profile_password,R.id.profile_mail,R.id.profile_github,R.id.profile_googleplay,R.id.profile_appstore,R.id.profile_website,R.id.profile_whattsapp,R.id.profile_slack,R.id.profile_linkedin,R.id.profile_skype,R.id.profile_facebook,R.id.profile_instagram,R.id.profile_snapchat,R.id.profile_twitter,R.id.profile_youtube};
    private ImageView[] imageViews;
    private FirebaseStorage myStorage;
    private DatabaseReference mDatabase;
    final MembersInfo member = new MembersInfo();
    private ImageView userProfilePhoto;
    private Uri filePath;
    private final RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    private ProgressDialog pd;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        myStorage=FirebaseStorage.getInstance();
        storageRef= myStorage.getReference();
        imageViews=new ImageView[15];
        selfImg=(ImageView)findViewById(R.id.profile_self);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.ABSOLUTE);
        anim.setDuration(500);
        if (mAuth.getCurrentUser() == null) {
            finish();

        }
        for(int i=0;i<settingImg.length;i++){
            imageViews[i]=(ImageView)findViewById(settingImg[i]);
            imageViews[i].setOnClickListener(this);
        }
        nameSurname=(ShimmerTextView) findViewById(R.id.user_profile_name);
        status=(ShimmerTextView) findViewById(R.id.user_profile_status);
        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(2000)
                .setStartDelay(100);
        shimmer.start(nameSurname);
        Shimmer shimmerTwo = new Shimmer();
        shimmer.setDuration(2000)
                .setStartDelay(400);
        shimmer.start(status);
        userProfilePhoto=(ImageView)findViewById(R.id.user_profile_photo);
        final MaterialDialog.Builder dialogUp = new MaterialDialog.Builder(ProfileActivity.this)
                .negativeText("Cancel")
                .theme(Theme.LIGHT)
                .titleColor(getResources().getColor(R.color.md_dark_dialogs))
                .widgetColor(getResources().getColor(R.color.md_dark_dialogs))
                .positiveColor(getResources().getColor(R.color.md_black_1000))
                .negativeColor(getResources().getColor(R.color.md_dark_dialogs))
                .positiveText("Done");

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        loadDataOnFirebase();
        nameSurname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUp.title("Change your name and surname");
                loadDialog(dialogUp,"name-surname");
            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUp.title("Change your status");
                loadDialog(dialogUp,"status");
            }
        });
        userProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 111);//one can be replaced with any action code

            }
        });
        selfImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutPage aboutPage=new AboutPage(ProfileActivity.this,member);

            }
        });




    }
    @Override
    public void onClick(View v) {
        final MaterialDialog.Builder dialog = new MaterialDialog.Builder(ProfileActivity.this)
                .negativeText("Cancel")
                .theme(Theme.LIGHT)
                .titleColor(getResources().getColor(R.color.md_dark_dialogs))
                .widgetColor(getResources().getColor(R.color.md_dark_dialogs))
                .positiveColor(getResources().getColor(R.color.md_black_1000))
                .negativeColor(getResources().getColor(R.color.md_dark_dialogs))
                .positiveText("Done");

        v.startAnimation(anim);
        switch (v.getId()) {
            case R.id.profile_password:
                // do your code
                dialog.title("Change your password");
                dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        mAuth.getCurrentUser().updatePassword(dialog.getInputEditText().getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Password is updated!", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO
                            }
                });
                dialog.inputRangeRes(6, 60, R.color.md_red_500).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .input("Password", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something

                    }
                }).show();
                break;

            case R.id.profile_github:
                dialog.title("Change your github account");
                loadDialog(dialog,"github");
                break;

            case R.id.profile_googleplay:
                dialog.title("Change your google play developer account");
                loadDialog(dialog,"google-play-developer");
                break;
            case R.id.profile_appstore:
                // do your code
                dialog.title("Change your app store account");
                loadDialog(dialog,"appstore-developer");
                break;
            case R.id.profile_linkedin:
                // do your code
                dialog.title("Change your linkedin account");
                loadDialog(dialog,"linkedin");
                break;
            case R.id.profile_instagram:
                // do your code
                dialog.title("Change your instagram account");
                loadDialog(dialog,"instagram");
                break;
            case R.id.profile_mail:
                // do your code
                dialog.title("Your Mail");
                dialog.content(mAuth.getCurrentUser().getEmail().toString());
                dialog.contentGravity(GravityEnum.CENTER);
                dialog.onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                    }
                }).show();
                break;
            case R.id.profile_skype:
                // do your code
                dialog.title("Change your skype number");
                loadDialog(dialog,"skype");
                break;
            case R.id.profile_slack:
                // do your code
                dialog.title("Change your slack account");
                loadDialog(dialog,"slack");
                break;
            case R.id.profile_snapchat:
                // do your code
                dialog.title("Change your snapchat account");
                loadDialog(dialog,"snapchat");
                break;
            case R.id.profile_twitter:
                // do your code
                dialog.title("Change your twitter account");
                loadDialog(dialog,"twitter");
                break;
            case R.id.profile_website:
                // do your code
                dialog.title("Change your website");
                loadDialog(dialog,"website");
                break;
            case R.id.profile_whattsapp:
                // do your code
                dialog.title("Change your whatsapp number");
                loadDialog(dialog,"whatsapp");
                break;
            case R.id.profile_youtube:
                // do your code
                dialog.title("Change your youtube account");
                loadDialog(dialog,"youtube");

                break;
            case R.id.profile_facebook:
                // do your code
                dialog.title("Change your facebook account");
                loadDialog(dialog,"facebook");
                break;
            default:
                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            File file=new File(getRealPathFromURI(ProfileActivity.this,data.getData()));
            long size=file.length();
            //image boyutu 3 mb'dan az olmalı
            if(file.length()<10 * 1024 * 1024) {

                try {
                    //getting image from gallery
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    //Setting image to ImageView
                    userProfilePhoto.setImageBitmap(bitmap);


                    pd.show();

                   StorageReference childRef = storageRef.child("images/profiles/" + mAuth.getCurrentUser().getUid().toString());

                    //uploading the image
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] theData = baos.toByteArray();
                                        UploadTask uploadTask = childRef.putBytes(theData);

                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                pd.dismiss();
                                                Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(ProfileActivity.this, "Upload Failed -> " + e + "/n Please try again", Toast.LENGTH_SHORT).show();
                                            }
                                        });



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(ProfileActivity.this, "The image should be less than 10mb", Toast.LENGTH_SHORT).show();
            }
        }
        }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private void addingData(String where,String data){
        final String wheref=where;

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").child(wheref).setValue(data);
        mDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(ProfileActivity.this, "Successful",
                        Toast.LENGTH_SHORT).show();
            }

            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(ProfileActivity.this, "Adding data failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void loadDialog(final MaterialDialog.Builder dialog,final String where){
        if(where.equals("skype")||where.equals("whatsapp")){
            dialog.inputRangeRes(0, 18, R.color.md_red_500).inputType(InputType.TYPE_CLASS_PHONE);
        }else if(where.equals("name-surname")){
            dialog.inputRangeRes(0, 30, R.color.md_red_500).inputType(InputType.TYPE_CLASS_TEXT);
        }else if(where.equals("status")){
            dialog.inputRangeRes(0, 70, R.color.md_red_500).inputType(InputType.TYPE_CLASS_TEXT);
        }
        else{
            dialog.inputRangeRes(0, 39, R.color.md_red_500).inputType(InputType.TYPE_CLASS_TEXT);
        }
        dialog.input(where, "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(final MaterialDialog dialog, CharSequence input) {

                    }
                });
       final ValueEventListener vl =new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.hasChild(where)){
                    dialog.show().getInputEditText().setText(snapshot.child(where).getValue().toString());
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").removeEventListener(this);
                }else{
                    dialog.show();
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").removeEventListener(this);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.show();

            }
        };
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").addValueEventListener(vl);
        dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                // TODO
                if(!where.equals("skype")&&!where.equals("whatsapp")&&!where.equals("website")&&dialog.getInputEditText().getText().toString().startsWith("http")||dialog.getInputEditText().getText().toString().startsWith("https")||dialog.getInputEditText().getText().toString().startsWith("www")||dialog.getInputEditText().getText().toString().startsWith(".")||dialog.getInputEditText().getText().toString().endsWith(".com")){
                    dialog.getInputEditText().setError("Please enter only username");
                }else if(where.equals("website")&&!dialog.getInputEditText().getText().toString().startsWith("http://")&&!dialog.getInputEditText().getText().toString().startsWith("https://")){
                    dialog.getInputEditText().setError("Please enter your website like that http://www.example.com");
                    }
                else{

                    addingData(where,dialog.getInputEditText().getText().toString().trim());
                }

            }
        }).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                // TODO
            }
        });
    }

    public void loadDataOnFirebase(){

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {



                if (snapshot.hasChild("name-surname") && mAuth != null) {
                    member.nameSurname = snapshot.child("name-surname").getValue(String.class);
                    nameSurname.setText(member.nameSurname);
                } else {
                    member.nameSurname = "Computer Society Member";
                }
                if (snapshot.hasChild("status") && mAuth != null) {
                    member.status = snapshot.child("status").getValue(String.class);
                    status.setText(member.status);
                } else {
                    member.status = "-";
                }
                if(snapshot.hasChild("last-online-date")){
                    Long val = snapshot.child("last-online-date").getValue(Long.class);
                    Date date=new Date(val);
                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm");
                    String dateText = df2.format(date);
                    member.last_login=dateText;
                    member.uid=snapshot.getKey().toString();
                }else{
                    member.last_login="-";
                    member.uid=snapshot.getKey().toString();
                }
                if (snapshot.hasChild("github") && mAuth != null) {
                    String git = snapshot.child("github").getValue(String.class);
                    member.github = git;
                } else {
                    member.github = null;
                }
                if (snapshot.hasChild("google-play-developer") && mAuth != null) {
                    String googlePlay = snapshot.child("google-play-developer").getValue(String.class);
                    member.googlePlay = googlePlay;
                } else {
                    member.googlePlay = null;
                }
                if (snapshot.hasChild("appstore-developer") && mAuth != null) {
                    String appStore = snapshot.child("appstore-developer").getValue(String.class);
                    member.appStore = appStore;
                } else {
                    member.appStore = null;
                }
                if (snapshot.hasChild("skype") && mAuth != null) {
                    String skype = snapshot.child("skype").getValue(String.class);
                    member.skype = skype;
                } else {
                    member.skype = null;
                }
                if (snapshot.hasChild("slack") && mAuth != null) {
                    String slack = snapshot.child("slack").getValue(String.class);
                    member.slack = slack;
                } else {
                    member.slack = null;
                }
                if (snapshot.hasChild("snapchat") && mAuth != null) {
                    String snapchat = snapshot.child("snapchat").getValue(String.class);
                    member.snap = snapchat;
                } else {
                    member.snap = null;
                }
                if (snapshot.hasChild("twitter") && mAuth != null) {
                    String twitter = snapshot.child("twitter").getValue(String.class);
                    member.twitter = twitter;
                } else {
                    member.twitter = null;
                }
                if (snapshot.hasChild("facebook") && mAuth != null) {
                    String facebook = snapshot.child("facebook").getValue(String.class);
                    member.facebook = facebook;
                } else {
                    member.facebook = null;
                }
                if (snapshot.hasChild("whatsapp") && mAuth != null) {
                    String whatsapp = snapshot.child("whatsapp").getValue(String.class);
                    member.whatsapp = whatsapp;
                } else {
                    member.whatsapp = null;
                }
                if (snapshot.hasChild("youtube") && mAuth != null) {
                    String youtube = snapshot.child("youtube").getValue(String.class);
                    member.youtube = youtube;
                } else {
                    member.youtube = null;
                }
                if (snapshot.hasChild("linkedin") && mAuth != null) {
                    String linkedin = snapshot.child("linkedin").getValue(String.class);
                    member.linkedin = linkedin;
                } else {
                    member.linkedin = null;
                }
                if (snapshot.hasChild("website") && mAuth != null) {
                    String web = snapshot.child("website").getValue(String.class);
                    member.website = web;
                } else {
                    member.website = null;
                }
                if (snapshot.hasChild("online")) {
                    Boolean val = snapshot.child("online").getValue(Boolean.class);
                    if (snapshot.getKey() == mAuth.getCurrentUser().getUid()) {
                        member.online = true;
                    } else {
                        member.online = val;
                    }

                }
                StorageReference image = storageRef.child("images/profiles/" + mAuth.getCurrentUser().getUid().toString());
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).centerCrop().into(userProfilePhoto);
                        member.imgSrc = uri.toString();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        member.imgSrc = null;
                        Glide.with(getApplicationContext()).load(R.drawable.ic_user).centerCrop().into(userProfilePhoto);
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(mAuth!=null){
                    Toast.makeText(ProfileActivity.this, "There is a problem/n Please try again", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    }
