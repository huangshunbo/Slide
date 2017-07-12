package com.android.slide.widget;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class LeSlideTouchListener implements View.OnTouchListener {

    private static final float SLIDE_THRESHOLD = 100;
    private static final float SLIDE_VELOCITY_THRESHOLD = 100;

    public final GestureDetector mGestureDetector;

    public LeSlideTouchListener(Context context) {
        mGestureDetector = new GestureDetector(context, new LeSlideGestureListener());
    }

    public LeSlideTouchListener(Context context, LeSlideGestureListener listener) {
        mGestureDetector = new GestureDetector(context, listener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public class LeSlideGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > SLIDE_THRESHOLD && Math.abs(velocityX) > SLIDE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSlideLeft();
                } else {
                    onSlideRight();
                }
            }

            if (Math.abs(diffY) > SLIDE_THRESHOLD && Math.abs(velocityY) > SLIDE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSlideBottom();
                } else {
                    onSlideTop();
                }
            }
            return result;
        }

        private void onSlideTop() {
        }

        private void onSlideBottom() {
        }

        private void onSlideRight() {
            Log.e("gnodss", "Slide Right");
        }

        public void onSlideLeft() {
            Log.e("gnodss", "Slide Left");
        }
    }
}
