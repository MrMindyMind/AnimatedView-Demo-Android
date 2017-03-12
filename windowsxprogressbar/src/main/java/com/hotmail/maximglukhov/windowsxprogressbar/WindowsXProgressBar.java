package com.hotmail.maximglukhov.windowsxprogressbar;

import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.Interpolator;

import com.hotmail.maximglukhov.animatedviewlib.AnimatedDraw;
import com.hotmail.maximglukhov.animatedviewlib.AnimatedView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 11-Mar-17.
 */

public class WindowsXProgressBar extends AnimatedView {

    /**
     * Defines the default amount of dots for the progress bar.
     */
    private static final int DEFAULT_DOT_COUNT = 5;

    /**
     * Defines if the animation is indeterminate by default.
      */
    private static final boolean DEFAULT_IS_INDETERMINATE = true;

    /**
     * Dot animation duration.
     */
    private static final int DOT_ANIMATION_DURATION = 1500;

    /**
     * Defines if the animation is horizontal or circular.
     */
    private static final boolean DEFAULT_IS_HORIZONTAL = true;

    /**
     * Determines if the animation is indeterminate.
     */
    private boolean mIsIndeterminate;

    /**
     * Determines if the animation is horizontal.
     */
    private boolean mIsHorizontal;

    /**
     * Defines the amount of dots in the progress.
     */
    private int mDotCount;

    /**
     * Time fraction to invoke the next dot.
     * Calculated as 1/dotCount.
     */
    private float mNextDotTimeFraction;
    /**
     * Paint for every dot.
     */
    private Paint mDotPaint;

    /**
     * Available view height.
     */
    private int mHeight;
    /**
     * Available view width.
     */
    private int mWidth;
    /**
     * Center of the view.
     */
    private PointF mCenter;
    /**
     * Radius from the center of the view.
     */
    private float mRadius;

    /**
     * Device's screen density. Important for consistent sizing on different displays.
     */
    private float mScreenDensity;

    /**
     * {@link ArrayList} of {@link ProgressDotAnimatedDraw} objects.
     * Each {@link ProgressDotAnimatedDraw} invokes the next one in the list when the required amount of time passes.
     */
    private List<ProgressDotAnimatedDraw> mProgressDotAnimatedDraws = new ArrayList<>();

    /**
     * Defines the radius for every dot.
     */
    private float mDotRadius;

    public WindowsXProgressBar(Context context) {
        this(context, null, 0);
    }

    public WindowsXProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WindowsXProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public WindowsXProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Starts animations.
     */
    public void runAnimations() {
        if (mProgressDotAnimatedDraws.size() > 0) {
            // Restart just the first one since it's going to invoke the next ones.
            mProgressDotAnimatedDraws.get(0).startAnimation();
        }
    }

