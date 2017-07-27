package com.example.oguz.topluluk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Oguz on 26-Jul-17.
 */

public class InnerActivity  extends AppCompatActivity {
    private EditText editTxt;
    private Button done;
    private Button cancel;
    Toolbar mActionBarToolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_inner);
        Bundle extras = getIntent().getExtras();
        editTxt=(EditText)findViewById(R.id.input_information);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbarInner);
        cancel=(Button)findViewById(R.id.btn_cancel);
        done =(Button)findViewById(R.id.btn_done);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        editTxt.setBackgroundColor(Color.parseColor("#E0E0E0"));
        if (extras!=null){
            if (((String)extras.get("which")).equals("change")){
                editTxt.setHint("******");
                mActionBarToolbar.setTitle("Change your password");
                editTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editTxt.length()>6&&editTxt.length()<15){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(editTxt.getText().toString().trim())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(InnerActivity.this, "Password is updated!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(InnerActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(InnerActivity.this, "Your password should be min. 6 max. 15 characters.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
            else if(((String)extras.get("which")).equals("nameSurname")){
                editTxt.setHint("Name Surname");
                mActionBarToolbar.setTitle("Change your name and surname");
            }
        }


    }
}
