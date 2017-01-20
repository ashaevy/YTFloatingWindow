package com.ashaevy.youtubefloatingwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class MainActivity extends YouTubeBaseActivity {

    public static final String YOUTUBE_FRAGMENT_TAG = YouTubePlayerFragment.class.getSimpleName();
    public static final String VIDEO_ID = "video_id";
    public static final String TIMESTAMP = "timestamp";

    public static final String DEFAULT_VIDEO = "fhWaJi1Hsfo";

    public static final String KEY_VIDEO_TIMESTAMP = "KEY_VIDEO_TIMESTAMP";
    public static final String OVERLAY_TYPE = "overlay_type";
    public static final int OT_VIEW = 0;
    public static final int OT_FRAGMENT = 1;

    Button b;

    private static YouTubePlayerFragment floatingYouTubePlayerFragment;

    private YouTubePlayer youTubePlayer;

    private String currentVideoId;
    private int currentVideoTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FloatingWindowService.
                YT_ACTIVITY_CREATED));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent serviceIntent = new Intent(MainActivity.this, FloatingWindowService.class);
                if (arg0.getId() == R.id.btn_view) {
                    serviceIntent.putExtra(OVERLAY_TYPE, OT_VIEW);
                } else if (arg0.getId() == R.id.btn_fragment) {
                    serviceIntent.putExtra(OVERLAY_TYPE, OT_FRAGMENT);
                }
                startService(serviceIntent);
            }
        };
        findViewById(R.id.btn_view).setOnClickListener(onClickListener);
        findViewById(R.id.btn_fragment).setOnClickListener(onClickListener);

        currentVideoId = DEFAULT_VIDEO;
    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FloatingWindowService.
                YT_ACTIVITY_STARTED));
        super.onStart();

        YouTubePlayerFragment retainedFragment = ((YouTubePlayerFragment) getFragmentManager().
                findFragmentByTag(YOUTUBE_FRAGMENT_TAG));

        if (retainedFragment != null) {
            getFragmentManager().beginTransaction().remove(retainedFragment).commit();
        }

        floatingYouTubePlayerFragment = new YouTubePlayerFragment();
        floatingYouTubePlayerFragment.setRetainInstance(true);

        getFragmentManager().beginTransaction().add(R.id.youtube_fragment_layout,
                floatingYouTubePlayerFragment, YOUTUBE_FRAGMENT_TAG).commit();

        floatingYouTubePlayerFragment.initialize(getString(R.string.you_tube_access_id), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                final YouTubePlayer youTubePlayer, boolean b) {
                MainActivity.this.youTubePlayer = youTubePlayer;
                youTubePlayer.cueVideo(currentVideoId);
                youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {
                        if (currentVideoTimestamp != 0) {
                            youTubePlayer.seekToMillis(currentVideoTimestamp);
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

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e(FloatingWindowService.class.getSimpleName(), "Can't initialize YouTubePlayerView.");
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            int timeStamp = savedInstanceState.getInt(KEY_VIDEO_TIMESTAMP);
            if (timeStamp != 0) {
                currentVideoTimestamp = timeStamp;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        if (youTubePlayer != null) {
            currentVideoTimestamp = youTubePlayer.getCurrentTimeMillis();
            bundle.putInt(KEY_VIDEO_TIMESTAMP, currentVideoTimestamp);
        }
    }

    @Override
    protected void onStop() {
        Intent intent = new Intent(FloatingWindowService.
                YT_ACTIVITY_STOPPED);
        if (currentVideoId != null) {
            intent.putExtra(VIDEO_ID, currentVideoId);
            if (youTubePlayer != null) {
                intent.putExtra(TIMESTAMP, youTubePlayer.getCurrentTimeMillis());
            }
        }
        youTubePlayer = null;
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FloatingWindowService.
                YT_ACTIVITY_DESTROYED));
        super.onDestroy();
        floatingYouTubePlayerFragment = null;
    }

    public static YouTubePlayerFragment getYouTubePlayerFragment() {
        return floatingYouTubePlayerFragment;
    }

}