package com.bid.launcherwatch;



import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;

public class StepsMeterView extends RoundProgressBarWidthNumber {
    private Drawable[] mColors;
    /* access modifiers changed from: private */
    public boolean mIsAnim;
    private int[] mLevels;

    private class ProgressBarAnimation extends Animation {
        private float mFrom;
        private float mTo;

        public ProgressBarAnimation(float mFrom2, float mTo2) {
            this.mFrom = mFrom2;
            this.mTo = mTo2;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float mParamFloat, Transformation mParamTransformation) {
            super.applyTransformation(mParamFloat, mParamTransformation);
            StepsMeterView.this.setProgress((int) (this.mFrom + ((this.mTo - this.mFrom) * mParamFloat)));
        }
    }

    public StepsMeterView(Context context) {
        this(context, null);
    }

    public StepsMeterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepsMeterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsAnim = false;
        init(context);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void draw(Canvas paramCanvas) {
        int i = getMeasuredWidth();
        int j = getMeasuredHeight();
        paramCanvas.save();
        paramCanvas.rotate(270.0f, (float) (i / 2), (float) (j / 2));
        super.draw(paramCanvas);
        paramCanvas.restore();
    }

    private void init(Context context) {
        Resources res = context.getResources();
        TypedArray levels = res.obtainTypedArray(R.array.batterymeter_color_levels);
        TypedArray colors = res.obtainTypedArray(R.array.batterymeter_color_drawable);
        int N = levels.length();
        this.mLevels = new int[N];
        this.mColors = new Drawable[N];
        for (int i = 0; i < N; i++) {
            this.mLevels[i] = levels.getInt(i, 0);
            this.mColors[i] = colors.getDrawable(i);
        }
        levels.recycle();
        colors.recycle();
    }

    public void setProgressByLevel(int mLevel) {
        Drawable colorForLevel = getColorForLevel(mLevel);
        setProgress(mLevel);
    }

    private Drawable getColorForLevel(int percent) {
        Drawable color = null;
        for (int i = 0; i < this.mColors.length; i++) {
            int thresh = this.mLevels[i];
            color = this.mColors[i];
            if (percent <= thresh) {
                return color;
            }
        }
        return color;
    }

    public boolean isAnim() {
        return this.mIsAnim;
    }

    private void startAnim(int level) {
        ProgressBarAnimation localProgressBarAnimation = new ProgressBarAnimation(0.0f, (float) level);
        Log.d("BatteryMeterView", "startAnim, level = " + level);
        localProgressBarAnimation.setInterpolator(new OvershootInterpolator(0.5f));
        localProgressBarAnimation.setDuration(2000);
        localProgressBarAnimation.setRepeatCount(0);
        localProgressBarAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                StepsMeterView.this.mIsAnim = false;
            }
        });
        startAnimation(localProgressBarAnimation);
        setProgressDrawable(getColorForLevel(level));
        this.mIsAnim = true;
    }

    private void stopAnim() {
        Log.d("BatteryMeterView", "stopAnim");
        clearAnimation();
        this.mIsAnim = false;
    }

    public void runAnim(int level) {
        if (isAnim()) {
            stopAnim();
        }
        startAnim(level);
    }

    public void cleanAnim() {
        stopAnim();
        setProgressByLevel(0);
    }
}

