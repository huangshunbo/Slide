package com.android.slide.widget;


public interface LeSlideListener {
    void onSlideStateChanged(int state);

    void onSlideChange(float percent);

    void onSlideOpened();

    void onSlideClosed();

}