    /**
     * Stops all animations.
     */
    public void stopAnimations() {
        // Stop all dot animations.
        for (AnimatedDraw animatedDraw: mProgressDotAnimatedDraws) {
            animatedDraw.stopAnimation();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate available width and height.
        mWidth = w - (getPaddingLeft() + getPaddingRight());
        mHeight = h - (getPaddingBottom() + getPaddingTop());

        mCenter = new PointF(mWidth/2, mHeight/2);

        mRadius = Math.min(mWidth, mHeight) / 2.0f;
    }

    @Override
    protected void onDrawStatics(Canvas canvas) {
        // Draw non-animating here.
    }

    /**
     * Initialize this view's attributes.
     * @param context {@link Context} to initialize for.
     * @param attrs Attributes for this view.
     * @param defStyleAttr Style attributes.
     * @param defStyleRes Style resources.
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        initAttributes(context, attrs, defStyleAttr, defStyleRes);
        initAnimatedDraws();
        initPaints();

        calculateScreenDensity(context);
        mDotRadius = 2.5f * mScreenDensity;

        // Run automatically.
        if (mIsIndeterminate) {
            runAnimations();
        }
    }

    /**
     * Initialize paints.
     */
    private void initPaints() {
        mDotPaint = new Paint();
        mDotPaint.setColor(Color.BLUE);
        mDotPaint.setStyle(Paint.Style.FILL);
        mDotPaint.setAntiAlias(true);
    }

    /**
     * Initialize attributes
     * @param context {@link Context} to initialize for.
     * @param attrs Attributes for this view.
     * @param defStyleAttr Style attributes.
     * @param defStyleRes Style resources.
     */
    private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs != null) {
            TypedArray styledAttrsArr = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.WindowsXProgressBar, defStyleAttr, defStyleRes);
            try {
                mIsIndeterminate = styledAttrsArr.getBoolean(
                        R.styleable.WindowsXProgressBar_indeterminate,
                        DEFAULT_IS_INDETERMINATE);
                setDotCount(styledAttrsArr.getInt(
                        R.styleable.WindowsXProgressBar_dots_count,
                        DEFAULT_DOT_COUNT));
                mIsHorizontal = styledAttrsArr.getBoolean(
                        R.styleable.WindowsXProgressBar_horizontal,
                        DEFAULT_IS_HORIZONTAL);
            } finally {
                styledAttrsArr.recycle();
            }
        }
    }

    /**
     * Initialize {@link AnimatedDraw} objects.
     */
    private void initAnimatedDraws() {
        // Clear any current animated draws.
        if (mProgressDotAnimatedDraws.size() > 0) {
            for (AnimatedDraw animatedDraw: mProgressDotAnimatedDraws) {
                removeAnimated(animatedDraw);
            }
        }
        mProgressDotAnimatedDraws.clear();

        // Create animated draw for each dot count.
        for (int i = 0; i < mDotCount; i++ ) {
            ProgressDotAnimatedDraw animatedDraw = new ProgressDotAnimatedDraw(
                    new CustomInterpolator(), DOT_ANIMATION_DURATION, i);

            mProgressDotAnimatedDraws.add(animatedDraw);
            addAnimated(animatedDraw);
        }
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

    private void setDotCount(int dotCount) {
        mDotCount = dotCount;

        mNextDotTimeFraction = 1.0f / dotCount;
    }

    private class ProgressDotAnimatedDraw extends AnimatedDraw {

        /**
         * Defines the order in the dot list.
         */
        private final int mSerialCount;

        /**
         * Defines if this dot has invoked the animation of the next dot.
         */
        private boolean mInvokedNext = false;

        /**
         * Defines if this is the last dot in the list.
         */
        private boolean mIsLast = false;

        public ProgressDotAnimatedDraw(TimeInterpolator interpolator, int durationMillis,
                                       int serialCount) {
            super(interpolator, durationMillis);

            mSerialCount = serialCount;

            if (serialCount + 1 == mDotCount) {
                mIsLast = true;
            }
        }

        @Override
        public void onFrame(float interpolatedTime) {
            if (mIsLast) {
                if (interpolatedTime == 1.0f) {
                    // Check if the animation is indeterminate - if it is reset it once animation ends.
                    if (mIsIndeterminate) {
                        try {
                            for (AnimatedDraw animatedDraw: mProgressDotAnimatedDraws) {
                                animatedDraw.stopAnimation();
                            }

                            AnimatedDraw firstDot = mProgressDotAnimatedDraws
                                    .get(0);

                            firstDot.startAnimation();
                        } catch (IndexOutOfBoundsException e) {
                            // Do nothing.
                        }
                    }
                }
            } else {
                // Check if the next dot animation should be invoked.
                if (!mInvokedNext && interpolatedTime >= mNextDotTimeFraction) {
                    mInvokedNext = true;

                    try {
                        AnimatedDraw nextAnimatedDraw = mProgressDotAnimatedDraws
                                .get(mSerialCount + 1);

                        nextAnimatedDraw.startAnimation();
                    } catch (IndexOutOfBoundsException e) {
                        // Do nothing.
                    }
                }
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            float interpolatedValue = getAnimatedValue();
            if (interpolatedValue > 0 && interpolatedValue < 1) {
                // Use radius offset to make sure the dot isn't cut in half when starting.
                float offset = mDotRadius;

                if (mIsHorizontal) {
                    canvas.drawCircle(offset + mWidth * interpolatedValue,
                            mHeight / 2, mDotRadius, mDotPaint);
                } else {

                    float xInterpolation = (float) (mCenter.x
                            + (mRadius - mDotRadius) * Math.cos(interpolatedValue * 2.0 * 3.14));
                    float yInterpolation = (float) (mCenter.y
                            + (mRadius - mDotRadius) * Math.sin(interpolatedValue * 2.0 * 3.14));

                    canvas.drawCircle(xInterpolation, yInterpolation,
                            mDotRadius, mDotPaint);
                }
            }
        }

        @Override
        public void stopAnimation() {
            super.stopAnimation();

            mInvokedNext = false;
        }
    }

    /**
     * Custom interpolator for dot animation.
     * This interpolator implements two different functions according to time.
     *
     * <p>For t <= 0.5: y = -1 * cos((t * 2+1)π * 0.5)/2</p>
     * <p>For t > 0.5: y = 0.5 + 0.5 - (-1 * cos((t * 2+1)π * 0.5)/2)</p>
     *
     * These functions allows the dots to accelerate and decelerate twice.
     */
    private class CustomInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float t) {
            float func = (float) (-1.0f * Math.cos((t * 2 + 1) * 3.14 * 0.5f) / 2);

            if (t > 0.5f) {
                float currentValue = func;
                func = 0.5f + 0.5f - currentValue;
            }

            return func;
        }
    }
}
