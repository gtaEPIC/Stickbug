package com.example.stickbug.main;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import com.example.stickbug.R;
import com.example.stickbug.savedata.savedata;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private int repeats = 0;
    private com.example.stickbug.savedata.savedata savedata;
    TextView textView;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        savedata = new savedata(new File(this.getFilesDir(), "yes.prop"));
        repeats = savedata.getRepeats();

        textView = findViewById(R.id.times);
        textView.setText("x" + repeats);
        mediaPlayer = MediaPlayer.create(this, R.raw.sbs);
        MediaPlayer.OnPreparedListener listener = new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                repeats++;
                textView.setText("x" + repeats);
            }
        };
        mediaPlayer.setOnPreparedListener(listener);
        MediaPlayer.OnCompletionListener listener1 = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                repeats++;
                textView.setText("x" + repeats);
                mediaPlayer.start();
            }
        };
        mediaPlayer.setOnCompletionListener(listener1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        savedata.saveRepeats(repeats);
        mediaPlayer.release();
    }
}
