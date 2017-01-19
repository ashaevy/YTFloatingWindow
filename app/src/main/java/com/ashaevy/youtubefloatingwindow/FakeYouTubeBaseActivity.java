package com.ashaevy.youtubefloatingwindow;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.InputQueue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Class that mocks YouTubeBaseActivity to allow use of YouTubePlayerView outside
 * activities and fragments. For example inside WindowManager.addView().
 * Based on classes from YouTube API 1.2.2.
 */
public class FakeYouTubeBaseActivity extends YouTubeBaseActivity {

    public FakeYouTubeBaseActivity() {

    }

    public void init(Context context, WindowManager wm) throws Exception {
        emulateAttachContext(context);
        setWindowManager(wm);
        FakeYouTubeWindow fakeWindow = new FakeYouTubeWindow(context);
        setWindow(fakeWindow);
        setComponentName();
        emulateOnCreate();
    }

    public void start() throws Exception {
        emulateOnStart();
        emulateOnResume();
    }

    public void stop() throws Exception {
        emulateOnPause();
        emulateOnStop();
    }

    public void destroy() throws Exception {
        emulateOnDestroy(true);
    }

    private void emulateAttachContext(Context context) throws Exception {
        // setup Context of YouTubeBaseActivity
        Method attachBaseContextMethod = ContextThemeWrapper.class.getDeclaredMethod("attachBaseContext", Context.class);
        attachBaseContextMethod.setAccessible(true);
        attachBaseContextMethod.invoke(this, context);
    }

    private void setWindowManager(WindowManager wm) throws Exception {
        // set mWindowManager
        Field mWindowManagerField = Activity.class.getDeclaredField("mWindowManager");
        mWindowManagerField.setAccessible(true);
        mWindowManagerField.set(this, wm);
    }

    private void setWindow(FakeYouTubeWindow fakeWindow) throws Exception {
        // set mWindow
        Field mWindowField = Activity.class.getDeclaredField("mWindow");
        mWindowField.setAccessible(true);
        mWindowField.set(this, fakeWindow);
    }

    private void setComponentName() throws Exception {
        Field mComponentField = Activity.class.getDeclaredField("mComponent");
        mComponentField.setAccessible(true);
        mComponentField.set(this, new ComponentName(FloatingWindowService.class.getCanonicalName(), getPackageName()));
    }

    private void emulateOnCreate() throws Exception {
        // this.a = new YouTubeBaseActivity.a((byte)0);
        Class<?> innerClassA = Class.forName("com.google.android.youtube.player.YouTubeBaseActivity$a");
        Constructor<?> innerClassADeclaredByteConstructor = innerClassA.getDeclaredConstructor(YouTubeBaseActivity.class, Byte.TYPE);
        innerClassADeclaredByteConstructor.setAccessible(true);

        Object innerClassAInstance = innerClassADeclaredByteConstructor.newInstance(this, (byte)0);

        Field internalFieldA = YouTubeBaseActivity.class.getDeclaredField("a");
        internalFieldA.setAccessible(true);
        internalFieldA.set(this, innerClassAInstance);

        // this.d = var1 != null?var1.getBundle("YouTubeBaseActivity.KEY_PLAYER_VIEW_STATE"):null;
        // bundle - saved state
        Bundle bundle = null;
        Field internalFieldD = YouTubeBaseActivity.class.getDeclaredField("d");
        internalFieldD.setAccessible(true);
        internalFieldD.set(this, bundle);
    }

    private void emulateOnStart() throws Exception {
        // this.c = 1;

        //if(this.b != null) {
        //    this.b.a();
        //}

        Field internalFieldB = YouTubeBaseActivity.class.getDeclaredField("b");
        internalFieldB.setAccessible(true);
        Object internalFieldBValue = internalFieldB.get(this);
        if (internalFieldBValue != null) {
            YouTubePlayerView youTubePlayerViewFromFieldB = YouTubePlayerView.class.cast(internalFieldBValue);
            Method methodA = YouTubePlayerView.class.getDeclaredMethod("a");
            methodA.setAccessible(true);
            methodA.invoke(youTubePlayerViewFromFieldB);
        }
    }

    public void emulateOnResume() throws Exception {
        //this.c = 2;
        Field internalFieldC = YouTubeBaseActivity.class.getDeclaredField("c");
        internalFieldC.setAccessible(true);
        internalFieldC.set(this, 2);

        //if(this.b != null) {
        //    this.b.b();
        //}

        Field internalFieldB = YouTubeBaseActivity.class.getDeclaredField("b");
        internalFieldB.setAccessible(true);
        Object internalFieldBValue = internalFieldB.get(this);
        if (internalFieldBValue != null) {
            YouTubePlayerView youTubePlayerViewFromFieldB = YouTubePlayerView.class.cast(internalFieldBValue);
            Method methodB = YouTubePlayerView.class.getDeclaredMethod("b");
            methodB.setAccessible(true);
            methodB.invoke(youTubePlayerViewFromFieldB);
        }

    }

    public void emulateOnPause() throws Exception {
//        this.c = 1;
        Field internalFieldC = YouTubeBaseActivity.class.getDeclaredField("c");
        internalFieldC.setAccessible(true);
        internalFieldC.set(this, 1);
//        if(this.b != null) {
//            this.b.c();
//        }
        Field internalFieldB = YouTubeBaseActivity.class.getDeclaredField("b");
        internalFieldB.setAccessible(true);
        Object internalFieldBValue = internalFieldB.get(this);
        if (internalFieldBValue != null) {
            YouTubePlayerView youTubePlayerViewFromFieldB = YouTubePlayerView.class.cast(internalFieldBValue);
            Method methodC = YouTubePlayerView.class.getDeclaredMethod("c");
            methodC.setAccessible(true);
            methodC.invoke(youTubePlayerViewFromFieldB);
        }
    }

