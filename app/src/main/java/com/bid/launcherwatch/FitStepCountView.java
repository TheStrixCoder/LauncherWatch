package com.bid.launcherwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

public class FitStepCountView extends ProgressBar {
    private int curr_setps;
    private Context mContext;
    private boolean mIsAnim;
    private Paint mpaint;
    private BroadcastReceiver receiver;

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
            float f = this.mFrom + ((this.mTo - this.mFrom) * mParamFloat);
            if (f > 100.0f) {
                f = 100.0f;
            }
            FitStepCountView.this.setProgressByLevel((int) f);
        }
    }

    public FitStepCountView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public FitStepCountView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    public FitStepCountView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsAnim = false;
        this.curr_setps = 0;
        this.receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int counts_ratio = (FitStepCountView.this.getStepCounts() * 100) / 10000;
                if (counts_ratio != 0 && intent.getAction().equals("com.step.count.page")) {
                    if (intent.getBooleanExtra("selected", false)) {
                        if (!FitStepCountView.this.isAnim()) {
                            FitStepCountView.this.startAnim(counts_ratio);
                        }
                    } else if (FitStepCountView.this.isAnim()) {
                        FitStepCountView.this.stopAnim();
                    }
                }
            }
        };
        init(context);
        this.mContext = context;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter mIF = new IntentFilter();
        mIF.addAction("com.step.count.page");
        this.mContext.registerReceiver(this.receiver, mIF);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mContext.unregisterReceiver(this.receiver);
    }

    public void draw(Canvas paramCanvas) {
        int i = getMeasuredWidth();
        int j = getMeasuredHeight();
        paramCanvas.save();
        paramCanvas.rotate(270.0f, (float) (i / 2), (float) (j / 2));
        paramCanvas.drawCircle((float) (i / 2), (float) (j / 2), (float) ((j / 2) - 20), this.mpaint);
        super.draw(paramCanvas);
        paramCanvas.restore();
    }

    private void init(Context context) {
        this.mpaint = new Paint();
        this.mpaint.setColor(-7829368);
        this.mpaint.setStrokeWidth(25.0f);
        this.mpaint.setStyle(Style.STROKE);
        this.mpaint.setAntiAlias(true);
        setProgressDrawable(getResources().getDrawable(R.drawable.fitsteps_normal_circle));
    }

    /* access modifiers changed from: private */
    public void setProgressByLevel(int mLevel) {
        setProgress(mLevel);
    }

    /* access modifiers changed from: private */
    public boolean isAnim() {
        return this.mIsAnim;
    }

    /* access modifiers changed from: private */
    public void startAnim(int level) {
        ProgressBarAnimation localProgressBarAnimation = new ProgressBarAnimation(0.0f, (float) level);
        localProgressBarAnimation.setInterpolator(new OvershootInterpolator(0.5f));
        localProgressBarAnimation.setDuration(2000);
        localProgressBarAnimation.setRepeatCount(-1);
        startAnimation(localProgressBarAnimation);
        this.mIsAnim = true;
    }

    /* access modifiers changed from: private */
    public void stopAnim() {
        clearAnimation();
        this.mIsAnim = false;
    }

    /* access modifiers changed from: private */
    public int getStepCounts() {
        return this.curr_setps;
    }
}
