package com.bid.launcherwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View.MeasureSpec;
import android.widget.ProgressBar;

public class HorizontalProgressBarWithNumber extends ProgressBar {
    protected boolean mIfDrawText;
    protected Paint mPaint;
    protected int mReachedBarColor;
    protected int mReachedProgressBarHeight;
    protected int mRealWidth;
    protected int mTextColor;
    protected int mTextOffset;
    protected int mTextSize;
    protected int mUnReachedBarColor;
    protected int mUnReachedProgressBarHeight;

    public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaint = new Paint();
        this.mTextColor = -261935;
        this.mTextSize = sp2px(10);
        this.mTextOffset = dp2px(10);
        this.mReachedProgressBarHeight = dp2px(2);
        this.mReachedBarColor = -261935;
        this.mUnReachedBarColor = -2894118;
        this.mUnReachedProgressBarHeight = dp2px(2);
        this.mIfDrawText = true;
        obtainStyledAttributes(attrs);
        this.mPaint.setTextSize((float) this.mTextSize);
        this.mPaint.setColor(this.mTextColor);
    }

    /* access modifiers changed from: protected */
    public synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), measureHeight(heightMeasureSpec));
        this.mRealWidth = (getMeasuredWidth() - getPaddingRight()) - getPaddingLeft();
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            return specSize;
        }
        int result = (int) (((float) (getPaddingTop() + getPaddingBottom())) + Math.max((float) Math.max(this.mReachedProgressBarHeight, this.mUnReachedProgressBarHeight), Math.abs(this.mPaint.descent() - this.mPaint.ascent())));
        if (specMode == Integer.MIN_VALUE) {
            return Math.min(result, specSize);
        }
        return result;
    }


    private void obtainStyledAttributes(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBarWithNumber);
        this.mTextColor = attributes.getColor(R.styleable.HorizontalProgressBarWithNumber_progress_text_color, -261935);
        this.mTextSize = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithNumber_progress_text_size, (float) this.mTextSize);
        this.mReachedBarColor = attributes.getColor(R.styleable.HorizontalProgressBarWithNumber_progress_reached_color, this.mTextColor);
        this.mUnReachedBarColor = attributes.getColor(R.styleable.HorizontalProgressBarWithNumber_progress_unreached_color, -2894118);
        this.mReachedProgressBarHeight = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithNumber_progress_reached_bar_height, (float) this.mReachedProgressBarHeight);
        this.mUnReachedProgressBarHeight = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithNumber_progress_unreached_bar_height, (float) this.mUnReachedProgressBarHeight);
        this.mTextOffset = (int) attributes.getDimension(R.styleable.HorizontalProgressBarWithNumber_progress_text_offset, (float) this.mTextOffset);
        if (attributes.getInt(R.styleable.HorizontalProgressBarWithNumber_progress_text_visibility, 0) != 0) {
            this.mIfDrawText = false;
        }
        attributes.recycle();
    }

    /* access modifiers changed from: protected */
    public synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate((float) getPaddingLeft(), (float) (getHeight() / 2));
        boolean noNeedBg = false;
        float progressPosX = (float) ((int) (((float) this.mRealWidth) * ((((float) getProgress()) * 1.0f) / ((float) getMax()))));
        String text = getProgress() + "%";
        float textWidth = this.mPaint.measureText(text);
        float textHeight = (this.mPaint.descent() + this.mPaint.ascent()) / 2.0f;
        if (progressPosX + textWidth > ((float) this.mRealWidth)) {
            progressPosX = ((float) this.mRealWidth) - textWidth;
            noNeedBg = true;
        }
        float endX = progressPosX - ((float) (this.mTextOffset / 2));
        if (endX > 0.0f) {
            this.mPaint.setColor(this.mReachedBarColor);
            this.mPaint.setStrokeWidth((float) this.mReachedProgressBarHeight);
            canvas.drawLine(0.0f, 0.0f, endX, 0.0f, this.mPaint);
        }
        if (this.mIfDrawText) {
            this.mPaint.setColor(this.mTextColor);
            canvas.drawText(text, progressPosX, -textHeight, this.mPaint);
        }
        if (!noNeedBg) {
            float start = ((float) (this.mTextOffset / 2)) + progressPosX + textWidth;
            this.mPaint.setColor(this.mUnReachedBarColor);
            this.mPaint.setStrokeWidth((float) this.mUnReachedProgressBarHeight);
            canvas.drawLine(start, 0.0f, (float) this.mRealWidth, 0.0f, this.mPaint);
        }
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(1, (float) dpVal, getResources().getDisplayMetrics());
    }

    /* access modifiers changed from: protected */
    public int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(2, (float) spVal, getResources().getDisplayMetrics());
    }
}

