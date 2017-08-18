package com.example.oguz.topluluk;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Oguz on 17-Aug-17.
 */

public class OurFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.about_24dp)
                .setTicker("D.E.U Bilgisayar Topluluğu")
                .setContentTitle("D.E.U Notification")
                .setContentText(remoteMessage.getNotification().getBody())
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");
        SharedPreferences sharedPref = getSharedPreferences("notificationDEU", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        String notif = sharedPref.getString("notificationDEU", "");
        Date currentTime = Calendar.getInstance().getTime();
        if(!notif.equalsIgnoreCase(""))
        {
            notif = notif + remoteMessage.getNotification().getBody()+"é"+currentTime.toString()+"~";
            editor.putString("notificationDEU",notif);
        }else{
            editor.putString("notificationDEU", remoteMessage.getNotification().getBody()+"é"+currentTime.toString()+"~");
        }
        editor.commit();

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
        Log.d(TAG,sharedPref.getString("notificationDEU","").toString());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }
}
