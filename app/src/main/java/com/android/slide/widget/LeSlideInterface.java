package com.android.slide.widget;

import android.os.Bundle;


public interface LeSlideInterface {
    void lock();
    void unlock();
    void onBackPressed();
    void onRestoreInstanceState(Bundle savedInstanceState);
    void setEnableSlideToOpen(boolean b);
    boolean isSliding();
}
