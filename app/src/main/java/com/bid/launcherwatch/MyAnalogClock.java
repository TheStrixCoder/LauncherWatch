package com.bid.launcherwatch;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;
import android.view.View.MeasureSpec;

public class MyAnalogClock extends View {
    private int SCREEN_WIDE;
    private int mBatteryBorder;
    private int mBatteryColor;
    private Drawable mBatteryHand;
    /* access modifiers changed from: private */
    public int mBatteryLevel;
    private Postion mBatteryPostion;
    private int mBatteryRadius;
    private int mBatterySize;
    private Time mCalendar;
    /* access modifiers changed from: private */
    public boolean mChanged;
    private int mDate;
    private int mDateColor;
    private Drawable mDateDial;
    private Drawable mDateHand;
    private Postion mDatePostion;
    private int mDateSize;
    private Drawable mDial;
    private int mDialHeight;
    private int mDialWidth;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private boolean mHasAnalogClock;
    private boolean mHasBatteryDial;
    private boolean mHasDateDial;
    private boolean mHasMonthDial;
    private boolean mHasStepDial;
    private boolean mHasWeekDial;
    private float mHour;
    private Drawable mHourHand;
    private Drawable mLittleDateDial;
    private Drawable mLittleMonthDial;
    private Drawable mLittleWeekDial;
    private Drawable mMinuteHand;
    private float mMinutes;
    private int mMonth;
    private int mMonthColor;
    private Drawable mMonthDial;
    private Drawable mMonthHand;
    private Postion mMonthPostion;
    private int mMonthSize;
    private Paint mPaint;
    private Paint mPaintBattery;
    private Paint mPaintStep;
    private Receiver mReceiver;
    Rect mRect;
    RectF mRectF;
    private float mSecond;
    private Drawable mSecondHand;
    /* access modifiers changed from: private */
    public int mSecondHandDuring;
    private int mStep;
    private int mStepBorder;
    private int mStepColor;
    private Drawable mStepHand;
    private Postion mStepPostion;
    private int mStepRadius;
    private int mStepSize;
    private int mStepsTarget;
    private String mStrBattery;
    private String mStrDate;
    private String mStrFri;
    private String mStrHour;
    private String mStrMinute;
    private String mStrMon;
    private String mStrMonth;
    private String mStrSatur;
    private String mStrSecond;
    private String mStrStep;
    private String mStrSun;
    private String mStrThurs;
    private String mStrToday;
    private String mStrTues;
    private String mStrWeek;
    private String mStrWenes;
    /* access modifiers changed from: private */
    public Runnable mTicker;
    /* access modifiers changed from: private */
    public boolean mTickerStopped;
    private int mWeek;
    private int mWeekColor;
    private Drawable mWeekHand;
    private Postion mWeekPostion;
    private int mWeekSize;

    public MyAnalogClock(Context context) {
        super(context);
    }


    class Postion {

        /* renamed from: x */
        public int f4x;

        /* renamed from: y */
        public int f5y;

        public Postion(int x, int y) {
            this.f4x = x;
            this.f5y = y;
        }
    }

    private final class Receiver extends BroadcastReceiver {
        /* synthetic */ MyAnalogClock this$0;

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                int level = intent.getIntExtra("level", -1);
                if (level >= 0) {
                    this.this$0.mBatteryLevel = level;
                }
                this.this$0.mBatteryLevel = intent.getIntExtra("level", -1);
                this.this$0.mChanged = true;
                return;
            }
            if (action.equals("com.sinsoft.action.health.step_count")) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void onTimeChanged() {
        this.mCalendar.setToNow();
        int hour = this.mCalendar.hour;
        int minute = this.mCalendar.minute;
        int second = this.mCalendar.second;
        this.mStrDate = String.valueOf(this.mCalendar.monthDay);
        this.mStrToday = String.valueOf(this.mCalendar.monthDay);
        this.mStrWeek = getWeek(this.mCalendar.weekDay);
        this.mMonth = this.mCalendar.month + 1;
        this.mWeek = this.mCalendar.weekDay;
        this.mDate = this.mCalendar.monthDay;
        this.mSecond = ((float) second) + (((float) (System.currentTimeMillis() % 1000)) / 1000.0f);
        this.mMinutes = ((float) minute) + (((float) second) / 60.0f);
        this.mHour = ((float) hour) + (this.mMinutes / 60.0f) + (this.mSecond / 3600.0f);
        this.mStrMonth = String.valueOf(this.mCalendar.month + 1);
        this.mStrHour = getFormatHMS(hour);
        this.mStrMinute = getFormatHMS(minute);
        this.mStrSecond = getFormatHMS(second);
        this.mChanged = true;
        updateContentDescription(this.mCalendar);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        this.mTickerStopped = false;
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        filter.addAction("com.sinsoft.action.health.step_count");
        getContext().registerReceiver(this.mReceiver, filter);
        this.mTicker = new Runnable() {
            public void run() {
                if (!MyAnalogClock.this.mTickerStopped) {
                    MyAnalogClock.this.onTimeChanged();
                    MyAnalogClock.this.invalidate();
                    long now = SystemClock.uptimeMillis();
                    MyAnalogClock.this.mHandler.postAtTime(MyAnalogClock.this.mTicker, now + (((long) MyAnalogClock.this.mSecondHandDuring) - (now % ((long) MyAnalogClock.this.mSecondHandDuring))));
                }
            }
        };
        this.mTicker.run();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mTickerStopped = true;
        getContext().unregisterReceiver(this.mReceiver);
    }