    public void emulateOnSaveInstanceState(Bundle var1) {
//            Bundle var2 = this.b != null?this.b.e():this.d;
//            var1.putBundle("YouTubeBaseActivity.KEY_PLAYER_VIEW_STATE", var2);
        throw new IllegalStateException("Not implemented!");
    }

    public void emulateOnStop() throws Exception {
//                    this.c = 0;
        Field internalFieldC = YouTubeBaseActivity.class.getDeclaredField("c");
        internalFieldC.setAccessible(true);
        internalFieldC.set(this, 0);
//                    if(this.b != null) {
//                        this.b.d();
//                    }
        Field internalFieldB = YouTubeBaseActivity.class.getDeclaredField("b");
        internalFieldB.setAccessible(true);
        Object internalFieldBValue = internalFieldB.get(this);
        if (internalFieldBValue != null) {
            YouTubePlayerView youTubePlayerViewFromFieldB = YouTubePlayerView.class.cast(internalFieldBValue);
            Method methodD = YouTubePlayerView.class.getDeclaredMethod("d");
            methodD.setAccessible(true);
            methodD.invoke(youTubePlayerViewFromFieldB);
        }
    }

    public void emulateOnDestroy(boolean isFinishing) throws Exception {
//                    if(this.b != null) {
//                        this.b.b(this.isFinishing());
//                    }
        Field internalFieldB = YouTubeBaseActivity.class.getDeclaredField("b");
        internalFieldB.setAccessible(true);
        Object internalFieldBValue = internalFieldB.get(this);
        if (internalFieldBValue != null) {
            YouTubePlayerView youTubePlayerViewFromFieldB = YouTubePlayerView.class.cast(internalFieldBValue);
            Method methodB = YouTubePlayerView.class.getDeclaredMethod("b", Boolean.TYPE);
            methodB.setAccessible(true);
            methodB.invoke(youTubePlayerViewFromFieldB, isFinishing);
        }
    }

    public static class FakeYouTubeWindow extends Window {

        public FakeYouTubeWindow(Context context) {
            super(context);
        }

        @Override
        public void takeSurface(SurfaceHolder.Callback2 callback) {

        }

        @Override
        public void takeInputQueue(InputQueue.Callback callback) {

        }

        @Override
        public boolean isFloating() {
            return false;
        }

        @Override
        public void setContentView(int layoutResID) {

        }

        @Override
        public void setContentView(View view) {

        }

        @Override
        public void setContentView(View view, ViewGroup.LayoutParams params) {

        }

        @Override
        public void addContentView(View view, ViewGroup.LayoutParams params) {

        }

        @Override
        public View getCurrentFocus() {
            return null;
        }

        @Override
        public LayoutInflater getLayoutInflater() {
            return null;
        }

        @Override
        public void setTitle(CharSequence title) {

        }

        @Override
        public void setTitleColor(int textColor) {

        }

        @Override
        public void openPanel(int featureId, KeyEvent event) {

        }

        @Override
        public void closePanel(int featureId) {

        }

        @Override
        public void togglePanel(int featureId, KeyEvent event) {

        }

        @Override
        public void invalidatePanelMenu(int featureId) {

        }

        @Override
        public boolean performPanelShortcut(int featureId, int keyCode, KeyEvent event, int flags) {
            return false;
        }

        @Override
        public boolean performPanelIdentifierAction(int featureId, int id, int flags) {
            return false;
        }

        @Override
        public void closeAllPanels() {

        }

        @Override
        public boolean performContextMenuIdentifierAction(int id, int flags) {
            return false;
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {

        }

        @Override
        public void setBackgroundDrawable(Drawable drawable) {

        }

        @Override
        public void setFeatureDrawableResource(int featureId, int resId) {

        }

        @Override
        public void setFeatureDrawableUri(int featureId, Uri uri) {

        }

        @Override
        public void setFeatureDrawable(int featureId, Drawable drawable) {

        }

        @Override
        public void setFeatureDrawableAlpha(int featureId, int alpha) {

        }

        @Override
        public void setFeatureInt(int featureId, int value) {

        }

        @Override
        public void takeKeyEvents(boolean get) {

        }

        @Override
        public boolean superDispatchKeyEvent(KeyEvent event) {
            return false;
        }

        @Override
        public boolean superDispatchKeyShortcutEvent(KeyEvent event) {
            return false;
        }

        @Override
        public boolean superDispatchTouchEvent(MotionEvent event) {
            return false;
        }

        @Override
        public boolean superDispatchTrackballEvent(MotionEvent event) {
            return false;
        }

        @Override
        public boolean superDispatchGenericMotionEvent(MotionEvent event) {
            return false;
        }

        @Override
        public View getDecorView() {
            return null;
        }

        @Override
        public View peekDecorView() {
            return null;
        }

        @Override
        public Bundle saveHierarchyState() {
            return null;
        }

        @Override
        public void restoreHierarchyState(Bundle savedInstanceState) {

        }

        @Override
        protected void onActive() {

        }

        @Override
        public void setChildDrawable(int featureId, Drawable drawable) {

        }

        @Override
        public void setChildInt(int featureId, int value) {

        }

        @Override
        public boolean isShortcutKey(int keyCode, KeyEvent event) {
            return false;
        }

        @Override
        public void setVolumeControlStream(int streamType) {

        }

        @Override
        public int getVolumeControlStream() {
            return 0;
        }

        @Override
        public int getStatusBarColor() {
            return 0;
        }

        @Override
        public void setStatusBarColor(int color) {

        }

        @Override
        public int getNavigationBarColor() {
            return 0;
        }

        @Override
        public void setNavigationBarColor(int color) {

        }
    }
}
