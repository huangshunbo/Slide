package com.android.slide.widget;

public class LeSlideConfig {

    public static final int SLIDE_LEFT = 0;
    public static final int SLIDE_DURATION = 500;
    public static final int SLIDE_DEFAULT_SHADOW_Z = 16;
    public static final float SLIDE_DISTANCE_THRESHOLD = 0.25f;

    private int colorPrimary = -1;
    private int colorSecondary = -1;
    private float touchSize = -1f;
    private float sensitivity = 1f;
    private float velocityThreshold = 5f;
    private float distanceThreshold = SLIDE_DISTANCE_THRESHOLD;
    private boolean edgeOnly = false;
    private float edgeSize = 0.18f;
    private int parallaxOffset = 100;
    private int slideDuration = SLIDE_DURATION;

    private int position = SLIDE_LEFT;
    private LeSlideListener listener;

    /**
     * Hidden Constructor
     * Use the builder pattern
     */
    private LeSlideConfig(){}

    /**
     * @return      the primary status bar color
     */
    public int getPrimaryColor(){
        return colorPrimary;
    }

    /**
     * Get the secondary color that the slider will interpolatel That is the color of the Activity
     * that you are making slidable
     *
     * @return      the secondary status bar color
     */
    public int getSecondaryColor(){
        return colorSecondary;
    }


    /**
     * Get the touch 'width' to be used in the gesture detection. This value should incorporate with
     * the device's touch slop
     *
     * @return      the touch area size
     */
    public float getTouchSize(){
        return touchSize;
    }

    /**
     * Get the velocity threshold at which the slide action is completed regardless of offset
     * distance of the drag
     *
     * @return      the velocity threshold
     */
    public float getVelocityThreshold(){
        return velocityThreshold;
    }

    /**
     * Get at what % of the screen is the minimum viable distance the activity has to be dragged
     * in-order to be slinged off the screen
     *
     * @return      the distant threshold as a percentage of the screen size (width or height)
     */
    public float getDistanceThreshold(){
        return distanceThreshold;
    }

    /**
     * Get the touch sensitivity set in the {@link android.support.v4.widget.ViewDragHelper} when
     * creating it.
     *
     * @return      the touch sensitivity
     */
    public float getSensitivity(){
        return sensitivity;
    }

    /**
     * Get the slidr listener set by the user to respond to certain events in the sliding
     * mechanism.
     *
     * @return      the slidr listener
     */
    public LeSlideListener getListener(){
        return listener;
    }

    /**
     * Return whether or not the set status bar colors are valid
     * @return
     */
    public boolean areStatusBarColorsValid(){
        return colorPrimary != -1 && colorSecondary != -1;
    }

    /**
     * Has the user configured slidr to only catch at the edge of the screen ?
     *
     * @return      true if is edge capture only
     */
    public boolean isEdgeOnly() {
        return edgeOnly;
    }

    /**
     * Get the size of the edge field that is catchable
     *
     * @see #isEdgeOnly()
     * @return      the size of the edge that is grabable
     */
    public float getEdgeSize(float size) {
        return edgeSize * size;
    }

    public int getParallaxOffset() {
        return parallaxOffset;
    }

    public int getSlideDuration() {
        return slideDuration;
    }

    /**
     * The Builder for this configuration class. This is the only way to create a
     * configuration
     */
    public static class Builder{

        private LeSlideConfig config;

        public Builder(){
            config = new LeSlideConfig();
        }

        public Builder primaryColor( int color){
            config.colorPrimary = color;
            return this;
        }

        public Builder secondaryColor( int color){
            config.colorSecondary = color;
            return this;
        }

        public Builder touchSize(float size){
            config.touchSize = size;
            return this;
        }

        public Builder sensitivity(float sensitivity){
            config.sensitivity = sensitivity;
            return this;
        }


        public Builder velocityThreshold(float threshold){
            config.velocityThreshold = threshold;
            return this;
        }

        public Builder distanceThreshold(float threshold){
            config.distanceThreshold = threshold;
            return this;
        }

        public Builder edge(boolean flag){
            config.edgeOnly = flag;
            return this;
        }

        public Builder edgeSize(float edgeSize){
            config.edgeSize = edgeSize;
            return this;
        }

        /**
         * value in dp.
         */
        public Builder parallaxOffset(int offset) {
            config.parallaxOffset = offset;
            return this;
        }

        public Builder slideDuration(int duration) {
            config.slideDuration = duration;
            return this;
        }

        public Builder listener(LeSlideListener listener){
            config.listener = listener;
            return this;
        }

        public LeSlideConfig build(){
            return config;
        }

    }

}