    /* access modifiers changed from: protected */
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
        resetScreen(getRight() - getLeft());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int availableWidth = getRight() - getLeft();
        int availableHeight = getBottom() - getTop();
        int centerX = availableWidth / 2;
        int centerY = availableHeight / 2;
        boolean scaled = setScale(canvas, availableWidth, availableHeight);
        drawDial(canvas, centerX, centerY, this.mDial);
        if (this.mHasMonthDial) {
            drawMonth(canvas);
        }
        if (this.mHasDateDial) {
            drawDate(canvas);
        }
        if (this.mHasWeekDial) {
            drawWeek(canvas);
        }
        if (this.mHasBatteryDial) {
            drawBattery(canvas);
        }
        if (this.mHasStepDial) {
            drawStep(canvas);
        }
        if (this.mHasAnalogClock) {
            drawHourHandle(canvas, centerX, centerY);
            drawMinuteHandle(canvas, centerX, centerY);
            drawSecondHandle(canvas, centerX, centerY);
        }
        if (scaled) {
            canvas.restore();
        }
        this.mChanged = false;
    }

    private void resetScreen(int availableWidth) {
        this.SCREEN_WIDE = availableWidth;
    }

    private void drawDial(Canvas canvas, int x, int y, Drawable dial) {
        if (dial != null) {
            int w = dial.getIntrinsicWidth();
            int h = dial.getIntrinsicHeight();
            canvas.save();
            if (this.mChanged) {
                dial.setBounds(x - (w / 2), y - (h / 2), (w / 2) + x, (h / 2) + y);
            }
            dial.draw(canvas);
            canvas.restore();
        }
    }

    private boolean setScale(Canvas canvas, int availableWidth, int availableHeight) {
        Drawable dial = this.mDial;
        int x = availableWidth / 2;
        int y = availableHeight / 2;
        if (dial == null) {
            return false;
        }
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();
        int i = h;
        if (availableWidth < w || availableHeight < h) {
            float scale = Math.min(((float) availableWidth) / ((float) w), ((float) availableHeight) / ((float) h));
            canvas.save();
            canvas.scale(scale, scale, (float) x, (float) y);
            return true;
        } else if (availableWidth <= w || availableHeight <= h) {
            return false;
        } else {
            float scale2 = Math.min(((float) availableWidth) / ((float) w), ((float) availableHeight) / ((float) h));
            canvas.save();
            canvas.scale(scale2, scale2, (float) x, (float) y);
            return true;
        }
    }

    private void drawMonth(Canvas canvas) {
        if (this.mChanged && this.mMonthPostion != null) {
            Postion center = this.mMonthPostion;
            int dX = (this.SCREEN_WIDE * center.f4x) / 100;
            int dY = (this.SCREEN_WIDE * center.f5y) / 100;
            if (this.mMonthSize > 0) {
                this.mPaint.setTextSize((float) this.mMonthSize);
                this.mPaint.setColor(this.mMonthColor);
                if (this.mLittleMonthDial != null) {
                    drawDial(canvas, dX, dY, this.mLittleMonthDial);
                }
                Postion pos = getLeftTopPostion(this.mPaint, this.mMonthPostion, this.mStrMonth);
                canvas.drawText(this.mStrMonth, (float) pos.f4x, (float) pos.f5y, this.mPaint);
            }
            if (this.mMonthHand != null) {
                if (this.mMonthDial != null) {
                    drawDial(canvas, dX, dY, this.mMonthDial);
                }
                canvas.save();
                canvas.rotate((((float) this.mMonth) / 12.0f) * 360.0f, (float) dX, (float) dY);
                Drawable hand = this.mMonthHand;
                int w = hand.getIntrinsicWidth();
                int h = hand.getIntrinsicHeight();
                hand.setBounds(dX - (w / 2), dY - (h / 2), (w / 2) + dX, (h / 2) + dY);
                hand.draw(canvas);
                canvas.restore();
            }
        }
    }

    private void drawDate(Canvas canvas) {
        if (this.mChanged && this.mDatePostion != null) {
            Postion center = this.mDatePostion;
            int dX = (this.SCREEN_WIDE * center.f4x) / 100;
            int dY = (this.SCREEN_WIDE * center.f5y) / 100;
            if (this.mDateSize > 0) {
                this.mPaint.setTextSize((float) this.mDateSize);
                this.mPaint.setColor(this.mDateColor);
                if (this.mLittleDateDial != null) {
                    drawDial(canvas, dX, dY, this.mLittleDateDial);
                }
                Postion pos = getLeftTopPostion(this.mPaint, this.mDatePostion, this.mStrDate);
                canvas.drawText(this.mStrDate, (float) pos.f4x, (float) pos.f5y, this.mPaint);
            }
            if (this.mDateHand != null) {
                if (this.mDateDial != null) {
                    drawDial(canvas, dX, dY, this.mDateDial);
                }
                canvas.save();
                canvas.rotate(Math.min((((float) this.mDate) / 30.0f) * 360.0f, 360.0f), (float) dX, (float) dY);
                Drawable hand = this.mDateHand;
                int w = hand.getIntrinsicWidth();
                int h = hand.getIntrinsicHeight();
                hand.setBounds(dX - (w / 2), dY - (h / 2), (w / 2) + dX, (h / 2) + dY);
                hand.draw(canvas);
                canvas.restore();
            }
        }
    }

    private void drawWeek(Canvas canvas) {
        if (this.mChanged && this.mWeekPostion != null) {
            Postion center = this.mWeekPostion;
            int dX = (this.SCREEN_WIDE * center.f4x) / 100;
            int dY = (this.SCREEN_WIDE * center.f5y) / 100;
            if (this.mWeekSize > 0) {
                this.mPaint.setTextSize((float) this.mWeekSize);
                this.mPaint.setColor(this.mWeekColor);
                if (this.mLittleWeekDial != null) {
                    drawDial(canvas, dX, dY, this.mLittleWeekDial);
                }
                Postion pos = getLeftTopPostion(this.mPaint, this.mWeekPostion, this.mStrWeek);
                canvas.drawText(this.mStrWeek, (float) pos.f4x, (float) pos.f5y, this.mPaint);
            }
            if (this.mWeekHand != null) {
                canvas.save();
                canvas.rotate(Math.min((((float) (((this.mWeek - 1) + 7) % 7)) / 7.0f) * 360.0f, 360.0f), (float) dX, (float) dY);
                Drawable hand = this.mWeekHand;
                int w = hand.getIntrinsicWidth();
                int h = hand.getIntrinsicHeight();
                hand.setBounds(dX - (w / 2), dY - (h / 2), (w / 2) + dX, (h / 2) + dY);
                hand.draw(canvas);
                canvas.restore();
            }
        }
    }

    private void drawBattery(Canvas canvas) {
        if (this.mChanged && this.mBatteryLevel >= 0 && this.mBatteryPostion != null) {
            Postion center = this.mBatteryPostion;
            int dX = (this.SCREEN_WIDE * center.f4x) / 100;
            int dY = (this.SCREEN_WIDE * center.f5y) / 100;
            if (this.mBatterySize > 0) {
                this.mPaint.setTextSize((float) this.mBatterySize);
                this.mPaint.setColor(this.mBatteryColor);
                this.mStrBattery = this.mBatteryLevel + "%";
                Postion pos = getLeftTopPostion(this.mPaint, this.mBatteryPostion, this.mStrBattery);
                canvas.drawText(this.mStrBattery, (float) pos.f4x, (float) pos.f5y, this.mPaint);
            }
            if (this.mBatteryHand != null) {
                canvas.save();
                canvas.rotate(Math.min((((float) this.mBatteryLevel) / 100.0f) * 360.0f, 360.0f), (float) dX, (float) dY);
                Drawable hand = this.mBatteryHand;
                int w = hand.getIntrinsicWidth();
                int h = hand.getIntrinsicHeight();
                hand.setBounds(dX - (w / 2), dY - (h / 2), (w / 2) + dX, (h / 2) + dY);
                hand.draw(canvas);
                canvas.restore();
            }
            if (this.mBatteryRadius > 0 && this.mBatteryBorder > 0) {
                this.mRectF.left = (float) (dX - this.mBatteryRadius);
                this.mRectF.top = (float) (dY - this.mBatteryRadius);
                this.mRectF.right = (float) (this.mBatteryRadius + dX);
                this.mRectF.bottom = (float) (this.mBatteryRadius + dY);
                this.mPaintBattery.setStyle(Style.STROKE);
                canvas.drawArc(this.mRectF, -90.0f, (((float) this.mBatteryLevel) / 100.0f) * 360.0f, false, this.mPaintBattery);
            }
        }
    }

    private void drawStep(Canvas canvas) {
        this.mStep = WatchApp.getSteps(getContext());
        this.mStrStep = Integer.toString(this.mStep);
        this.mStepsTarget = WatchApp.getTargetSteps(getContext());
        if (this.mChanged && this.mStepPostion != null) {
            Postion center = this.mStepPostion;
            int dX = (this.SCREEN_WIDE * center.f4x) / 100;
            int dY = (this.SCREEN_WIDE * center.f5y) / 100;
            if (this.mStepSize > 0) {
                this.mPaint.setTextSize((float) this.mStepSize);
                this.mPaint.setColor(this.mStepColor);
                Postion pos = getLeftTopPostion(this.mPaint, this.mStepPostion, this.mStrStep);
                canvas.drawText(this.mStrStep, (float) pos.f4x, (float) pos.f5y, this.mPaint);
            }
            if (this.mStepHand != null) {
                canvas.save();
                canvas.rotate(Math.min((((float) this.mStep) * 360.0f) / ((float) this.mStepsTarget), 360.0f), (float) dX, (float) dY);
                Drawable hand = this.mStepHand;
                int w = hand.getIntrinsicWidth();
                int h = hand.getIntrinsicHeight();
                hand.setBounds(dX - (w / 2), dY - (h / 2), (w / 2) + dX, (h / 2) + dY);
                hand.draw(canvas);
                canvas.restore();
            }
            if (this.mStepRadius > 0 && this.mStepBorder > 0) {
                this.mRectF.left = (float) (dX - this.mStepRadius);
                this.mRectF.top = (float) (dY - this.mStepRadius);
                this.mRectF.right = (float) (this.mStepRadius + dX);
                this.mRectF.bottom = (float) (this.mStepRadius + dY);
                this.mPaintStep.setStyle(Style.STROKE);
                canvas.drawArc(this.mRectF, -90.0f, Math.min((((float) this.mStep) * 360.0f) / ((float) this.mStepsTarget), 360.0f), false, this.mPaintStep);
            }
        }
    }

    private void drawHourHandle(Canvas canvas, int x, int y) {
        if (this.mHourHand != null) {
            canvas.save();
            canvas.rotate((this.mHour / 12.0f) * 360.0f, (float) x, (float) y);
            Drawable hourHand = this.mHourHand;
            if (this.mChanged) {
                int w = hourHand.getIntrinsicWidth();
                int h = hourHand.getIntrinsicHeight();
                hourHand.setBounds(x - (w / 2), y - (h / 2), (w / 2) + x, (h / 2) + y);
            }
            hourHand.draw(canvas);
            canvas.restore();
        }
    }

    private void drawMinuteHandle(Canvas canvas, int x, int y) {
        if (this.mMinuteHand != null) {
            canvas.save();
            canvas.rotate((this.mMinutes / 60.0f) * 360.0f, (float) x, (float) y);
            Drawable minuteHand = this.mMinuteHand;
            if (this.mChanged) {
                int w = minuteHand.getIntrinsicWidth();
                int h = minuteHand.getIntrinsicHeight();
                minuteHand.setBounds(x - (w / 2), y - (h / 2), (w / 2) + x, (h / 2) + y);
            }
            minuteHand.draw(canvas);
            canvas.restore();
        }
    }

    private void drawSecondHandle(Canvas canvas, int x, int y) {
        if (this.mSecondHand != null) {
            canvas.save();
            canvas.rotate((this.mSecond / 60.0f) * 360.0f, (float) x, (float) y);
            Drawable secondHand = this.mSecondHand;
            if (this.mChanged) {
                int w = secondHand.getIntrinsicWidth();
                int h = secondHand.getIntrinsicHeight();
                secondHand.setBounds(x - (w / 2), y - (h / 2), (w / 2) + x, (h / 2) + y);
            }
            secondHand.draw(canvas);
            canvas.restore();
        }
    }

    private void updateContentDescription(Time time) {
        setContentDescription(DateUtils.formatDateTime(getContext(), time.toMillis(false), 129));
    }

    private String getWeek(int week) {
        switch (week) {
            case 0:
                return this.mStrSun;
            case 1:
                return this.mStrMon;
            case 2:
                return this.mStrTues;
            case 3:
                return this.mStrWenes;
            case 4:
                return this.mStrThurs;
            case 5:
                return this.mStrFri;
            case 6:
                return this.mStrSatur;
            default:
                return "";
        }
    }

    private Postion getLeftTopPostion(Paint paint, Postion pos, String str) {
        paint.getTextBounds(str, 0, str.length(), this.mRect);
        return new Postion(((this.SCREEN_WIDE * pos.f4x) / 100) - (this.mRect.width() / 2), ((this.SCREEN_WIDE * pos.f5y) / 100) + (this.mRect.height() / 2));
    }

    private String getFormatHMS(int t) {
        if (t < 10) {
            return "0" + t;
        }
        return "" + t;
    }
}

