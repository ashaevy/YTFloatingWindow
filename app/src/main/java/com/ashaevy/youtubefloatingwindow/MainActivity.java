package com.ashaevy.youtubefloatingwindow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity {

    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        b = (Button) findViewById(R.id.btn);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            startService(new Intent(MainActivity.this,FloatingWindowService.class));
            onBackPressed();
            }
        });

    }

}