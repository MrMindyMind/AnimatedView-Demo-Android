package com.hotmail.maximglukhov.checkmarkviewlib;

import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.hotmail.maximglukhov.animatedviewlib.AnimatedDraw;
import com.hotmail.maximglukhov.animatedviewlib.AnimatedDrawState;
import com.hotmail.maximglukhov.animatedviewlib.AnimatedView;

/**
 * Created by maxim on 04-Mar-17.
 */

public class CheckMarkView extends AnimatedView {

    /**
     * Multiplier for short distance (from center).
     */
    private static final float CHERKMARK_SHORT_MULTIPLIER = 0.3f;
    /**
     * Multiplier for long distance (from center).
     */
    private static final float CHECKMARK_LONG_MULTIPLIER = 0.5f;

    /**
     * Device's screen density. Important for consistent sizing on different displays.
     */
    private float mScreenDensity;

    /**
     * Paint for background drawing.
     */
    private Paint mBackgroundPaint;
    /**
     * Background animation.
     */
    private AnimatedDraw mBackground;

    /**
     * Paint for check mark drawing.
     */
    private Paint mCheckMarkPaint;
    /**
     * Left part of check mark animation.
     */
    private AnimatedDraw mCheckMarkLeft;
    /**
     * Right part of check mark animation.
     */
    private AnimatedDraw mCheckMarkRight;

    /**
     * Available screen width for drawing.
     */
    private int mWidth;
    /**
     * Available screen height for drawing.
     */
    private int mHeight;

    /**
     * Center of available drawing bounds.
     */
    private PointF mCenter = new PointF();
    /**
     * Radius from center of view.
     */
    private float mRadius;

    public CheckMarkView(Context context) {
        super(context);

        init(context);
    }

    public CheckMarkView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CheckMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    @TargetApi(21)
    public CheckMarkView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    /**
     * Start animations for this view.
     */
    public void runAnimations() {
        // Start animating background.
        mBackground.startAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate available width and height.
        mWidth  = w - (getPaddingLeft() + getPaddingRight());
        mHeight = h - (getPaddingBottom() + getPaddingTop());

        // Calculate center and radius.
        mCenter = new PointF(mWidth / 2.0f, mHeight / 2.0f);
        mRadius = Math.min(mWidth, mHeight) / 2.0f;
    }

    @Override
    protected void onDrawStatics(Canvas canvas) {
        // Draw non-animating here.
    }

    /**
     * Initialize this view.
     * @param context Context to initialize for (constructor).
     */
    private void init(Context context) {
        calculateScreenDensity(context);

        initPaints();
        initAnimatedDraws();
    }

    /**
     * Calculate screen density for this device.
     * @param context Context to get {@link WindowManager} instance from.
     */
    private void calculateScreenDensity(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        mScreenDensity = displayMetrics.density;
    }

    /**
     * Create {@link Paint} objects and set their attributes.
     */
    private void initPaints() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.rgb(46, 139, 87));
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);

        mCheckMarkPaint = new Paint();
        mCheckMarkPaint.setColor(Color.WHITE);
        mCheckMarkPaint.setStyle(Paint.Style.STROKE);
        mCheckMarkPaint.setStrokeWidth(2.5f * mScreenDensity);

        mCheckMarkPaint.setAntiAlias(true);
        mCheckMarkPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * Create {@link AnimatedDraw} objects for animations.
     */
    private void initAnimatedDraws() {
        int animTime = 150;

        mBackground = new BackgroundAnimatedDraw(new AccelerateInterpolator(), animTime);

        mCheckMarkLeft = new CheckMarkLeftAnimatedDraw(new AccelerateInterpolator(), animTime/2);
        mCheckMarkRight = new CheckMarkRightAnimatedDraw(new AccelerateInterpolator(), animTime/2);

        addAnimated(mBackground);
        addAnimated(mCheckMarkLeft);
        addAnimated(mCheckMarkRight);
    }

    /**
     * Draws the background of the check mark.
     */
    private class BackgroundAnimatedDraw extends AnimatedDraw {

        public BackgroundAnimatedDraw(TimeInterpolator interpolator, int durationMillis) {
            super(interpolator, durationMillis);
        }

        @Override
        public void onFrame(float interpolatedTime) {
            // Handle every frame according to time.

            if (interpolatedTime == 0.0f) {
                mCheckMarkLeft.resetAnimation();
            }

            if (interpolatedTime == 1.0f) {
                // Start animating check mark first part.
                mCheckMarkLeft.startAnimation();
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            // Draw frame by frame.

            // Calculate radius by interpolated animation value.
            float interpolatedRadius = mRadius * getAnimatedValue();

            canvas.drawCircle(mCenter.x, mCenter.y, interpolatedRadius, mBackgroundPaint);
        }
    }

    /**
     * Draws the left part of the check mark.
     */
    private class CheckMarkLeftAnimatedDraw extends AnimatedDraw {

        private PointF start;
        private PointF end;

        public CheckMarkLeftAnimatedDraw(TimeInterpolator interpolator, int durationMillis) {
            super(interpolator, durationMillis);
        }

        @Override
        public void onFrame(float interpolatedTime) {
            if (interpolatedTime == 0.0f) {
                if (mCheckMarkRight != null) {
                    mCheckMarkRight.resetAnimation();
                }

                start = new PointF(mCenter.x - CHECKMARK_LONG_MULTIPLIER * mRadius,
                        mCenter.y + CHERKMARK_SHORT_MULTIPLIER * mRadius);
                end = new PointF(mCenter.x, mCenter.y + CHECKMARK_LONG_MULTIPLIER * mRadius);
            }

            // Start the second part when this one ends.
            if (interpolatedTime == 1.0f) {
                if (mCheckMarkRight.getState() == AnimatedDrawState.NONE)
                    mCheckMarkRight.startAnimation();
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (start != null && end != null) {
                float interpolation = getAnimatedValue();

                canvas.drawLine(start.x, start.y,
                        start.x + interpolation * (end.x - start.x),
                        start.y + interpolation * (end.y - start.y),
                        mCheckMarkPaint);
            }
        }
    }

    /**
     * Draws the right part of the check mark.
     */
    private class CheckMarkRightAnimatedDraw extends AnimatedDraw {

        private PointF start;
        private PointF end;

        public CheckMarkRightAnimatedDraw(TimeInterpolator interpolator, int durationMillis) {
            super(interpolator, durationMillis);
        }

        @Override
        public void onFrame(float interpolatedTime) {
            if (interpolatedTime == 0.0f) {
                start = new PointF(mCenter.x, mCenter.y + CHECKMARK_LONG_MULTIPLIER * mRadius);
                end = new PointF(mCenter.x + CHERKMARK_SHORT_MULTIPLIER * mRadius,
                        mCenter.y - CHECKMARK_LONG_MULTIPLIER * mRadius);
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (start != null && end != null) {
                float interpolation = getAnimatedValue();

                canvas.drawLine(start.x, start.y,
                        start.x + interpolation * (end.x - start.x),
                        start.y - interpolation * (start.y - end.y),
                        mCheckMarkPaint);
            }
        }
    }
}
