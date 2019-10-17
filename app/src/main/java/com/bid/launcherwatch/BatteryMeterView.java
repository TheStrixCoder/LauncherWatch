package com.bid.launcherwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;

public class BatteryMeterView extends RoundProgressBarWidthNumber {
    private Drawable[] mColors;
    boolean mHasWindowFocus;
    private boolean mIsAnim;
    private int[] mLevels;
    BatteryTracker mTracker;

    private class BatteryTracker extends BroadcastReceiver {
        int health;
        int level;
        int mPrelevel;
        int mPreplugType;
        int plugType;
        boolean plugged;
        int status;
        String technology;
        int temperature;
        boolean testmode;
        int voltage;

        private BatteryTracker() {
            this.level = -1;
            this.mPrelevel = 0;
            this.mPreplugType = -1;
            this.testmode = false;
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BatteryMeterView.this.mHasWindowFocus) {
                if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                    this.level = (int) ((((float) intent.getIntExtra("level", 0)) * 100.0f) / ((float) intent.getIntExtra("scale", 100)));
                    this.plugType = intent.getIntExtra("plugged", 0);
                    this.plugged = this.plugType != 0;
                    this.health = intent.getIntExtra("health", 1);
                    this.status = intent.getIntExtra("status", 1);
                    this.technology = intent.getStringExtra("technology");
                    this.voltage = intent.getIntExtra("voltage", 0);
                    this.temperature = intent.getIntExtra("temperature", 0);
                    Log.d("BatteryMeterView", "plugType = " + this.plugType + ", status = " + this.status + ", level = " + this.level + ", isAnim = " + BatteryMeterView.this.isAnim());
                    if (!this.plugged || this.level >= 100) {
                        if (BatteryMeterView.this.isAnim()) {
                            BatteryMeterView.this.stopAnim();
                        }
                        BatteryMeterView.this.setProgressByLevel(this.level);
                    } else if (this.level != this.mPrelevel || this.plugType != this.mPreplugType) {
                        if (BatteryMeterView.this.isAnim()) {
                            BatteryMeterView.this.stopAnim();
                        }
                        BatteryMeterView.this.startAnim(this.level);
                    } else if (!BatteryMeterView.this.isAnim()) {
                        BatteryMeterView.this.startAnim(this.level);
                    }
                    this.mPrelevel = this.level;
                    this.mPreplugType = this.plugType;
                }
                return;
            }
            if (BatteryMeterView.this.isAnim()) {
                BatteryMeterView.this.stopAnim();
            }
        }
    }

    private class ProgressBarAnimation extends Animation {
        private float mFrom;
        private float mTo;

        public ProgressBarAnimation(float mFrom, float mTo) {
            this.mFrom = mFrom;
            this.mTo = mTo;
        }

        protected void applyTransformation(float mParamFloat, Transformation mParamTransformation) {
            super.applyTransformation(mParamFloat, mParamTransformation);
            BatteryMeterView.this.setProgress((int) (this.mFrom + ((this.mTo - this.mFrom) * mParamFloat)));
        }
    }

    public BatteryMeterView(Context context) {
        this(context, null);
    }

    public BatteryMeterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryMeterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHasWindowFocus = true;
        this.mTracker = new BatteryTracker();
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

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        this.mHasWindowFocus = hasWindowFocus;
        if (hasWindowFocus) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.BATTERY_CHANGED");
            Intent sticky = getContext().registerReceiver(this.mTracker, filter);
            if (sticky != null) {
                this.mTracker.onReceive(getContext(), sticky);
            }
            return;
        }
        getContext().unregisterReceiver(this.mTracker);
        if (isAnim()) {
            stopAnim();
        }
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

    private void setProgressByLevel(int mLevel) {
        setProgressDrawable(getColorForLevel(mLevel));
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

    private boolean isAnim() {
        return this.mIsAnim;
    }

    private void startAnim(int level) {
        ProgressBarAnimation localProgressBarAnimation = new ProgressBarAnimation(0.0f, (float) level);
        Log.d("BatteryMeterView", "startAnim, level = " + level);
        localProgressBarAnimation.setInterpolator(new OvershootInterpolator(0.5f));
        localProgressBarAnimation.setDuration(4000);
        localProgressBarAnimation.setRepeatCount(0);
        startAnimation(localProgressBarAnimation);
        setProgressDrawable(getColorForLevel(level));
        this.mIsAnim = true;
    }

    private void stopAnim() {
        Log.d("BatteryMeterView", "stopAnim");
        clearAnimation();
        this.mIsAnim = false;
    }
}
