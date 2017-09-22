package com.example.oguz.bilgisayarToplulugu;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
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
import com.vansuita.materialabout.builder.AboutBuilder;

import java.io.File;

import gun0912.tedbottompicker.TedBottomPicker;

/**
 * Created by Oguz on 19-Jul-17.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private ImageButton profilePhoto;
    private ImageButton nameSurname;
    private ImageButton status;
    private Integer[] settingImg={R.id.profile_password,R.id.profile_mail,R.id.profile_github,R.id.profile_googleplay,R.id.profile_appstore,R.id.profile_website,R.id.profile_whattsapp,R.id.profile_slack,R.id.profile_linkedin,R.id.profile_skype,R.id.profile_facebook,R.id.profile_instagram,R.id.profile_snapchat,R.id.profile_twitter,R.id.profile_youtube};
    private ImageView[] imageViews;
    private FirebaseStorage myStorage;
    private DatabaseReference mDatabase;
    private ImageView userProfilePhoto;
    private ImageButton selfAbout;
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
        selfAbout=(ImageButton)findViewById(R.id.self_about);
        profilePhoto=(ImageButton)findViewById(R.id.edit_profileImage);
        nameSurname=(ImageButton)findViewById(R.id.edit_namesurname);
        status=(ImageButton)findViewById(R.id.edit_status);
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
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"),111);*/
                TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(ProfileActivity.this)
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                filePath = uri;
                                File file=new File(getRealPathFromURI(ProfileActivity.this,uri));
                                long size=file.length();
                                //image boyutu 3 mb'dan az olmalı
                                if(file.length()<3 * 1024 * 1024) {

                                    try {
                                        //getting image from gallery
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                                        //Setting image to ImageView
                                        userProfilePhoto.setImageBitmap(bitmap);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (filePath != null) {
                                        pd.show();

                                        StorageReference childRef = storageRef.child("images/profiles/" + mAuth.getCurrentUser().getUid());

                                        //uploading the image
                                        UploadTask uploadTask = childRef.putFile(filePath);

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
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Select an image or less than 3mb", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(ProfileActivity.this, "The image should be less than 3mb", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .create();

                tedBottomPicker.show(getSupportFragmentManager());
            }
        });
        selfAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
                final ProgressDialog dialog = new ProgressDialog(ProfileActivity.this);
                dialog.setMessage("Loading");
                dialog.show();
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        final AboutBuilder ab = AboutBuilder.with(ProfileActivity.this) .setLinksAnimated(true)
                                .setShowAsCard(false).addFiveStarsAction().setWrapScrollView(true).setAppName(R.string.app_name).addShareAction(R.string.app_name);
                        ab.setCover(R.drawable.profilebackground);
                        if(snapshot.hasChild("name-surname")){
                            ab.setName(snapshot.child("name-surname").getValue().toString());
                        }
                        if(snapshot.hasChild("status")){
                            ab.setSubTitle(snapshot.child("status").getValue().toString());
                        }
                        if(snapshot.hasChild("github")&&!snapshot.child("github").getValue().toString().trim().isEmpty()){
                            ab.addGitHubLink(snapshot.child("github").getValue().toString());
                        }
                        if(snapshot.hasChild("website")&&!snapshot.child("website").getValue().toString().trim().isEmpty()){
                            ab.addWebsiteLink(snapshot.child("website").getValue().toString());
                        }
                        if(snapshot.hasChild("linkedin")&&!snapshot.child("linkedin").getValue().toString().trim().isEmpty()){
                            ab.addLinkedInLink(snapshot.child("linkedin").getValue().toString());
                        }
                        StorageReference image = storageRef.child("images/profiles/"+mAuth.getCurrentUser().getUid());

                        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide
                                        .with(getApplication().getApplicationContext())
                                        .load(uri)
                                        .asBitmap()
                                        .into(new SimpleTarget<Bitmap>(96, 96) {
                                            @Override
                                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                                // Do something with bitmap here.
                                                ab.setPhoto(bitmap);
                                                View view=ab.build();
                                                if(!((Activity) ProfileActivity.this).isFinishing())
                                                {
                                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
                                                    dialogBuilder.setView(view);
                                                    dialogBuilder.show();
                                                }

                                                dialog.dismiss();

                                            }
                                        });
                            }}).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                ab.setPhoto(R.mipmap.logo);
                                View view=ab.build();
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
                                dialogBuilder.setView(view);
                                dialogBuilder.show();
                                dialog.dismiss();
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
            File file=new File(getRealPathFromURI(this,data.getData()));
            long size=file.length();
            //image boyutu 3 mb'dan az olmalı
            if(file.length()<3 * 1024 * 1024) {

                try {
                    //getting image from gallery
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    //Setting image to ImageView
                    userProfilePhoto.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (filePath != null) {
                    pd.show();

                    StorageReference childRef = storageRef.child("images/profiles/" + mAuth.getCurrentUser().getUid());

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

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
                } else {
                    Toast.makeText(ProfileActivity.this, "Select an image or less than 3mb", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(ProfileActivity.this, "The image should be less than 3mb", Toast.LENGTH_SHORT).show();
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
                if(snapshot.hasChild("name-surname")){
                    ((TextView)findViewById(R.id.user_profile_name)).setText(snapshot.child("name-surname").getValue().toString());
                }
                if(snapshot.hasChild("status")){
                    ((TextView)findViewById(R.id.user_profile_short_bio)).setText(snapshot.child("status").getValue().toString());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                if(mAuth!=null){
                    Toast.makeText(ProfileActivity.this, "There is a problem/n Please try again", Toast.LENGTH_SHORT).show();
                }

            }
        });
        StorageReference image = storageRef.child("images/profiles/"+mAuth.getCurrentUser().getUid());

        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileActivity.this).load(uri).centerCrop().into(userProfilePhoto);
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Glide.with(ProfileActivity.this).load(R.drawable.ic_user).centerCrop().into(userProfilePhoto);
            }
        });

    }
    }
