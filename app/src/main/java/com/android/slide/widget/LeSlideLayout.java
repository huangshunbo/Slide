package com.android.slide.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class LeSlideLayout extends FrameLayout {
    private String TAG = "LeSlideLayout";

    private static final int MIN_FLING_VELOCITY = 400; // dips per second
    private int mScreenWidth;

    private View mDimView;
    private View mDecorView;
    private LeViewDragHelper mDragHelper;

    private OnPanelSlideListener mListener;
    private boolean mIsLocked = false;
    private boolean mIsEdgeTouched = false;

    private float mShadowsHeight;
    private int mEdgePosition;
    private LeSlideConfig mConfig;
    private View mBgActivityRootView;
    private Field mViewField;
    private Field mRootField;
    private Field mGlobalField;
    private Field mIdentField;
    private Method mGetViewRootImpl;
    private float mParallaxOffset;
    private boolean mIsInitialize;
    private boolean mEnableSlideToOpen;

    /**
     * The panel sliding interface that gets called
     * whenever the panel is closed or opened
     */
    public interface OnPanelSlideListener {
        void onStateChanged(int state);

        void onClosed();

        void onOpened();

        void onSlideChange(float percent);
    }

    public LeSlideLayout(Context context, View decorView) {
        this(context, decorView, new LeSlideConfig.Builder().build());
    }

    public LeSlideLayout(Context context, View decorView, LeSlideConfig config) {
        super(context);
        mDecorView = decorView;
        mConfig = config;
        init();
    }

    /**
     * Set the panel slide listener that gets called based on slider changes
     *
     * @param listener
     */
    public void setOnPanelSlideListener(OnPanelSlideListener listener) {
        mListener = listener;
    }

    /**
     * Initialize the slider panel
     */
    private void init() {
        TypedValue outValue = new TypedValue();
        final Resources.Theme theme = getContext().getTheme();
        if (theme.resolveAttribute(android.R.attr.colorBackground, outValue, true)) {
            int colorBackground = outValue.data;
            mDecorView.setBackgroundColor(colorBackground);
        }

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        final float density = getResources().getDisplayMetrics().density;
        final float minVel = MIN_FLING_VELOCITY * density;

        mShadowsHeight = LeSlideConfig.SLIDE_DEFAULT_SHADOW_Z * density;
//        mParallaxOffset = mConfig.getParallaxOffset() * density;
        mParallaxOffset = mScreenWidth >> 1;

        LeViewDragHelper.Callback callback = mLeftCallback;
        mEdgePosition = LeViewDragHelper.EDGE_LEFT;


        mDragHelper = LeViewDragHelper.create(this, mConfig.getSensitivity(), callback);
        mDragHelper.setMinVelocity(minVel);
        mDragHelper.setEdgeTrackingEnabled(mEdgePosition);

        setMotionEventSplittingEnabled(false);

        // Setup the dimmer view
        mDimView = new View(getContext());

        // Add the dimmer view to the layout
        addView(mDimView);

        post(new Runnable() {
            @Override
            public void run() {

                if (!mIsInitialize) {
                    mIsInitialize = true;
                    if (mEnableSlideToOpen) {
                        slideToOpen();
                    }

                }
            }
        });
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        super.dispatchRestoreInstanceState(container);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean interceptForDrag;

        if (mIsLocked) {
            return false;
        }

        if (mConfig.isEdgeOnly()) {
            mIsEdgeTouched = canDragFromEdge(ev);
        }

        try {
            interceptForDrag = mDragHelper.shouldInterceptTouchEvent(ev);
        } catch (Exception e) {
            interceptForDrag = false;
        }

        return interceptForDrag && !mIsLocked;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsLocked) {
            return false;
        }

        try {
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int oldLeft = 0;
        if (isSliding() && mDecorView != null) {
            oldLeft = mDecorView.getLeft();
        }
        super.onLayout(changed, left, top, right, bottom);
        if (isSliding() && mDecorView != null) {
            int offset = oldLeft - mDecorView.getLeft();
            if (offset > 0) {
                mDecorView.offsetLeftAndRight(offset);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation();
        }
    }

    /**
     * Lock this sliding panel to ignore touch inputs.
     */
    public void lock() {
        mIsLocked = true;
        mDragHelper.abort();
    }

    /**
     * Unlock this sliding panel to listen to touch inputs.
     */
    public void unlock() {
        mIsLocked = false;
        mDragHelper.abort();
    }

    private boolean canDragFromEdge(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        return x < mConfig.getEdgeSize(getWidth());
    }

    public void onBackPressed() {
        int settleLeft = mScreenWidth;
        mDragHelper.settleViewAt(mDecorView, settleLeft, getTop(), mConfig.getSlideDuration());
        invalidate();
    }

    public boolean isSliding() {
        return mDragHelper.getViewDragState() != LeViewDragHelper.STATE_IDLE;
    }

    public void setEnableSlideToOpen(boolean b) {
        mEnableSlideToOpen = b;
    }

    public void slideToOpen() {
        mBgActivityRootView = getBelowRootView();

        if (mBgActivityRootView != null) {
            if (mDimView != null) mDimView.setX(0);
            mDecorView.setTranslationZ(mShadowsHeight);
        }
        mDragHelper.slideView(mDecorView, mScreenWidth, 0, mConfig.getSlideDuration());
    }

    /**
     * The drag helper callback interface for the Left position
     */
    private LeViewDragHelper.Callback mLeftCallback = new LeViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            boolean edgeCase = !mConfig.isEdgeOnly() || mDragHelper.isEdgeTouched(mEdgePosition, pointerId) || mIsEdgeTouched;
            return child == mDecorView && edgeCase;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return clamp(left, 0, mScreenWidth);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mScreenWidth;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            int left = releasedChild.getLeft();
            int settleLeft = 0;
            int leftThreshold = (int) (getWidth() * mConfig.getDistanceThreshold());
            boolean isVerticalSwiping = Math.abs(yvel) > mConfig.getVelocityThreshold();

            if (xvel > 0) {
                if (Math.abs(xvel) > mConfig.getVelocityThreshold() && !isVerticalSwiping) {
                    settleLeft = mScreenWidth;
                } else if (left > leftThreshold) {
                    settleLeft = mScreenWidth;
                }

            } else if (xvel == 0) {
                if (left > leftThreshold) {
                    settleLeft = mScreenWidth;
                }
            }

            mDragHelper.settleCapturedViewAt(settleLeft, releasedChild.getTop());
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            float percent = 1f - ((float) left / (float) mScreenWidth);
            if (mListener != null) mListener.onSlideChange(percent);
            if (mBgActivityRootView != null) {
//                mBgActivityRootView.setX(-mParallaxOffset * percent);
                mDimView.setX(-mParallaxOffset * percent);
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mListener != null) mListener.onStateChanged(state);

            switch (state) {
                case LeViewDragHelper.STATE_IDLE:
                    mDecorView.setTranslationZ(0);
                    if (mDecorView.getLeft() == 0) {
                        // State Open
                        if (mListener != null) mListener.onOpened();

                        if (mBgActivityRootView != null) {
//                            mBgActivityRootView.setX(0);
                            mDimView.setX(0);
                            mBgActivityRootView = null;
                        }
                        recyleDimBg();
                    } else {
                        // State Closed
                        if (mListener != null) mListener.onClosed();
                    }
                    break;
                case LeViewDragHelper.STATE_DRAGGING:
                    mBgActivityRootView = getBelowRootView();
                    if (mBgActivityRootView != null) {
//                        mBgActivityRootView.setX(-mParallaxOffset);
                        boolean result = setDimBackground();
                        if (result) {
                            mDimView.setX(-mParallaxOffset);
                            mDecorView.setTranslationZ(mShadowsHeight);
                        } else {
                            mBgActivityRootView = null;
                            mDragHelper.abort();
                        }
                    } else {
                        mDragHelper.abort();
                    }
                    break;
                case LeViewDragHelper.STATE_SETTLING:
                    break;
            }
        }

    };

    /**
     * Method to get the rootView of previous activity.
     */
    private View getBelowRootView() {
        View decorView = null;

        try {
            if (mGlobalField == null) {
                mGlobalField = Class.forName("android.view.WindowManagerImpl").getDeclaredField("mGlobal");
                mGlobalField.setAccessible(true);
            }

            if (mRootField == null) {
                mRootField = Class.forName("android.view.WindowManagerGlobal").getDeclaredField("mRoots");
                mRootField.setAccessible(true);
            }

            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Object global = mGlobalField.get(wm);
            ArrayList roots = (ArrayList) mRootField.get(global);

            if (roots.size() > 1) {
                if (mViewField == null) {
                    mViewField = Class.forName("android.view.ViewRootImpl").getDeclaredField("mView");
                    mViewField.setAccessible(true);
                }
                if (mIdentField == null) {
                    //TODO: according to the source code, mIndent is add customly, so
                    // pay attention to using it.
                    mIdentField = Class.forName("android.view.ViewRootImpl").getDeclaredField("mIdent");
                    mIdentField.setAccessible(true);
                }
                Object view = roots.get(roots.size() - 2);
                decorView = (View) mViewField.get(view);
                long indent = (long) mIdentField.get(view);


                if (mGetViewRootImpl == null) {
                    mGetViewRootImpl = View.class.getDeclaredMethod("getViewRootImpl");
                    mGetViewRootImpl.setAccessible(true);
                }
                Object currentViewRootImpl = mGetViewRootImpl.invoke(this);
                final long currentIndent = (long) mIdentField.get(currentViewRootImpl);
                if (view != null && decorView != null && (isDiscardView(decorView) || (currentIndent <= indent))) {
                    for (int i = roots.size() - 1; i >= 0 && (isDiscardView(decorView) || (currentIndent <= indent)); i--) {
                        view = roots.get(i);
                        decorView = (View) mViewField.get(view);
                        indent = (long) mIdentField.get(view);
                    }
                    if ((isDiscardView(decorView) || (currentIndent <= indent))) {
                        decorView = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decorView;
    }

    private boolean isDiscardView(View decorView) {
        return (decorView.getContext() == getContext()) || !(decorView.getContext() instanceof Activity)
                || !((decorView.getWidth() == getWidth() && decorView.getHeight() == getHeight()) ||
                (decorView.getHeight() == getWidth() && decorView.getWidth() == getHeight()));
    }

    private boolean setDimBackground() {
        if (mBgActivityRootView == null) return false;

        if (getHeight() != mBgActivityRootView.getHeight()) {
            int width = getWidth();
            int height = getHeight();
            mScreenWidth = getResources().getDisplayMetrics().widthPixels;
            mParallaxOffset = mScreenWidth >> 1;

            mBgActivityRootView.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            mBgActivityRootView.layout(0, 0, width, height);
        }
        if (mBgActivityRootView.getMeasuredWidth() <= 0 || mBgActivityRootView.getMeasuredHeight() <= 0) {
            return false;
        }

        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(mBgActivityRootView.getMeasuredWidth(),
                    mBgActivityRootView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError error) {
            // If there is not enough memory to create the bitmap cache, just
            // ignore the issue as bitmap caches are not required to draw the
            // view hierarchy
            // refer to View.buildDrawingCacheImpl's resolve.
        }
        if (bitmap != null) {
            Canvas canvas = new Canvas(bitmap);
            mBgActivityRootView.draw(canvas);
            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            mDimView.setBackground(drawable);
            return true;
        }
        return false;
    }

    @Override
    public void dispatchWindowVisibilityChanged(int visibility) {
        super.dispatchWindowVisibilityChanged(visibility);
        if (visibility != View.VISIBLE) {
            recyleDimBg();
        }
    }

    private void recyleDimBg() {
        if (mDimView == null) return;
        Drawable d = mDimView.getBackground();
        if (d instanceof BitmapDrawable) {
            mDimView.setBackground(null);
            try {
                ((BitmapDrawable) d).getBitmap().recycle();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Clamp Integer values to a given range
     *
     * @param value the value to clamp
     * @param min   the minimum value
     * @param max   the maximum value
     * @return the clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public void setInitializeState() {
        mIsInitialize = true;
    }
}
