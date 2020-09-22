package com.example.stickbug.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stickbug.Background.Background;
import com.example.stickbug.R;
import com.example.stickbug.Savedata.Savedata;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private int repeats = 0;
    boolean appOpen = false;
    TextView textView;
    Savedata savedata;
    MediaPlayer mediaPlayer;
    Background background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }





    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String check = bundle.getString(Background.REQUEST);
                int Repeats = bundle.getInt(Background.REPEATS);
                repeats = Repeats;
                savedata.saveRepeats(repeats);
                if (appOpen) {
                    textView.setText("x" + repeats);
                }
                if (check != null && check.equals(Background.STOPPLZ)) {
                    System.exit(0);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        appOpen = true;
        textView.setText("x" + repeats);
    }
    @Override
    protected void onPause() {
        super.onPause();
        appOpen = false;
    }




    @Override
    @TargetApi(26)
    protected void onStart() {
        super.onStart();
        appOpen = true;
        registerReceiver(receiver, new IntentFilter(Background.PACKAGE));
        savedata = new Savedata(new File(this.getFilesDir(), "yes.prop"));
        repeats = savedata.getRepeats();
        textView = findViewById(R.id.times);
        textView.setText("x" + repeats);
        Intent intent = new Intent(this, Background.class);
        intent.putExtra(Background.REQUEST, Background.TOTAL_REPEATS);
        intent.putExtra(Background.REPEATS, repeats);
        startForegroundService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        appOpen = false;
        unregisterReceiver(receiver);
    }
}
