package com.bid.launcherwatch.cleananimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.bid.launcherwatch.R;

public class CenterDot extends View {
    private int mBtnTextColor;
    private int mMaxYOffset;
    private Paint mPaint;
    private int mProgress;
    private int mProgressTextColor;
    private int mProgressTextSize;
    private int mRadius;
    private int mWidth;
    private int mYOffset;

    public CenterDot(Context context, int width) {
        this(context, null, width);
    }

    public CenterDot(Context context, AttributeSet attrs, int width) {
        super(context, attrs);
        this.mWidth = width;
        init();
    }

    public void setAnimationPogress(float progress) {
        this.mYOffset = (int) (((float) this.mMaxYOffset) * (1.0f - progress));
        setScaleX((float) (1.0d - (((double) progress) * 0.2d)));
        setScaleY((float) (1.0d - (((double) progress) * 0.2d)));
        invalidate();
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        invalidate();
    }

    public void setProgressTextSize(int progressTextSize) {
        this.mProgressTextSize = progressTextSize;
    }

    public void setProgressTextColor(int progressTextColor) {
        this.mProgressTextColor = progressTextColor;
    }

    public void setBtnTextColor(int btnTextColor) {
        this.mBtnTextColor = btnTextColor;
    }

    private void init() {
        this.mRadius = this.mWidth / 2;
        this.mMaxYOffset = this.mRadius / 2;
        this.mYOffset = this.mMaxYOffset;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        setBackgroundResource(R.drawable.saoba);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(this.mWidth, this.mWidth);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String str = this.mProgress + "";
        new Rect();
        this.mPaint.setTextSize((float) ((this.mYOffset * 2) / 3));
        this.mPaint.setColor(getResources().getColor(R.color.colorPrimary));
        canvas.save();
        canvas.restore();
    }
}

