package com.android.slide.activity.mainactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class SwipeActivity extends Activity {

    private static final String TAG = "ViewActivity";
    private static final float EDGE_SIZE = 20;
    private GestureDetector mGestureDetector;
    private int mEdgeSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SwipeActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        mGestureDetector = new GestureDetector(this, new LeSlideGestureListener());
        final float density = getResources().getDisplayMetrics().density;
        mEdgeSize = (int) (EDGE_SIZE * density + 0.5f);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mGestureDetector.onTouchEvent(ev)) {

        }
        return super.dispatchTouchEvent(ev);
    }

    public class LeSlideGestureListener implements GestureDetector.OnGestureListener {
        private static final float SLIDE_VELOCITY_THRESHOLD = 400;
        private int mSlideThresHold = -1;

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

            if (mSlideThresHold <= 0) {
                mSlideThresHold = (int) (getWindow().getDecorView().getWidth() * 0.5);
            }
            if (diffX > 0 && e1.getX() < mEdgeSize) {
                onSlideLeft();
                result = true;
            }

            return result;
        }

        public void onSlideLeft() {
            Log.i("SwipeActivity", "Slide Left");
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
