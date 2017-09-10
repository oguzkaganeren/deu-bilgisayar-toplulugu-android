package com.example.oguz.bilgisayarToplulugu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Oguz on 26-Jul-17.
 */

public class InnerActivity  extends AppCompatActivity {
    //bu activity profile kısmındaki ayarların birine girdiğinde gözükür ve ayara göre içeriği değişir.
    private EditText editTxt;
    private Button done;
    private Button cancel;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private Toolbar mActionBarToolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_inner);
        //koşulu extras ile sağlayacağız
        Bundle extras = getIntent().getExtras();
        user = FirebaseAuth.getInstance().getCurrentUser();
        editTxt=(EditText)findViewById(R.id.input_information);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbarInner);
        cancel=(Button)findViewById(R.id.btn_cancel);
        mDatabase= FirebaseDatabase.getInstance().getReference();
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
                        if(editTxt.length()>6&&editTxt.length()<50){
                            user.updatePassword(editTxt.getText().toString().trim())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(InnerActivity.this, "Password is updated!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(InnerActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{
                            editTxt.setError("Your password should be min. 6 max. 50 characters.");
                        }

                    }
                });


            }
            else if(((String)extras.get("which")).equals("nameSurname")){
                editTxt.setHint("Name Surname");
                mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.hasChild("name-surname")){
                            editTxt.setHint(snapshot.child("name-surname").getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });
                mActionBarToolbar.setTitle("Change your name and surname");
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTxt.getText().toString().trim().length() > 1 && editTxt.getText().toString().trim().length() < 30) {
                            addingData("name-surname");
                        } else {
                            editTxt.setError("Your name and surname should be min. 1 max. 30 characters.");
                        }
                    }
                });
        }else if(((String)extras.get("which")).equals("status")){
                editTxt.setHint("Your status");
                mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.hasChild("status")){
                            editTxt.setHint(snapshot.child("status").getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });
                mActionBarToolbar.setTitle("Change your status");
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTxt.getText().toString().trim().length() > 6 && editTxt.getText().toString().trim().length() < 75) {
                            addingData("status");
                        } else {
                            editTxt.setError("Your github address should be min. 6 max. 75 characters.");
                        }
                    }
                });
        }else if(((String)extras.get("which")).equals("github")) {
                editTxt.setHint("Your github username");
                mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.hasChild("github")){
                            editTxt.setHint(snapshot.child("github").getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });
                mActionBarToolbar.setTitle("Change your github id");
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTxt.getText().toString().trim().length() > 6 && editTxt.getText().toString().trim().length() < 50) {
                            if(editTxt.getText().toString().startsWith("http")||editTxt.getText().toString().startsWith("https")||editTxt.getText().toString().startsWith("www")||editTxt.getText().toString().startsWith(".")||editTxt.getText().toString().endsWith(".com")){
                                editTxt.setError("Please enter only github username");
                            }
                            else {
                                addingData("github");
                            }

                        } else {
                            editTxt.setError("Your github address should be min. 6 max. 80 characters.");
                        }
                    }
                });
        }else if(((String)extras.get("which")).equals("linkedin")) {
                editTxt.setHint("Your linkedin address");
                mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.hasChild("linkedin")){
                            editTxt.setHint(snapshot.child("linkedin").getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });
                mActionBarToolbar.setTitle("Change your linkedin username");
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTxt.getText().toString().trim().length() > 6 && editTxt.getText().toString().trim().length() < 50) {
                            if(editTxt.getText().toString().startsWith("http")||editTxt.getText().toString().startsWith("https")||editTxt.getText().toString().startsWith("www")||editTxt.getText().toString().startsWith(".")||editTxt.getText().toString().endsWith(".com")){
                                editTxt.setError("Please enter only linkedin username");
                            }else{
                                addingData("linkedin");
                            }


                        } else {
                            editTxt.setError("Your linkedin address should be min. 6 max. 80 characters.");
                        }
                    }
                });
        }else if(((String)extras.get("which")).equals("website")) {
                editTxt.setHint("Your website address");
                mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.hasChild("website")){
                            editTxt.setHint(snapshot.child("website").getValue().toString());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });
                mActionBarToolbar.setTitle("Change your website address");
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTxt.getText().toString().trim().length() > 6 && editTxt.getText().toString().trim().length() < 35) {
                            if(editTxt.getText().toString().startsWith("http://")||editTxt.getText().toString().startsWith("https://")){
                                addingData("website");
                            }else {
                                editTxt.setError("Please enter your website like that http://www.example.com");

                            }
                        } else {
                            editTxt.setError( "Your website address should be min. 6 max. 80 characters.");
                        }
                    }
                });
            }



    }
}
    private void addingData(String where){
        final String wheref=where;

                    mDatabase.child("users").child(user.getUid()).child(wheref).setValue(editTxt.getText().toString().trim());
                    mDatabase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            finish();
                        }

                        public void onCancelled(DatabaseError firebaseError) {
                            Toast.makeText(InnerActivity.this, "Adding data failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            }
    }

