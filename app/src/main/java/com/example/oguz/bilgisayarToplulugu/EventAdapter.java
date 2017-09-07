package com.example.oguz.bilgisayarToplulugu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vansuita.materialabout.builder.AboutBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oguz on 29-Aug-17.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventsViewHolder>{
    private Context context;
    private List<EventsInfo> dataList;
    public EventAdapter(Context context, List<EventsInfo> dataList) {
        this.context=context;
        this.dataList = dataList;
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(final EventAdapter.EventsViewHolder eventsViewHolder, int i) {
       final EventsInfo wb = dataList.get(i);
        eventsViewHolder.address.setText(wb.address);
        eventsViewHolder.title.setText(wb.title);
        eventsViewHolder.description.setText(wb.description);

        String cDate=wb.date.toString();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault());
        try {
            format.setLenient(false);
            Date date = format.parse(cDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" +         cal.get(Calendar.YEAR);
            eventsViewHolder.date.setText(formatedDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            eventsViewHolder.date.setText(wb.date.toString());
        }
        Glide.with(context)
                .load(R.drawable.event)
                .centerCrop()
                .into(eventsViewHolder.image);
        eventsViewHolder.theCard.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String[] loc=wb.location.split("-");
                Uri gmmIntentUri = Uri.parse("geo:<" + loc[0]  + ">,<" + loc[1] + ">?q=<" + loc[0]  + ">,<" + loc[1] + ">(" + wb.title + ")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                v.getContext().startActivity(mapIntent);


            }
        });
        StorageReference storageRef=FirebaseStorage.getInstance().getReference();
        StorageReference image = storageRef.child("images/profiles/"+wb.uid);

        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).centerCrop().into(eventsViewHolder.userImage);
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Glide.with(context).load(R.drawable.ic_user).centerCrop().into(eventsViewHolder.userImage);
            }
        });

        eventsViewHolder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final AboutBuilder ab = AboutBuilder.with(context) .setLinksAnimated(true)
                        .setShowAsCard(false).addFiveStarsAction().setWrapScrollView(true).setAppName(R.string.app_name).addShareAction(R.string.app_name);
                ab.setCover(R.drawable.profilebackground);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    final FirebaseStorage myStorage=FirebaseStorage.getInstance();
                    final StorageReference storageRef= myStorage.getReference();
                    mDatabase.child("users").child(wb.uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("name-surname")) {
                                            String nameSurname = dataSnapshot.child("name-surname").getValue(String.class);
                                            ab.setName(nameSurname);
                                        } else {
                                            ab.setName("Computer Society Member");
                                        }
                                        if (dataSnapshot.hasChild("status")) {
                                            String status = dataSnapshot.child("status").getValue(String.class);
                                            ab.setSubTitle(status);
                                        } else {
                                            ab.setSubTitle("-");
                                        }
                                        if(dataSnapshot.hasChild("last-online-date")){
                                            Long val = dataSnapshot.child("last-online-date").getValue(Long.class);
                                            Date date=new Date(val);
                                            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy HH:mm");
                                            String dateText = df2.format(date);
                                            ab.setBrief("Last seen: "+dateText);
                                        }
                                        if (dataSnapshot.hasChild("github")) {
                                            String git = dataSnapshot.child("github").getValue(String.class);
                                            ab.addGitHubLink(git);
                                        }
                                        if (dataSnapshot.hasChild("linkedin")) {
                                            String linkedin = dataSnapshot.child("linkedin").getValue(String.class);
                                            ab.addLinkedInLink(linkedin);
                                        }
                                        if (dataSnapshot.hasChild("website")) {
                                            String web = dataSnapshot.child("website").getValue(String.class);
                                            ab.addWebsiteLink(web);
                                        }
                                        StorageReference image = storageRef.child("images/profiles/" + dataSnapshot.getKey().toString());
                                        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide
                                                        .with(context)
                                                        .load(uri.toString())
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
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                ab.setPhoto(R.mipmap.logo);
                                                View view=ab.build();
                                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                                dialogBuilder.setView(view);
                                                dialogBuilder.show();
                                            }
                                        });

                                }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Toast.makeText(getActivity(), "Firebase problem", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth  mAuth = FirebaseAuth.getInstance();
        eventsViewHolder.join.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v) {
                final EventRegister reg = new EventRegister();

               // reg.changedTime=ServerValue.TIMESTAMP;
               // mDatabase.child("eventregister").child(wb.getEventKey()).child(mAuth.getCurrentUser().getUid()).setValue(reg);
                ValueEventListener registerListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Is better to use a List, because you don't know the size
                        // of the iterator returned by dataSnapshot.getChildren() to
                        // initialize the array

                                if (mAuth.getCurrentUser() != null) {
                                    if (dataSnapshot.hasChild("register")) {
                                        Boolean register = dataSnapshot.child("register").getValue(Boolean.class);

                                        //-----
                                        if(register){
                                            reg.register=false;
                                            v.setBackgroundColor(context.getResources().getColor(R.color.green));
                                            eventsViewHolder.join.setText("Join");

                                        }else
                                        {
                                            reg.register=true;
                                            v.setBackgroundColor(context.getResources().getColor(R.color.red));
                                            eventsViewHolder.join.setText("Leave");
                                        }
                                    }else{
                                        reg.register=true;
                                        v.setBackgroundColor(context.getResources().getColor(R.color.red));
                                        eventsViewHolder.join.setText("Leave");
                                    }
                                    reg.changedTime=ServerValue.TIMESTAMP;
                                    mDatabase.child("eventregister").child(wb.getEventKey()).child(mAuth.getCurrentUser().getUid()).setValue(reg);
                                }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Toast.makeText(getActivity(), "Firebase problem", Toast.LENGTH_SHORT).show();
                    }
                };
                mDatabase.child("eventregister").child(wb.getEventKey()).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(registerListener);
                mDatabase.child("eventregister").child(wb.getEventKey()).child(mAuth.getCurrentUser().getUid()).removeEventListener(registerListener);
            }
        });
        ValueEventListener getData=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("register")) {
                            Boolean reg = dataSnapshot.child("register").getValue(Boolean.class);
                            Log.d("a", "onDataChange: "+reg);

                            if(reg){
                                eventsViewHolder.join.setText("Leave");
                                eventsViewHolder.join.setBackgroundColor(context.getResources().getColor(R.color.red));
                            }else{
                                eventsViewHolder.join.setText("Join");
                                eventsViewHolder.join.setBackgroundColor(context.getResources().getColor(R.color.green));
                            }
                    }

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Toast.makeText(getActivity(), "Firebase problem", Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.child("eventregister").child(wb.getEventKey()).child(mAuth.getCurrentUser().getUid()).addValueEventListener(getData);
        //bu kısımda bi problem çıkabilir
        //mDatabase.child("eventregister").child(wb.getEventKey()).child(mAuth.getCurrentUser().getUid()).removeEventListener(getData);
        mDatabase.child("eventregister").child(wb.getEventKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                if (dataSnapshot.exists()) {
                    int count=0;
                    for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                            if (memberSnapshot.hasChild("register")) {
                                Boolean reg = memberSnapshot.child("register").getValue(Boolean.class);
                                Log.d("a", "onDataChange: "+reg);

                                if(reg){
                                    count++;
                                    String value;
                                    if(eventsViewHolder.join.getText().toString().startsWith("Join")){
                                       value =eventsViewHolder.join.getText().toString().substring(0,4);
                                    }else{
                                        value=eventsViewHolder.join.getText().toString().substring(0,5);
                                    }

                                    eventsViewHolder.join.setText(value+"("+count+")");
                                }
                            }
                        }

                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Toast.makeText(getActivity(), "Firebase problem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public EventAdapter.EventsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.fragment_event, viewGroup, false);
        return new EventAdapter.EventsViewHolder(itemView);
    }
    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        protected TextView address;
        protected TextView title;
        protected TextView description;
        protected TextView date;
        protected ImageView image;
        protected ImageView userImage;
        protected Button join;
        protected View theCard;
        public EventsViewHolder(View v) {
            super(v);
            address =  (TextView) v.findViewById(R.id.address_event_frag);
            title = (TextView)  v.findViewById(R.id.title_event_frag);
            description = (TextView) v.findViewById(R.id.description_event_frag);
            date = (TextView) v.findViewById(R.id.date_event_frag);
            image=(ImageView)v.findViewById(R.id.thumbnail_event_frag);
            userImage=(ImageView)v.findViewById(R.id.profile_picture_event);
            join=(Button)v.findViewById(R.id.btn_join);
            theCard= v;

        }

    }

}
class EventRegister {

    public Boolean register;
    public Object changedTime;

    public EventRegister() {
    }

    public EventRegister(Boolean register) {
        this.register = register;
        this.changedTime = ServerValue.TIMESTAMP;
    }
}
