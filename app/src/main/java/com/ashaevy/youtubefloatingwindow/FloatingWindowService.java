package com.ashaevy.youtubefloatingwindow;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class FloatingWindowService extends Service {

    WindowManager wm;
    LinearLayout ll;

    FakeYouTubeBaseActivity fakeYouTubeBaseActivity;
    YouTubePlayerView youTubePlayerView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        ll = new LinearLayout(this);
        ll.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams layoutParameteres = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1000);
        ll.setBackgroundColor(Color.argb(66,255,0,0));
        ll.setLayoutParams(layoutParameteres);
        ll.setOrientation(LinearLayout.VERTICAL);

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                800, 1200, WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        parameters.gravity = Gravity.CENTER | Gravity.CENTER;
        parameters.x = 0;
        parameters.y = 0;

      Button     stop = new Button(this);
    stop.setText("Stop");
        ViewGroup.LayoutParams btnParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        stop.setLayoutParams(btnParameters);

        ll.addView(stop);

        Button addPlayer = new Button(this);
        addPlayer.setText("Add player");
        addPlayer.setLayoutParams(btnParameters);
        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (fakeYouTubeBaseActivity != null) {
                        return;
                    }

                    fakeYouTubeBaseActivity = new FakeYouTubeBaseActivity();
                    fakeYouTubeBaseActivity.init(FloatingWindowService.this, wm);

                    youTubePlayerView = new YouTubePlayerView(fakeYouTubeBaseActivity);
                    youTubePlayerView.initialize(getString(R.string.you_tube_access_id), new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                            youTubePlayer.cueVideo("fhWaJi1Hsfo");
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                            Log.e(FloatingWindowService.class.getSimpleName(), "Can't initialize YouTubePlayerView.");
                        }
                    });
                    youTubePlayerView.setLayoutParams(new LinearLayout.LayoutParams(800, 500));

                    ll.addView(youTubePlayerView);

                    fakeYouTubeBaseActivity.start();

                } catch (Exception e) {
                    Log.e(FloatingWindowService.class.getSimpleName(), "Can't setup YouTubeActivity: ", e);
                    fakeYouTubeBaseActivity = null;
                    youTubePlayerView = null;
                }
            }
        });

        ll.addView(addPlayer);

        Button removePlayer = new Button(this);
        removePlayer.setText("Remove player");
        removePlayer.setLayoutParams(btnParameters);
        removePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fakeYouTubeBaseActivity != null) {
                    ll.removeView(youTubePlayerView);
                    youTubePlayerView = null;
                    try {
                        fakeYouTubeBaseActivity.stop();
                        fakeYouTubeBaseActivity.destroy();
                    } catch (Exception e) {
                        Log.e(FloatingWindowService.class.getSimpleName(), "Can't stop YouTubeActivity: ", e);
                    } finally {
                        fakeYouTubeBaseActivity = null;
                    }
                }
            }
        });

        ll.addView(removePlayer);

        wm.addView(ll, parameters);

        IBinder applicationWindowToken = ll.getApplicationWindowToken();
        Log.i(FloatingWindowService.class.getSimpleName(), "Application token of ll: " + applicationWindowToken);

        ll.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatedParameters = parameters;
            double x;
            double y;
            double pressedX;
            double pressedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        pressedX = event.getRawX();
                        pressedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - pressedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - pressedY));

                        wm.updateViewLayout(ll, updatedParameters);

                    default:
                        break;
                }

                return false;
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(ll);
                stopSelf();
                System.exit(0);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

}