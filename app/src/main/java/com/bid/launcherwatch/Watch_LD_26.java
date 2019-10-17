package com.bid.launcherwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class Watch_LD_26 extends View {
    /* access modifiers changed from: private */
    public Time mCalendar;
    private Drawable mCenter;
    private boolean mChanged;
    private Context mContext;
    Paint mDatePaint;
    private Drawable mDial;
    private int mDialHeight;
    private int mDialWidth;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private float mHour;
    private Drawable mHourHand;
    Paint mHourLinePaint;
    private Drawable mHourbg;
    private Drawable mMinuteHand;
    Paint mMinuteLinePaint;
    private Drawable mMinutebg;
    private float mMinutes;
    private float mSecond;
    private Drawable mSecondHand;
    /* access modifiers changed from: private */
    public Runnable mTicker;
    /* access modifiers changed from: private */
    public boolean mTickerStopped;

    public Watch_LD_26(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public Watch_LD_26(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    public Watch_LD_26(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCalendar = new Time();
        this.mHandler = new Handler();
        this.mTickerStopped = false;
        this.mContext = context;
        Resources r = getContext().getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomClock, defStyle, 0);
        this.mHourbg = r.getDrawable(R.drawable.ld_26_hourbg);
        this.mMinutebg = r.getDrawable(R.drawable.ld_26_minutebg);
        this.mDial = a.getDrawable(R.styleable.CustomClock_dial);
        this.mHourHand = a.getDrawable(R.styleable.CustomClock_hand_hour);
        this.mMinuteHand = a.getDrawable(R.styleable.CustomClock_hand_minute);
        this.mSecondHand = a.getDrawable(R.styleable.CustomClock_hand_second);
        this.mCenter = a.getDrawable(R.styleable.CustomClock_center);
        a.recycle();
        if (this.mDial != null) {
            this.mDialWidth = this.mDial.getIntrinsicWidth();
            this.mDialHeight = this.mDial.getIntrinsicHeight();
        } else {
            this.mDialWidth = 1;
            this.mDialHeight = 1;
        }
        if (this.mCalendar == null) {
            this.mCalendar = new Time();
        }
        Typeface create = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        this.mDatePaint = Watch_Common.createPaint(context, Typeface.createFromAsset(context.getAssets(), "fonts/096-CAI978.ttf"), Align.CENTER, 36, -1);
        this.mHourLinePaint = new Paint();
        this.mHourLinePaint.setStyle(Style.STROKE);
        this.mHourLinePaint.setStrokeWidth(50.0f);
        this.mHourLinePaint.setStrokeCap(Cap.ROUND);
        this.mHourLinePaint.setColor(Color.parseColor("#fc015b"));
        this.mHourLinePaint.setAntiAlias(true);
        this.mMinuteLinePaint = new Paint();
        this.mMinuteLinePaint.setStyle(Style.STROKE);
        this.mMinuteLinePaint.setStrokeWidth(60.0f);
        this.mHourLinePaint.setStrokeCap(Cap.ROUND);
        this.mMinuteLinePaint.setColor(Color.parseColor("#ff8c01"));
        this.mMinuteLinePaint.setAntiAlias(true);
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
                if (!Watch_LD_26.this.mTickerStopped) {
                    Watch_LD_26.this.onTimeChanged(Watch_LD_26.this.mCalendar);
                    Watch_LD_26.this.invalidate();
                    long now = SystemClock.uptimeMillis();
                    Watch_LD_26.this.mHandler.postAtTime(Watch_LD_26.this.mTicker, now + (1000 - (now % 1000)));
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
        float hour;
        boolean changed = this.mChanged;
        if (changed) {
            this.mChanged = false;
        }
        int x = (getRight() - getLeft()) / 2;
        int y = (getBottom() - getTop()) / 2;
        Drawable dial = this.mDial;
        if (dial != null) {
            int w = dial.getIntrinsicWidth();
            int h = dial.getIntrinsicHeight();
            int i = w;
            int i2 = h;
            if (changed) {
                dial.setBounds(x - (w / 2), y - (h / 2), (w / 2) + x, (h / 2) + y);
            }
            dial.draw(canvas);
        }
        String hourText = Watch_Common.getHourText(this.mContext);
        canvas.drawText(hourText + " " + Watch_Common.getMinuteText(this.mCalendar), 200.0f, 220.0f, this.mDatePaint);
        if (this.mCalendar.second % 2 == 0) {
            Canvas canvas2 = canvas;
            canvas2.drawText(":", 200.0f, 220.0f, this.mDatePaint);
        }
        RectF oval1 = new RectF(25.0f, 25.0f, 375.0f, 375.0f);
        if (this.mHour > 12.0f) {
            hour = this.mHour - 12.0f;
        } else {
            hour = this.mHour;
        }
        canvas.drawArc(oval1, -90.0f, (hour / 12.0f) * 360.0f, false, this.mHourLinePaint);
        canvas.drawArc(new RectF(80.0f, 80.0f, 320.0f, 320.0f), -90.0f, (this.mMinutes / 60.0f) * 360.0f, false, this.mMinuteLinePaint);
        Watch_Common.drawHourhand(canvas, this.mHour, this.mHourHand, changed, x, y);
        Watch_Common.drawMinuteHand(canvas, this.mMinutes, this.mMinuteHand, changed, x, y);
        Watch_Common.drawDrawable(canvas, this.mHourbg, 0, 400);
        Watch_Common.drawDrawable(canvas, this.mMinutebg, 0, 400);
    }

    public void updateContentDescription(Time time) {
        setContentDescription(DateUtils.formatDateTime(getContext(), time.toMillis(false), 129));
    }
}
