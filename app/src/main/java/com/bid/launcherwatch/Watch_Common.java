package com.bid.launcherwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Watch_Common extends View {
    public Time mCalendar;
    public boolean mChanged;
    public Drawable mDial;
    public int mDialHeight;
    public int mDialWidth;
    public final Handler mHandler;
    public float mHour;
    public float mMinutes;
    public float mSecond;
    public Runnable mTicker;
    public boolean mTickerStopped;

    public Watch_Common(Context context, Handler mHandler) {
        super(context);
        this.mHandler = mHandler;
    }

    public static Paint createPaint(Context context, Typeface font, Align align, int size, int color) {
        Paint mPaint = new Paint();
        mPaint.setTypeface(font);
        mPaint.setTextSize((float) size);
        mPaint.setColor(color);
        mPaint.setTextAlign(align);
        mPaint.setAntiAlias(true);
        return mPaint;
    }

    public void onAttachedToWindow() {
        this.mTickerStopped = false;
        super.onAttachedToWindow();
        runTicker();
    }

    public void onTimeChanged(Time mCalendar2) {
        mCalendar2.setToNow();
        int hour = mCalendar2.hour;
        int minute = mCalendar2.minute;
        int second = mCalendar2.second;
        this.mHour = ((float) hour) + (this.mMinutes / 60.0f);
        this.mMinutes = ((float) minute) + (((float) second) / 60.0f);
        this.mSecond = (float) second;
        this.mChanged = true;
        updateContentDescription(mCalendar2);
    }

    public void runTicker() {
        this.mTicker = new Runnable() {
            public void run() {
                if (!Watch_Common.this.mTickerStopped) {
                    Watch_Common.this.onTimeChanged(Watch_Common.this.mCalendar);
                    Watch_Common.this.invalidate();
                    long now = SystemClock.uptimeMillis();
                    Watch_Common.this.mHandler.postAtTime(Watch_Common.this.mTicker, now + (1000 - (now % 1000)));
                }
            }
        };
        this.mTicker.run();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mTickerStopped = true;
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"NewApi"})
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        float hScale = 1.0f;
        float vScale = 1.0f;
        if (widthMode != 0 && widthSize < this.mDialWidth) {
            hScale = ((float) widthSize) / ((float) this.mDialWidth);
        }
        if (heightMode != 0 && heightSize < this.mDialHeight) {
            vScale = ((float) heightSize) / ((float) this.mDialHeight);
        }
        float scale = Math.min(hScale, vScale);
        setMeasuredDimension(resolveSizeAndState((int) (((float) this.mDialWidth) * scale), widthMeasureSpec, 0), resolveSizeAndState((int) (((float) this.mDialHeight) * scale), heightMeasureSpec, 0));
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mChanged = true;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean changed = this.mChanged;
        if (changed) {
            this.mChanged = false;
        }
        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();
        int x = availableWidth / 2;
        int y = availableHeight / 2;
        Drawable dial = this.mDial;
        if (dial != null) {
            int w = dial.getIntrinsicWidth();
            int h = dial.getIntrinsicHeight();
            int i = w;
            int i2 = h;
            if (availableWidth < w || availableHeight < h) {
                float scale = Math.min(((float) availableWidth) / ((float) w), ((float) availableHeight) / ((float) h));
                canvas.save();
                canvas.scale(scale, scale, (float) x, (float) y);
            } else if (availableWidth > w && availableHeight > h) {
                float scale2 = Math.min(((float) availableWidth) / ((float) w), ((float) availableHeight) / ((float) h));
                canvas.save();
                canvas.scale(scale2, scale2, (float) x, (float) y);
            }
            if (changed) {
                dial.setBounds(x - (w / 2), y - (h / 2), (w / 2) + x, (h / 2) + y);
            }
            dial.draw(canvas);
        }
    }

    public static void drawHourhand(Canvas canvas, float mHour2, Drawable hourHand, boolean changed, int x, int y) {
        if (hourHand != null) {
            canvas.save();
            canvas.rotate((mHour2 / 12.0f) * 360.0f, (float) x, (float) y);
            if (changed) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds(x - (w / 2), y - (h / 2), (w / 2) + x, (h / 2) + y);
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    public static void drawMinuteHand(Canvas canvas, float mMinutes2, Drawable minuteHand, boolean changed, int x, int y) {
        if (minuteHand != null) {
            canvas.save();
            canvas.rotate((mMinutes2 / 60.0f) * 360.0f, (float) x, (float) y);
            if (changed) {
                int w = minuteHand.getIntrinsicWidth();
                int h = minuteHand.getIntrinsicHeight();
                minuteHand.setBounds(x - (w / 2), y - (h / 2), (w / 2) + x, (h / 2) + y);
            }
            minuteHand.draw(canvas);
            canvas.restore();
        }
    }

    public static String getHourText(Context context) {
        int hour1;
        String str = "";
        if (DateFormat.is24HourFormat(context)) {
            hour1 = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        } else {
            hour1 = Integer.parseInt(new SimpleDateFormat("hh").format(new Date()));
        }
        if (hour1 < 10) {
            return "0" + hour1;
        }
        return String.valueOf(hour1);
    }

    public static void drawDrawable(Canvas canvas, Drawable drawable, int leftx, int bottomy) {
        if (drawable != null) {
            drawable.setBounds(leftx, bottomy - drawable.getIntrinsicHeight(), leftx + drawable.getIntrinsicWidth(), bottomy);
            drawable.draw(canvas);
        }
    }

    public static String getMinuteText(Time mCalendar2) {
        String str = "00";
        if (mCalendar2.minute < 10) {
            return "0" + String.valueOf(mCalendar2.minute);
        }
        return String.valueOf(mCalendar2.minute);
    }

    public void updateContentDescription(Time time) {
        setContentDescription(DateUtils.formatDateTime(getContext(), time.toMillis(false), 129));
    }
}
