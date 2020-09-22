package com.example.stickbug.Background;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.stickbug.R;
import com.example.stickbug.Savedata.Savedata;

import java.io.File;


public class Background extends Service {

    MediaPlayer mediaPlayer;
    public static final String PACKAGE = "com.example.stickbug.Background";
    public static final String REQUEST = "Request";
    public static final String REPEATS = "Repeats";
    public static final String TOTAL_REPEATS = "Total Repeats";
    public static final String APP_STATE = "App State";
    public static final String APP_OPENED = "Opened";
    public static final String STOPPLZ = "Stop";

    private int repeats;
    private boolean playing = false;
    NotificationManagerCompat managerCompat;

    private void refresh() {
        Savedata savedata = new com.example.stickbug.Savedata.Savedata(new File(this.getFilesDir(), "yes.prop"));
        savedata.saveRepeats(repeats);
    }

    @Override
    public void onCreate() {
        managerCompat = NotificationManagerCompat.from(this);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Background Stick Bug";
            String description = "Plays Stick Bug in the Background :)";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("yes", name, importance);
            channel.setDescription(description);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setSound(null,null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @TargetApi(26)
    public Notification createNotification() {
        Intent notificationIntent = new Intent(this, Background.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent stopIntent = new Intent(this, Background.class).putExtra(REQUEST, STOPPLZ);
        PendingIntent stopPending = PendingIntent.getForegroundService(this, 0, stopIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "yes")
                .setSmallIcon(R.drawable.sb)
                .setContentTitle("You are being Stick Bugged")
                .setContentText("Song has played a total of x" + repeats)
                .setContentIntent(pendingIntent)
                .setTicker("Stick Bug")
                .addAction(R.drawable.stickbug, "MAKE IT STOP", stopPending)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        return builder.build();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle incoming = intent.getExtras();
        String request = "";
        String appState = "";
        try {
            assert incoming != null;
            request = incoming.getString(REQUEST);
            appState = incoming.getString(APP_STATE);

        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            assert request != null;
            if (request.equals(REPEATS)) {
                returnCounter();
            }
            if (request.equals(TOTAL_REPEATS)) {
                repeats = intent.getExtras().getInt(REPEATS, 0);
            }
            if (request.equals(STOPPLZ)) {
                returnCounter(intent);
                //System.exit(0);
                stopSelf();
            }
            assert appState != null;
            if (appState.equals(APP_OPENED)) {
                managerCompat.cancel(0);
            }
            //managerCompat.notify(0, createNotification());
        }catch (NullPointerException e) {
            e.printStackTrace();
            Log.i("Error", "Error was sort of expected.");
        }


        if (!playing) {
            playing = true;
            mediaPlayer = MediaPlayer.create(this, R.raw.sbs);
            MediaPlayer.OnPreparedListener listener = new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    repeats++;
                    returnCounter();
                    startForeground(1, createNotification());
                }
            };
            mediaPlayer.setOnPreparedListener(listener);
            MediaPlayer.OnCompletionListener listener1 = new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    repeats++;
                    mediaPlayer.start();
                    returnCounter();
                    startForeground(1, createNotification());
                }
            };
            mediaPlayer.setOnCompletionListener(listener1);
        }
        startForeground(1, createNotification());
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void returnCounter() {
        refresh();
        Intent intent = new Intent(PACKAGE);
        intent.putExtra(REPEATS, repeats);
        sendBroadcast(intent);
    }
    private void returnCounter(Intent oldIntent) {
        Intent intent = new Intent(PACKAGE);
        intent.replaceExtras(oldIntent);
        intent.putExtra(REPEATS, repeats);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        try {
            mediaPlayer.release();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
