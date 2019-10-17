package com.bid.launcherwatch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RoundProgressBarWidthNumber extends HorizontalProgressBarWithNumber {
    private int mMaxPaintWidth;
    private int mRadius;

    public RoundProgressBarWidthNumber(Context context) {
        this(context, null);
    }

    public RoundProgressBarWidthNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBarWidthNumber(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRadius = dp2px(30);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBarWidthNumber);
        this.mRadius = (int) ta.getDimension(0, (float) this.mRadius);
        ta.recycle();
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setStrokeCap(Cap.ROUND);
    }

    /* access modifiers changed from: protected */
    public synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mMaxPaintWidth = Math.max(this.mReachedProgressBarHeight, this.mUnReachedProgressBarHeight);
        int expect = (this.mRadius * 2) + this.mMaxPaintWidth + getPaddingLeft() + getPaddingRight();
        int realWidth = Math.min(resolveSize(expect, widthMeasureSpec), resolveSize(expect, heightMeasureSpec));
        this.mRadius = (((realWidth - getPaddingLeft()) - getPaddingRight()) - this.mMaxPaintWidth) / 2;
        setMeasuredDimension(realWidth, realWidth);
    }

    /* access modifiers changed from: protected */
    public synchronized void onDraw(Canvas canvas) {
        float measureText = this.mPaint.measureText(getProgress() + "%");
        float descent = (this.mPaint.descent() + this.mPaint.ascent()) / 2.0f;
        canvas.save();
        canvas.translate((float) (getPaddingLeft() + (this.mMaxPaintWidth / 2)), (float) (getPaddingTop() + (this.mMaxPaintWidth / 2)));
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setColor(this.mUnReachedBarColor);
        this.mPaint.setStrokeWidth((float) this.mUnReachedProgressBarHeight);
        canvas.drawCircle((float) this.mRadius, (float) this.mRadius, (float) this.mRadius, this.mPaint);
        this.mPaint.setColor(this.mReachedBarColor);
        this.mPaint.setStrokeWidth((float) this.mReachedProgressBarHeight);
        Canvas canvas2 = canvas;
        canvas2.drawArc(new RectF(0.0f, 0.0f, (float) (this.mRadius * 2), (float) (this.mRadius * 2)), 0.0f, ((((float) getProgress()) * 1.0f) / ((float) getMax())) * 360.0f, false, this.mPaint);
        canvas.restore();
    }
}
