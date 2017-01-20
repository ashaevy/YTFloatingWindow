package com.ashaevy.youtubefloatingwindow;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;

public class FloatingWindowService extends Service {

    public static final String YT_ACTIVITY_CREATED = "YT_ACTIVITY_CREATED";
    public static final String YT_ACTIVITY_STARTED = "YT_ACTIVITY_STARTED";
    public static final String YT_ACTIVITY_STOPPED = "YT_ACTIVITY_STOPPED";
    public static final String YT_ACTIVITY_DESTROYED = "YT_ACTIVITY_DESTROYED";

    private WindowManager wm;
    private LinearLayout ll;

    private FakeYouTubeBaseActivity fakeYouTubeBaseActivity;
    private YouTubePlayerView youTubePlayerView;

    private View youTubeFragmentView;
    private YouTubePlayerFragment youTubePlayerFragment;

    private int overlayType = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        if (YT_ACTIVITY_STOPPED.equals(intent.getAction())) {
            addPlayer(intent);
        } else if (YT_ACTIVITY_CREATED.equals(intent.getAction()) ||
                YT_ACTIVITY_STARTED.equals(intent.getAction()) ||
                YT_ACTIVITY_DESTROYED.equals(intent.getAction())) {
            removePlayer();
        }
        }
    };

    private void removePlayer() {
        if (overlayType == MainActivity.OT_VIEW) {
            removeYTView();
        } else {
            removeYTFragment();
        }
    }

    private void addPlayer(Intent intent) {
        if (overlayType == MainActivity.OT_VIEW) {
            addYTView();
        } else {
            addYTFragment(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        overlayType = intent.getIntExtra(MainActivity.OVERLAY_TYPE, -1);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        ll = new LinearLayout(this) {
            WindowManager.LayoutParams updatedParameters = parameters;
            double x;
            double y;
            double pressedX;
            double pressedY;

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {

                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        pressedX = ev.getRawX();
                        pressedY = ev.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (ev.getRawX() - pressedX));
                        updatedParameters.y = (int) (y + (ev.getRawY() - pressedY));

                        wm.updateViewLayout(ll, updatedParameters);

                    default:
                        break;
                }

                return false;

            }
        };
        ll.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams layoutParameteres = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1000);
        ll.setBackgroundColor(Color.argb(66,255,0,0));
        ll.setLayoutParams(layoutParameteres);
        ll.setOrientation(LinearLayout.VERTICAL);

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
                Intent intent = new Intent();
                intent.putExtra(MainActivity.VIDEO_ID, MainActivity.DEFAULT_VIDEO);
                addPlayer(intent);
            }
        });

        ll.addView(addPlayer);

        Button removePlayer = new Button(this);
        removePlayer.setText("Remove player");
        removePlayer.setLayoutParams(btnParameters);
        removePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlayer();
            }
        });

        ll.addView(removePlayer);

        wm.addView(ll, parameters);

        IBinder applicationWindowToken = ll.getApplicationWindowToken();
        Log.i(FloatingWindowService.class.getSimpleName(), "Application token of ll: " + applicationWindowToken);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wm.removeView(ll);
                stopSelf();
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(YT_ACTIVITY_CREATED);
        intentFilter.addAction(YT_ACTIVITY_STARTED);
        intentFilter.addAction(YT_ACTIVITY_STOPPED);
        intentFilter.addAction(YT_ACTIVITY_DESTROYED);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void removeYTView() {
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

    private void addYTView() {
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

    private void removeYTFragment() {
        if (youTubePlayerFragment != null) {
            ll.removeView(youTubeFragmentView);
            youTubeFragmentView = null;
            youTubePlayerFragment = null;
        }
    }

    private void addYTFragment(final Intent intent) {
        if (youTubePlayerFragment != null) {
            return;
        }

        youTubePlayerFragment = MainActivity.getYouTubePlayerFragment();
        if (youTubePlayerFragment != null) {
            youTubePlayerFragment.onCreate(null);
            youTubeFragmentView = youTubePlayerFragment.onCreateView(null, null, null);
            ll.addView(youTubeFragmentView);

            youTubePlayerFragment.initialize(getString(R.string.you_tube_access_id), new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                    final YouTubePlayer youTubePlayer, boolean b) {
                    String videoId = intent.getStringExtra(MainActivity.VIDEO_ID);
                    if (videoId != null) {
                        youTubePlayer.cueVideo(videoId);
                        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                            @Override
                            public void onLoading() {

                            }

                            @Override
                            public void onLoaded(String s) {
                                final int time = intent.getIntExtra(MainActivity.TIMESTAMP, -1);
                                if (time != -1) {
                                    youTubePlayer.seekToMillis(time);
                                    youTubePlayer.play();
                                }
                            }

                            @Override
                            public void onAdStarted() {

                            }

                            @Override
                            public void onVideoStarted() {

                            }

                            @Override
                            public void onVideoEnded() {

                            }

                            @Override
                            public void onError(YouTubePlayer.ErrorReason errorReason) {

                            }
                        });
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Log.e(FloatingWindowService.class.getSimpleName(), "Can't initialize YouTubePlayerView.");
                }
            });
            youTubePlayerFragment.onStart();
            youTubePlayerFragment.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        stopSelf();
    }

}