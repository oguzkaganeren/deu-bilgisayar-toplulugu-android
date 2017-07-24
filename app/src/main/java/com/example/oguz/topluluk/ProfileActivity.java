package com.example.oguz.topluluk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

/**
 * Created by Oguz on 19-Jul-17.
 */

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private ImageButton changePass;
    private ImageButton profilePhoto;
    private ImageButton nameSurname;
    private ImageButton status;
    private ImageButton gitAdr;
    private ImageButton linkedinAdr;
    private ImageButton websiteAdr;
    private ImageButton signOut;
    private ImageView userProfilePhoto;
    private Uri filePath;
    private ProgressDialog pd;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage myStorage=FirebaseStorage.getInstance();
        storageRef= myStorage.getReference();
        if (mAuth.getCurrentUser() == null) {
            finish();
        }
        profilePhoto=(ImageButton)findViewById(R.id.edit_profileImage);
        changePass=(ImageButton)findViewById(R.id.edit_password);
        nameSurname=(ImageButton)findViewById(R.id.edit_namesurname);
        status=(ImageButton)findViewById(R.id.edit_status);
        gitAdr=(ImageButton)findViewById(R.id.edit_github);
        linkedinAdr=(ImageButton)findViewById(R.id.edit_linkedin);
        websiteAdr=(ImageButton)findViewById(R.id.edit_website);
        signOut=(ImageButton)findViewById(R.id.signOut);
        userProfilePhoto=(ImageView)findViewById(R.id.user_profile_photo);
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(ProfileActivity.this, "Signout successful", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"),111);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting image to ImageView
                userProfilePhoto.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(filePath != null) {
                pd.show();

                StorageReference childRef = storageRef.child("images/profiles"+mAuth.getCurrentUser().getUid());

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
                        Toast.makeText(ProfileActivity.this, "Upload Failed -> " + e+"/n Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(ProfileActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
            }
        }
        }
    }
