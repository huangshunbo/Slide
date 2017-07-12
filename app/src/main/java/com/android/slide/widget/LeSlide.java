package com.android.slide.widget;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


public class LeSlide {

    public static LeSlideInterface attach(Activity activity){
        return attach(activity, new LeSlideConfig.Builder().build());
    }

    /**
     * Attach a slide to an activity based on the passed config
     *
     * @param activity      the activity to attach the slider to
     * @param config        the slider configuration to make
     * @return              a interface that allows
     *                      the user to lock/unlock the sliding for whatever purpose.
     */
    public static LeSlideInterface attach(final Activity activity, final LeSlideConfig config){

        // Hack to get the decorview
        ViewGroup decorView = (ViewGroup)activity.getWindow().getDecorView();

        View oldScreen = decorView.getChildAt(0);
        decorView.removeViewAt(0);

        // Setup the slider panel and attach it to the decor
        final LeSlideLayout panel = new LeSlideLayout(activity, oldScreen, config);
        panel.addView(oldScreen);
        decorView.addView(panel, 0);

        // Set the panel slide listener for when it becomes closed or opened
        panel.setOnPanelSlideListener(new LeSlideLayout.OnPanelSlideListener() {

            private final ArgbEvaluator mEvaluator = new ArgbEvaluator();

            @Override
            public void onStateChanged(int state) {
                if(config.getListener() != null){
                    config.getListener().onSlideStateChanged(state);
                }
            }

            @Override
            public void onClosed() {
                if(config.getListener() != null){
                    config.getListener().onSlideClosed();
                }

                activity.finish();
                activity.overridePendingTransition(0, 0);
            }

            @Override
            public void onOpened() {
                if(config.getListener() != null){
                    config.getListener().onSlideOpened();
                }
            }

            @Override
            public void onSlideChange(float percent) {
                // Interpolate the statusbar color
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                        config.areStatusBarColorsValid()){

                    int newColor = (int) mEvaluator.evaluate(percent, config.getPrimaryColor(),
                            config.getSecondaryColor());

                    activity.getWindow().setStatusBarColor(newColor);
                }

                if(config.getListener() != null){
                    config.getListener().onSlideChange(percent);
                }
            }
        });

        // Setup the lock interface
        LeSlideInterface slideInterface = new LeSlideInterface() {
            @Override
            public void lock() {
                panel.lock();
            }

            @Override
            public void unlock() {
                panel.unlock();
            }

            @Override
            public void onBackPressed() {
                panel.onBackPressed();
            }

            @Override
            public void onRestoreInstanceState(Bundle savedInstanceState) {
                if (savedInstanceState != null) {
                    panel.setInitializeState();
                }
            }

            @Override
            public void setEnableSlideToOpen(boolean b) {
                panel.setEnableSlideToOpen(b);
            }

            @Override
            public boolean isSliding() {
                return panel.isSliding();
            }
        };

        // Return the lock interface
        return slideInterface;

    }

}
