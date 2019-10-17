package com.bid.launcherwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDialClockFace extends View {
    private boolean enable;
    /* access modifiers changed from: private */
    public Time mCalendar;
    private Drawable mCenter;
    private boolean mChanged;
    private Context mContext;
    private Drawable mDial;
    private int mDialHeight;
    private int mDialWidth;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private float mHour;
    private Drawable mHourHand;
    Paint mHourMinutePaint;
    Paint mHourMinutePaintStroke;
    private Drawable mMinuteHand;
    private float mMinutes;
    private float mSecond;
    private Drawable mSecondHand;
    /* access modifiers changed from: private */
    public Runnable mTicker;
    /* access modifiers changed from: private */
    public boolean mTickerStopped;
    Paint mWeekPaint;
    Paint mWeekPaintStroke;
    Paint mYearPaint;
    Paint mYearPaintStroke;

    public CustomDialClockFace(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public CustomDialClockFace(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    public CustomDialClockFace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCalendar = new Time();
        this.mHandler = new Handler();
        this.mTickerStopped = false;
        this.enable = true;
        this.mContext = context;
        Resources resources = getContext().getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomClock, defStyle, 0);
        this.mDial = getCustomPicDrawable();
        if (this.mDial == null) {
            this.mDial = a.getDrawable(R.styleable.CustomClock_dial);
        }
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
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        this.mHourMinutePaintStroke = Watch_Common.createPaint(context, font, Align.CENTER, 60, -1);
        this.mHourMinutePaintStroke.setStyle(Style.STROKE);
        this.mHourMinutePaintStroke.setColor(Color.parseColor("#000000"));
        this.mHourMinutePaintStroke.setStrokeWidth(4.0f);
        this.mWeekPaintStroke = Watch_Common.createPaint(context, font, Align.CENTER, 22, -1);
        this.mWeekPaintStroke.setStyle(Style.STROKE);
        this.mWeekPaintStroke.setColor(Color.parseColor("#000000"));
        this.mWeekPaintStroke.setStrokeWidth(4.0f);
        this.mYearPaintStroke = Watch_Common.createPaint(context, font, Align.LEFT, 22, -1);
        this.mYearPaintStroke.setStyle(Style.STROKE);
        this.mYearPaintStroke.setColor(Color.parseColor("#000000"));
        this.mYearPaintStroke.setStrokeWidth(4.0f);
        this.mHourMinutePaint = Watch_Common.createPaint(context, font, Align.CENTER, 60, -1);
        this.mWeekPaint = Watch_Common.createPaint(context, font, Align.CENTER, 22, -1);
        this.mYearPaint = Watch_Common.createPaint(context, font, Align.LEFT, 22, -1);
    }

    public Drawable getCustomPicDrawable() {
        String picPath = this.mContext.getSharedPreferences("custom_clockview_bg_img", 0).getString("bg_img", "");
        if (new File(picPath).exists()) {
            return new BitmapDrawable(BitmapFactory.decodeFile(picPath));
        }
        return null;
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
                if (!CustomDialClockFace.this.mTickerStopped) {
                    CustomDialClockFace.this.onTimeChanged(CustomDialClockFace.this.mCalendar);
                    CustomDialClockFace.this.invalidate();
                    long now = SystemClock.uptimeMillis();
                    CustomDialClockFace.this.mHandler.postAtTime(CustomDialClockFace.this.mTicker, now + (1000 - (now % 1000)));
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
        boolean changed = this.mChanged;
        if (changed) {
            this.mChanged = false;
        }
        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();
        int x = availableWidth / 2;
        int y = availableHeight / 2;
        Drawable dial = this.mDial;
        int i = availableWidth;
        int i2 = availableHeight;
        if (dial != null) {
            if (changed) {
                dial.setBounds(x - (availableWidth / 2), y - (availableHeight / 2), (availableWidth / 2) + x, (availableHeight / 2) + y);
            }
            dial.draw(canvas);
        }
        String week = getWeek(this.mCalendar, this.mContext);
        canvas.drawText(getHourMinText(this.mContext), (float) (availableWidth / 2), (float) 316, this.mHourMinutePaintStroke);
        canvas.drawText(getHourMinText(this.mContext), (float) (availableWidth / 2), (float) 316, this.mHourMinutePaint);
        canvas.drawText(week, (float) ((availableWidth / 2) - 70), (float) 353, this.mYearPaintStroke);
        canvas.drawText(week, (float) ((availableWidth / 2) - 70), (float) 353, this.mYearPaint);
        canvas.drawText(getYear(), (float) ((availableWidth / 2) - 13), (float) 353, this.mYearPaintStroke);
        canvas.drawText(getYear(), (float) ((availableWidth / 2) - 13), (float) 353, this.mYearPaint);
    }

    public String getWeek(Time mCalendar2, Context context) {
        switch (mCalendar2.weekDay) {
            case 0:
                return context.getString(R.string.sunday_abbre);
            case 1:
                return context.getString(R.string.monday_abbre);
            case 2:
                return context.getString(R.string.tuesday_abbre);
            case 3:
                return context.getString(R.string.wednesday_abbre);
            case 4:
                return context.getString(R.string.thursday_abbre);
            case 5:
                return context.getString(R.string.friday_abbre);
            case 6:
                return context.getString(R.string.saturday_abbre);
            default:
                return "";
        }
    }

    public void updateContentDescription(Time time) {
        setContentDescription(DateUtils.formatDateTime(getContext(), time.toMillis(false), 129));
    }

    public static String getHourMinText(Context context) {
        String str = "";
        if (DateFormat.is24HourFormat(context)) {
            return new SimpleDateFormat("HH:mm").format(new Date());
        }
        return new SimpleDateFormat("hh:mm").format(new Date());
    }

    public static String getYear() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
